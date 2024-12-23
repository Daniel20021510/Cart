package org.macales.cart.controller;

import org.macales.cart.dto.AddItemDTO;
import org.macales.cart.dto.CartDTO;
import org.macales.cart.model.Item;
import org.macales.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/user/{userID}/cart/{skuID}")
    public ResponseEntity<Void> addItem(@PathVariable long userID, @PathVariable long skuID, @RequestBody AddItemDTO addItemDTO) {
        if (skuID <= 0 || addItemDTO.getCount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid SKU or count");
        }

        addItemDTO.setSku(skuID);

        try {
            cartService.addItem(userID, addItemDTO);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @DeleteMapping("/user/{userID}/cart/{skuID}")
    public ResponseEntity<Void> deleteItem(@PathVariable long userID, @PathVariable long skuID) {
        if (skuID <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid SKU");
        }

        try {
            cartService.deleteItem(userID, skuID);

            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @DeleteMapping("/user/{userID}/cart")
    public ResponseEntity<Void> deleteCart(@PathVariable long userID) {
        try {
            cartService.deleteCart(userID);

            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @GetMapping("/user/{userID}/cart")
    public ResponseEntity<EntityModel<Map<String, Object>>> getCart(@PathVariable long userID) {
        try {
            Map<String, Object> cartData = new HashMap<>();

            CartDTO cart = cartService.getCart(userID);

            List<Link> itemsLinks = new ArrayList<>();
            for (Item item : cart.getItems()) {
                Link itemLink = WebMvcLinkBuilder.linkTo(methodOn(ItemController.class).getItem(item.getID())).withRel("item");
                itemsLinks.add(itemLink);
            }

            cartData.put("items", itemsLinks);
            cartData.put("totalPrice", cart.getTotalPrice());

            Map<String, Map<String, Object>> linkMap = new HashMap<>();

            Map<String, Object> selfLink = new HashMap<>();
            selfLink.put("href", linkTo(methodOn(CartController.class).getCart(userID)).withSelfRel().getHref());
            selfLink.put("method", "GET");
            linkMap.put("self", selfLink);

            cartData.put("_links", linkMap);

            Map<String, Map<String, Object>> actionMap = new HashMap<>();

            Map<String, Object> addItemAction = new HashMap<>();
            addItemAction.put("href", linkTo(methodOn(CartController.class).addItem(userID, 0, null)).withRel("addItem").getHref());
            addItemAction.put("method", "POST");
            actionMap.put("addItem", addItemAction);

            Map<String, Object> deleteItemAction = new HashMap<>();
            deleteItemAction.put("href", linkTo(methodOn(CartController.class).deleteItem(userID, 0)).withRel("deleteItem").getHref());
            deleteItemAction.put("method", "DELETE");
            actionMap.put("deleteItem", deleteItemAction);

            Map<String, Object> deleteCartAction = new HashMap<>();
            deleteCartAction.put("href", linkTo(methodOn(CartController.class).deleteCart(userID)).withRel("deleteCart").getHref());
            deleteCartAction.put("method", "DELETE");
            actionMap.put("deleteCart", deleteCartAction);

            cartData.put("actions", actionMap);

            return ResponseEntity.ok(EntityModel.of(cartData));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @PostMapping("/user/{userID}/cart/checkout")
    public ResponseEntity<Void> checkout(@PathVariable long userID) {
        try {
            cartService.checkout(userID);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }
}
