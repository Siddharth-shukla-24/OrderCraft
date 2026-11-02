package com.ordercraft;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordercraft.dto.ItemRequestDto;
import com.ordercraft.dto.OrderCreateRequestDto;
import com.ordercraft.dto.PaymentRequestDto;
import com.ordercraft.dto.SupplierRequestDto;
import com.ordercraft.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderCraftApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", is("ordercraft-backend")));
    }

    @Test
    void testCustomerLookup() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].customer_name", notNullValue()));
    }

    @Test
    void testDashboardStats() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_orders", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.status_breakdown", hasSize(4)))
                .andExpect(jsonPath("$.active_suppliers", greaterThanOrEqualTo(0)));
    }

    @Test
    void testOrderBillingPaymentFlow() throws Exception {
        // 1. Create order
        OrderCreateRequestDto orderReq = new OrderCreateRequestDto(
                1L,
                LocalDate.now().plusDays(14),
                2L,
                List.of(
                        new ItemRequestDto(null, "E2E-1", "E2E Test Line", new BigDecimal("2"), new BigDecimal("1500.00"))
                )
        );

        String createOrderJson = objectMapper.writeValueAsString(orderReq);
        String orderRes = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOrderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.order_id", notNullValue()))
                .andExpect(jsonPath("$.total_amount", is(3000.0)))
                .andReturn().getResponse().getContentAsString();

        Number orderIdNum = com.jayway.jsonpath.JsonPath.read(orderRes, "$.order_id");
        Long orderId = orderIdNum.longValue();

        // 2. Generate bill
        String billRes = mockMvc.perform(post("/api/orders/" + orderId + "/bill"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bill_id", notNullValue()))
                .andExpect(jsonPath("$.bill_number", startsWith("BILL-")))
                .andExpect(jsonPath("$.tax_amount", is(0.0)))
                .andExpect(jsonPath("$.total_amount", is(3000.0)))
                .andReturn().getResponse().getContentAsString();

        Number billIdNum = com.jayway.jsonpath.JsonPath.read(billRes, "$.bill_id");
        Long billId = billIdNum.longValue();

        // 3. Duplicate bill generation must fail (1 bill per order)
        mockMvc.perform(post("/api/orders/" + orderId + "/bill"))
                .andExpect(status().isBadRequest());

        // 4. Record partial payment
        PaymentRequestDto p1Req = new PaymentRequestDto(
                billId,
                LocalDate.now(),
                new BigDecimal("1000.00"),
                "Bank transfer",
                "TXN-101"
        );
        mockMvc.perform(post("/api/bills/" + billId + "/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p1Req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.payment_id", notNullValue()))
                .andExpect(jsonPath("$.amount", is(1000.0)));

        // 5. Check tracking detail
        mockMvc.perform(get("/api/tracking/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paid_amount", is(1000.0)))
                .andExpect(jsonPath("$.amount_due", is(2000.0)))
                .andExpect(jsonPath("$.payment_status", is("PARTIALLY PAID")));

        // 6. Overpayment attempt must fail
        PaymentRequestDto overReq = new PaymentRequestDto(
                billId,
                LocalDate.now(),
                new BigDecimal("2000.01"),
                "Cash",
                "TXN-ERR"
        );
        mockMvc.perform(post("/api/bills/" + billId + "/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSupplierApi() throws Exception {
        SupplierRequestDto req = new SupplierRequestDto(
                "Delta Precision Parts",
                "Kavita Verma",
                "kavita@deltaprecision.example.com",
                "+91 98300 33001",
                "Plot 44, Udyog Vihar",
                "ACTIVE"
        );

        String res = mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.supplier_id", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        Number idNum = com.jayway.jsonpath.JsonPath.read(res, "$.supplier_id");
        Long supplierId = idNum.longValue();

        mockMvc.perform(patch("/api/suppliers/" + supplierId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("INACTIVE")));
    }

    @Test
    void testGetAllPayments() throws Exception {
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }
}
