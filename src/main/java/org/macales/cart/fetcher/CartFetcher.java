package org.macales.cart.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.macales.cart.dto.AddItemDTO;
import org.macales.cart.dto.GetCartDTO;
import org.macales.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@DgsComponent
public class CartFetcher {
    private CartService cartService;

    @Autowired
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    @DgsQuery
    public Optional<GetCartDTO> getCart(long userID) {
        return Optional.of(cartService.getCartDTO(userID));
    }

    @DgsMutation
    public boolean addItem(@InputArgument long userID, @InputArgument AddItemDTO addItem) {
        try {
            cartService.addItem(userID, addItem);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @DgsMutation
    public boolean deleteItem(@InputArgument long userID, @InputArgument long sku) {
        try {
            cartService.deleteItem(userID, sku);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @DgsMutation
    public boolean deleteCart(@InputArgument long userID) {
        try {
            cartService.deleteCart(userID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
