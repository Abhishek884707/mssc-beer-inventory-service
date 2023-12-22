package org.mourya.msscbeerinventoryservice.web.controller;

import com.netflix.discovery.converters.Auto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mourya.msscbeerinventoryservice.respositories.BeerInventoryRepository;
import org.mourya.msscbeerinventoryservice.web.mapper.BeerInventoryMapper;
import org.mourya.msscbeerinventoryservice.web.model.BeerInventoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerInventoryController {

    @Autowired
    private final BeerInventoryRepository beerInventoryRepository;
    @Autowired
    private final BeerInventoryMapper beerInventoryMapper;

    @GetMapping("api/v1/beer/{beerId}/inventory")
    List<BeerInventoryDto> listBeersById(@PathVariable UUID beerId){
        log.debug("Finding Inventory for beerId:" + beerId);

        return beerInventoryRepository.findAllByBeerId(beerId)
                .stream()
                .map(beerInventoryMapper::beerInventoryToBeerInventoryDto)
                .toList();
    }
}