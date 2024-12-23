package org.macales.cart.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.macales.cart.client.ProductClient;
import org.macales.cart.dto.*;
import org.macales.cart.model.Cart;
import org.macales.cart.model.Item;
import org.macales.cart.model.Product;
import org.macales.cart.repository.CartRepository;
import org.macales.cart.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    private CartRepository cartRepository;
    private ProductClient productClient;
    private ModelMapper modelMapper;
    private RabbitTemplate rabbitTemplate;

    static final String exchangeName = "cartCheckoutExchange";

    @Autowired
    public void setCartRepository(CartRepository  cartRepository) {
        this.cartRepository= cartRepository;
    }

    @Autowired
    public void setProductClient(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public void addItem(long userID, AddItemDTO addItemDTO) {
        try {
            productClient.getProduct(addItemDTO.getSku());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        Cart cart = cartRepository.findById(userID).orElseGet(() -> {
            Cart newCart = new Cart(userID);
            return cartRepository.save(newCart);
        });

        Optional<Item> existingItem = cart.getItems().stream()
                .filter(item -> item.getSku() == addItemDTO.getSku())
                .findFirst();

        if (existingItem.isPresent()) {
            Item item = existingItem.get();
            item.setCount(item.getCount() + addItemDTO.getCount());
        } else {
            Item item = modelMapper.map(addItemDTO, Item.class);
            item.setCart(cart);
            cart.getItems().add(item);
        }

        cartRepository.save(cart);
    }

    @Override
    public void deleteItem(long userID, long sku) {
        Optional<Cart> cartOptional = cartRepository.findById(userID);

        cartOptional.ifPresent(cart -> {
            cart.getItems().removeIf(item -> item.getSku() == sku);
            cartRepository.save(cart);
        });
    }

    @Override
    public void deleteCart(long userID) {
        Cart cart = cartRepository.findById(userID).orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        cart.getItems().clear();

        cartRepository.save(cart);

    }

    @Override
    public GetCartDTO getCartDTO(long userID) {
        Cart cart = cartRepository.findById(userID).orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<ItemProductDTO> itemProductDTOS = cart.getItems().stream()
                .map(item -> {
                    ItemProductDTO itemProductDTO = modelMapper.map(item, ItemProductDTO.class);
                    try {
                        Product product = productClient.getProduct(itemProductDTO.getSku());
                        itemProductDTO.setProduct(product);
                        return itemProductDTO;
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }).collect(Collectors.toList());

        GetCartDTO getCartDTO = new GetCartDTO();
        getCartDTO.setItems(itemProductDTOS);
        getCartDTO.setTotalPrice(
                itemProductDTOS.stream().map(itemDTO -> {
            return itemDTO.getProduct().getPrice() * itemDTO.getCount();
        }).reduce(0L, Long::sum));

        return getCartDTO;
    }

    @Override
    public CartDTO getCart(long userID) {
        Cart cart = cartRepository.findById(userID).orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cartDTO.setTotalPrice(
                cart.getItems().stream().map(item -> {
                    try {
                        Product product = productClient.getProduct(item.getSku());
                        return product.getPrice() * item.getCount();
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }).reduce(0L, Long::sum));

        return cartDTO;
    }

    @Override
    public void checkout(long userID) {
        Cart cart = cartRepository.findById(userID).orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<ItemDTO> itemDTOS = cart.getItems().stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .toList();

        ObjectMapper objectMapper = new ObjectMapper();
        String itemsJson;
        try {
            itemsJson = objectMapper.writeValueAsString(itemDTOS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert items to JSON", e);
        }

        MessagePostProcessor messagePostProcessor = message -> {
            MessageProperties properties = message.getMessageProperties();
            properties.setHeader("userID", userID);
            return message;
        };

        rabbitTemplate.convertAndSend(exchangeName, "cart.checkout", itemsJson, messagePostProcessor);
    }
}
