package org.mourya.msscbeerinventoryservice.web.mapper;

import org.mapstruct.Mapper;
import org.mourya.msscbeerinventoryservice.domain.BeerInventory;
import org.mourya.brewery.model.BeerInventoryDto;

@Mapper(uses = {DateMapper.class})
public interface BeerInventoryMapper {

    BeerInventory beerInventoryDtoToBeerInventory(BeerInventoryDto beerInventoryDTO);

    BeerInventoryDto beerInventoryToBeerInventoryDto(BeerInventory beerInventory);
}
