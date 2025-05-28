package com.example.swop.data.remote.models;

import java.math.BigDecimal;

public class OrderDetailDto {
    private Long id;
    private Long order;
    private Long product;
    private BigDecimal price;
    private String sku;
    private Integer quantity;

    public OrderDetailDto() {
    }

    public OrderDetailDto(Long id, Long order, Long product, BigDecimal price, String sku, Integer quantity) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.price = price;
        this.sku = sku;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
