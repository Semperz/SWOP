package com.example.swop.shop.cartwindow;

import com.example.swop.data.remote.models.ProductDto;

//Clase auxiliar para representar un producto en el carrito de compras y su cantidad
public class CartItem {
    public final ProductDto product;
    public final int quantity;

    public CartItem(ProductDto product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ProductDto getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}
