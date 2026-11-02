package com.ordercraft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SupplierRequestDto {

    @NotBlank(message = "Supplier name is required")
    @JsonProperty("supplier_name")
    private String supplierName;

    @NotBlank(message = "Contact person is required")
    @JsonProperty("contact_person")
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Phone is required")
    @JsonProperty("phone")
    private String phone;

    @JsonProperty("address")
    private String address;

    @JsonProperty("status")
    private String status;

    public SupplierRequestDto() {}

    public SupplierRequestDto(String supplierName, String contactPerson, String email, String phone, String address, String status) {
        this.supplierName = supplierName;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.status = status;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
