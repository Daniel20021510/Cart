package org.macales.cart.dto;

import org.macales.cart.model.Item;

import java.util.List;

public class CartDTO {
    private List<Item> items;

    private long totalPrice;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }
}
