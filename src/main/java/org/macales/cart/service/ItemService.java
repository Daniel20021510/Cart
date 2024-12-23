package org.macales.cart.service;

import org.macales.cart.dto.ItemProductDTO;

public interface ItemService {
    ItemProductDTO getItem(long id);
}
