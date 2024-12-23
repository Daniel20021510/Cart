package org.macales.cart.service;

import org.macales.cart.dto.AddItemDTO;
import org.macales.cart.dto.CartDTO;
import org.macales.cart.dto.GetCartDTO;

public interface CartService {
    void addItem(long userID, AddItemDTO addItemDTO);
    void deleteItem(long userID, long sku);
    void deleteCart(long userID);
    GetCartDTO getCartDTO(long userID);
    CartDTO getCart(long userID);
    void checkout(long userID);
}
