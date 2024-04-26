package org.mourya.msscbeerinventoryservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mourya.brewery.model.BeerOrderDto;
import org.mourya.brewery.model.BeerOrderLineDto;
import org.mourya.msscbeerinventoryservice.domain.BeerInventory;
import org.mourya.msscbeerinventoryservice.respositories.BeerInventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class AllocationServiceImpl implements AllocationService {

    private final BeerInventoryRepository beerInventoryRepository;

    @Override
    public Boolean allocateOrder(BeerOrderDto beerOrderDto) {

        log.info("Allocating OrderId : " + beerOrderDto.getId());
        AtomicInteger totalOrder = new AtomicInteger();
        AtomicInteger totalAllocated = new AtomicInteger();
        beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
            if(((beerOrderLineDto.getOrderQuantity() != null ? beerOrderLineDto.getOrderQuantity(): 0)
                    - (beerOrderLineDto.getQuantityAllocated() != null ? beerOrderLineDto.getQuantityAllocated() :0 )) > 0){
               allocateBeerOrderLine(beerOrderLineDto);
            }
            totalOrder.set(totalAllocated.get() + beerOrderLineDto.getOrderQuantity());
            totalAllocated.set(totalAllocated.get() + (beerOrderLineDto.getQuantityAllocated() != null ? beerOrderLineDto.getQuantityAllocated() : 0));
        });
        log.info(("Total Ordered: " + totalOrder.get() + " Total Allocated: " + totalAllocated.get()));
        return totalOrder.get() == totalAllocated.get();
    }

    @Override
    public void deallocateOrder(BeerOrderDto beerOrderDto) {
        beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
            BeerInventory beerInventory = BeerInventory.builder()
                    .beerId(beerOrderLineDto.getBeerId())
                    .upc(beerOrderLineDto.getUpc())
                    .quantityOnHand(beerOrderLineDto.getQuantityAllocated())
                    .build();

            BeerInventory savedBeerInventory = beerInventoryRepository.save(beerInventory);

            log.debug("Saved inventory for beer upc: " + savedBeerInventory.getUpc() + " inventory id: " + savedBeerInventory.getId());
        });
    }

    private void allocateBeerOrderLine(BeerOrderLineDto beerOrderLineDto) {
        List<BeerInventory> beerInventoryList = beerInventoryRepository.findAllByUpc(beerOrderLineDto.getUpc());

        beerInventoryList.forEach(beerInventory -> {
            int inventory = (beerInventory.getQuantityOnHand() == null ? 0 : beerInventory.getQuantityOnHand());
            int orderQty = (beerOrderLineDto.getOrderQuantity() == null ? 0 : beerOrderLineDto.getOrderQuantity());
            int allocatedQty = (beerOrderLineDto.getQuantityAllocated() == null ? 0 : beerOrderLineDto.getQuantityAllocated());
            int qtyToAllocate = orderQty-allocatedQty;
            if(inventory >= qtyToAllocate){ // full allocation
                inventory = inventory - qtyToAllocate;
                beerOrderLineDto.setQuantityAllocated(orderQty);
                beerInventory.setQuantityOnHand(inventory);

                beerInventoryRepository.save(beerInventory);
            }else if(inventory > 0){ //partial allocation
                beerOrderLineDto.setQuantityAllocated(allocatedQty+inventory);
                beerInventory.setQuantityOnHand(0);
            }

            if(beerInventory.getQuantityOnHand() == 0){
                beerInventoryRepository.delete(beerInventory);
            }

        });
    }
}
