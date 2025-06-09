package com.example.swop.data.remote.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class OrderDto {
    private Long id;
    private CustomerDto customer;
    private Set<OrderDetailDto> orderDetails;
    private BigDecimal amount;
    private String shippingAddress;
    private String orderAddress;
    private String orderEmail;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;
    private LocalDateTime orderDate;

    private boolean fromAuction;

    public OrderDto() {
    }

    public OrderDto(Long id, CustomerDto customer, Set<OrderDetailDto> orderDetails,BigDecimal amount, String shippingAddress, String orderAddress, String orderEmail, OrderStatus orderStatus, PaymentMethod paymentMethod, LocalDateTime orderDate, boolean fromAuction) {
        this.id = id;
        this.customer = customer;
        this.orderDetails = orderDetails;
        this.amount = amount;
        this.shippingAddress = shippingAddress;
        this.orderAddress = orderAddress;
        this.orderEmail = orderEmail;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
        this.fromAuction = fromAuction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDto customer) {
        this.customer = customer;
    }

    public Set<OrderDetailDto> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(Set<OrderDetailDto> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderEmail() {
        return orderEmail;
    }

    public void setOrderEmail(String orderEmail) {
        this.orderEmail = orderEmail;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public boolean isFromAuction() {
        return fromAuction;
    }

    public void setFromAuction(boolean fromAuction) {
        this.fromAuction = fromAuction;
    }
}
