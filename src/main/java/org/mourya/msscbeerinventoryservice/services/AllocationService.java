package org.mourya.msscbeerinventoryservice.services;

import org.mourya.brewery.model.BeerOrderDto;

public interface AllocationService
{
    Boolean allocateOrder(BeerOrderDto beerOrderDto);
}
