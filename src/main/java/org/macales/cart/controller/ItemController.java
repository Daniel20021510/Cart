package org.macales.cart.controller;

import org.macales.cart.dto.ItemProductDTO;
import org.macales.cart.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/item/{ID}")
    public ResponseEntity<EntityModel<ItemProductDTO>> getItem(@PathVariable long ID) {
        try {
            ItemProductDTO item = itemService.getItem(ID);

            Link selfLink = linkTo(methodOn(ItemController.class).getItem(ID)).withSelfRel();
            item.add(selfLink);

            Link addItemLink = linkTo(methodOn(CartController.class).addItem(0, 0, null)).withRel("addItem");
            item.add(addItemLink);

            Link deleteItemLink = linkTo(methodOn(CartController.class).deleteItem(0, 0)).withRel("deleteItem");
            item.add(deleteItemLink);

            return ResponseEntity.ok(EntityModel.of(item));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }
}
