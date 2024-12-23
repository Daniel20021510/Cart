package org.macales.cart.dto;

import java.util.List;

public class GetCartDTO {
    private List<ItemProductDTO> items;

    private long totalPrice;

    public List<ItemProductDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemProductDTO> items) {
        this.items = items;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }
}
