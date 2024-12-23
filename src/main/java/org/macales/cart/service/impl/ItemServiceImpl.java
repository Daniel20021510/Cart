package org.macales.cart.service.impl;

import org.macales.cart.client.ProductClient;
import org.macales.cart.dto.ItemProductDTO;
import org.macales.cart.model.Item;
import org.macales.cart.model.Product;
import org.macales.cart.repository.ItemRepository;
import org.macales.cart.service.ItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private ProductClient productClient;
    private ModelMapper modelMapper;

    @Autowired
    public void setCartRepository(ItemRepository  itemRepository) {
        this.itemRepository= itemRepository;
    }

    @Autowired
    public void setProductClient(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ItemProductDTO getItem(long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        ItemProductDTO itemProductDTO = modelMapper.map(item, ItemProductDTO.class);
        try {
            Product product = productClient.getProduct(itemProductDTO.getSku());
            itemProductDTO.setProduct(product);
            return itemProductDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
