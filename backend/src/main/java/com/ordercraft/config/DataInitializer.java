package com.ordercraft.config;

import com.ordercraft.entity.*;
import com.ordercraft.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerOrderRepository orderRepository;
    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           CustomerRepository customerRepository,
                           SupplierRepository supplierRepository,
                           CustomerOrderRepository orderRepository,
                           BillRepository billRepository,
                           PaymentRepository paymentRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.supplierRepository = supplierRepository;
        this.orderRepository = orderRepository;
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            return;
        }

        // Roles
        Role r1 = roleRepository.save(new Role(null, "Administrator", "Manage user access, roles and basic configuration."));
        Role r2 = roleRepository.save(new Role(null, "Order operator", "Create, validate, update and view customer orders."));
        Role r3 = roleRepository.save(new Role(null, "Billing / finance user", "Generate bills and record or review payment entries."));
        Role r4 = roleRepository.save(new Role(null, "Supplier manager", "Maintain supplier records and status."));

        // Users
        User u1 = userRepository.save(new User(null, r1, "Aarav Mehta", "aarav.mehta@example.com", "mock-hash", "ACTIVE"));
        User u2 = userRepository.save(new User(null, r2, "Neha Verma", "neha.verma@example.com", "mock-hash", "ACTIVE"));
        User u3 = userRepository.save(new User(null, r3, "Rohit Sinha", "rohit.sinha@example.com", "mock-hash", "ACTIVE"));
        User u4 = userRepository.save(new User(null, r4, "Pooja Nair", "pooja.nair@example.com", "mock-hash", "ACTIVE"));

        // Customers
        Customer c1 = customerRepository.save(new Customer(null, "Apex Fabrications", "orders@apexfab.example.com", "+91 98100 11001", "Okhla Industrial Area, New Delhi", "ACTIVE"));
        Customer c2 = customerRepository.save(new Customer(null, "Sharma Auto Parts", "accounts@sharmaauto.example.com", "+91 98100 11002", "Sector 63, Noida", "ACTIVE"));
        Customer c3 = customerRepository.save(new Customer(null, "Bharat Steelworks", "billing@bharatsteel.example.com", "+91 98100 11003", "Sahibabad Industrial Area, Ghaziabad", "ACTIVE"));
        Customer c4 = customerRepository.save(new Customer(null, "Kumar Engineering", "info@kumareng.example.com", "+91 98100 11004", "Faridabad, Haryana", "ACTIVE"));
        Customer c5 = customerRepository.save(new Customer(null, "Nova Machine Tools", "purchase@novamt.example.com", "+91 98100 11005", "Manesar, Gurugram", "ACTIVE"));
        Customer c6 = customerRepository.save(new Customer(null, "Delhi Precision Co.", "sales@delhiprecision.example.com", "+91 98100 11006", "Mayapuri, New Delhi", "ACTIVE"));
        Customer c7 = customerRepository.save(new Customer(null, "Gupta Industries", "gupta@guptaind.example.com", "+91 98100 11007", "Bahadurgarh, Haryana", "ACTIVE"));
        Customer c8 = customerRepository.save(new Customer(null, "Rathi Components", "rathi@rathicomp.example.com", "+91 98100 11008", "Greater Noida", "ACTIVE"));

        // Suppliers (independent)
        supplierRepository.save(new Supplier(null, "Hindustan Alloys", "Vikram Anand", "vikram@hindalloys.example.com", "+91 98200 22001", "Sector 5, Bhiwadi", "ACTIVE"));
        supplierRepository.save(new Supplier(null, "Metro Fasteners", "Sunita Rao", "sunita@metrofast.example.com", "+91 98200 22002", "Wazirpur, New Delhi", "ACTIVE"));
        supplierRepository.save(new Supplier(null, "Shakti Castings", "Manoj Tiwari", "manoj@shakticast.example.com", "+91 98200 22003", "Ballabgarh, Haryana", "ACTIVE"));
        supplierRepository.save(new Supplier(null, "Prime Bearings", "Anita Joshi", "anita@primebear.example.com", "+91 98200 22004", "Loni, Ghaziabad", "ACTIVE"));
        supplierRepository.save(new Supplier(null, "Omega Packaging", "Karan Malhotra", "karan@omegapack.example.com", "+91 98200 22005", "Kundli, Sonipat", "ACTIVE"));
        supplierRepository.save(new Supplier(null, "Vishal Coatings", "Deepak Chauhan", "deepak@vishalcoat.example.com", "+91 98200 22006", "Dadri, Greater Noida", "INACTIVE"));

        // Helper to add order with items
        CustomerOrder o1 = createOrder(c8, u2, LocalDate.parse("2026-09-15"), LocalDate.parse("2026-10-06"), OrderStatus.COMPLETED, List.of(
                new ItemSeed("GS-250", "Gear shaft 250 mm", 40, 1200),
                new ItemSeed("BS-06", "Bearing set", 20, 1420)
        ));
        CustomerOrder o2 = createOrder(c7, u2, LocalDate.parse("2026-09-20"), LocalDate.parse("2026-10-30"), OrderStatus.NEW, List.of(
                new ItemSeed("FL-80", "Flange plate", 60, 1500),
                new ItemSeed("BK-15", "Mounting bracket", 100, 399)
        ));
        CustomerOrder o3 = createOrder(c6, u2, LocalDate.parse("2026-09-25"), LocalDate.parse("2026-10-16"), OrderStatus.COMPLETED, List.of(
                new ItemSeed("PN-40", "Precision pin", 200, 271)
        ));
        CustomerOrder o4 = createOrder(c5, u2, LocalDate.parse("2026-09-28"), LocalDate.parse("2026-10-19"), OrderStatus.PROCESSING, List.of(
                new ItemSeed("MT-300", "Machine table base", 5, 39000),
                new ItemSeed("CL-22", "Clamp assembly", 50, 1046)
        ));
        CustomerOrder o5 = createOrder(c4, u2, LocalDate.parse("2026-10-02"), LocalDate.parse("2026-10-23"), OrderStatus.PROCESSING, List.of(
                new ItemSeed("HS-18", "Hydraulic seal kit", 25, 1750),
                new ItemSeed("GK-09", "Gasket pack", 50, 500)
        ));
        CustomerOrder o6 = createOrder(c3, u2, LocalDate.parse("2026-10-04"), LocalDate.parse("2026-10-25"), OrderStatus.COMPLETED, List.of(
                new ItemSeed("ST-500", "Steel rod 500 mm", 300, 750),
                new ItemSeed("ST-120", "Steel plate", 60, 1500)
        ));
        CustomerOrder o7 = createOrder(c2, u2, LocalDate.parse("2026-10-07"), LocalDate.parse("2026-10-28"), OrderStatus.CONFIRMED, List.of(
                new ItemSeed("AP-77", "Axle pin", 100, 425),
                new ItemSeed("BP-31", "Brake plate", 50, 1000)
        ));
        CustomerOrder o8 = createOrder(c1, u2, LocalDate.parse("2026-10-09"), LocalDate.parse("2026-10-30"), OrderStatus.NEW, List.of(
                new ItemSeed("FB-900", "Fabricated frame", 8, 18000),
                new ItemSeed("WP-10", "Weld plate", 40, 1000)
        ));

        // Bills
        Bill b1 = createBill(o1, "BILL-2026-0001", LocalDate.parse("2026-09-16"), BillStatus.ISSUED);
        Bill b2 = createBill(o3, "BILL-2026-0002", LocalDate.parse("2026-09-26"), BillStatus.ISSUED);
        Bill b3 = createBill(o4, "BILL-2026-0003", LocalDate.parse("2026-09-29"), BillStatus.ISSUED);
        Bill b4 = createBill(o5, "BILL-2026-0004", LocalDate.parse("2026-10-03"), BillStatus.GENERATED);
        Bill b5 = createBill(o6, "BILL-2026-0005", LocalDate.parse("2026-10-05"), BillStatus.ISSUED);

        // Payments
        paymentRepository.save(new Payment(null, b1, LocalDate.parse("2026-09-18"), new BigDecimal("76400.00"), "Bank transfer", "REF-0001", "RECORDED"));
        paymentRepository.save(new Payment(null, b2, LocalDate.parse("2026-09-27"), new BigDecimal("54200.00"), "Cheque", "REF-0002", "RECORDED"));
        paymentRepository.save(new Payment(null, b3, LocalDate.parse("2026-10-01"), new BigDecimal("100000.00"), "Bank transfer", "REF-0003", "RECORDED"));
        paymentRepository.save(new Payment(null, b5, LocalDate.parse("2026-10-06"), new BigDecimal("200000.00"), "Bank transfer", "REF-0004", "RECORDED"));
        paymentRepository.save(new Payment(null, b5, LocalDate.parse("2026-10-10"), new BigDecimal("115000.00"), "Cheque", "REF-0005", "RECORDED"));
    }

    private CustomerOrder createOrder(Customer c, User u, LocalDate orderDate, LocalDate reqDate, OrderStatus status, List<ItemSeed> items) {
        CustomerOrder order = new CustomerOrder();
        order.setCustomer(c);
        order.setCreatedBy(u);
        order.setOrderDate(orderDate);
        order.setRequiredDate(reqDate);
        order.setStatus(status);

        BigDecimal total = BigDecimal.ZERO;
        for (ItemSeed s : items) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setItemCode(s.code);
            item.setDescription(s.desc);
            item.setQuantity(new BigDecimal(s.qty));
            item.setUnitPrice(new BigDecimal(s.price).setScale(2));
            BigDecimal lineTotal = item.getQuantity().multiply(item.getUnitPrice()).setScale(2);
            item.setLineTotal(lineTotal);
            total = total.add(lineTotal);
            order.getItems().add(item);
        }
        order.setTotalAmount(total.setScale(2));
        return orderRepository.save(order);
    }

    private Bill createBill(CustomerOrder o, String number, LocalDate date, BillStatus status) {
        Bill b = new Bill();
        b.setOrder(o);
        b.setBillNumber(number);
        b.setBillDate(date);
        b.setSubtotal(o.getTotalAmount());
        b.setTaxAmount(BigDecimal.ZERO.setScale(2));
        b.setTotalAmount(o.getTotalAmount());
        b.setStatus(status);
        return billRepository.save(b);
    }

    private record ItemSeed(String code, String desc, double qty, double price) {}
}
