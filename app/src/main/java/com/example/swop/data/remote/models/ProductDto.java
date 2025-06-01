package com.example.swop.data.remote.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDto {
    private Long id;
    private String sku;
    private String name;
    private BigDecimal price;
    private Double weight;
    private String descriptions;
    private String thumbnail;
    private String image;
    private List<Long> categoryIds;
    private Integer stock;
    private String createDate;

    public ProductDto() {
    }

    public ProductDto(Long id, String sku, String name, BigDecimal price, Double weight, String descriptions, String thumbnail, String image, List<Long> categoryIds, Integer stock, String createDate) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.descriptions = descriptions;
        this.thumbnail = thumbnail;
        this.image = image;
        this.categoryIds = categoryIds;
        this.stock = stock;
        this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
