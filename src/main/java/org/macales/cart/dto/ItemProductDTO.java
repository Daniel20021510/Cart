package org.macales.cart.dto;

import org.macales.cart.model.Product;
import org.springframework.hateoas.RepresentationModel;

public class ItemProductDTO extends RepresentationModel<ItemProductDTO> {
    private long sku;

    private int count;

    private Product product;

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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
