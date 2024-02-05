package org.mourya.msscbeerinventoryservice.services.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mourya.msscbeerinventoryservice.config.JmsConfig;
import org.mourya.brewery.model.events.NewInventoryEvent;
import org.mourya.brewery.model.events.BeerDto;
import org.mourya.msscbeerinventoryservice.domain.BeerInventory;
import org.mourya.msscbeerinventoryservice.respositories.BeerInventoryRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NewInventoryListener {

    private final BeerInventoryRepository beerInventoryRepository;

    @Transactional
    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(@Payload NewInventoryEvent event){

        BeerDto beerDto = event.getBeerDto();
        log.debug("Got Inventory: " + beerDto);

        beerInventoryRepository.save(BeerInventory.builder()
                .beerId(beerDto.getId())
                .upc(beerDto.getUpc())
                .quantityOnHand(beerDto.getQuantityOnHand())
                .build());
    }

}