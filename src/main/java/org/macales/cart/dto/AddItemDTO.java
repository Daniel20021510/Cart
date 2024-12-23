package org.macales.cart.dto;

public class AddItemDTO {
    private long sku;

    private int count;

    public AddItemDTO() {}

    public AddItemDTO(long sku, int count) {
        this.sku = sku;
        this.count = count;
    }

    public long getSku() {
        return sku;
    }

    public void setSku(long sku) {
        this.sku = sku;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
