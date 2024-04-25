package org.mourya.msscbeerinventoryservice.services.listener;

import lombok.extern.slf4j.Slf4j;
import org.mourya.brewery.model.events.AllocateOrderRequest;
import org.mourya.brewery.model.events.AllocateOrderResult;
import org.mourya.msscbeerinventoryservice.config.JmsConfig;
import org.mourya.msscbeerinventoryservice.services.AllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
//@RequiredArgsConstructor
@Component
public class AllocationListener {

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderRequest request) {
        AllocateOrderResult.AllocateOrderResultBuilder builder = AllocateOrderResult.builder();
        builder.beerOrderDto(request.getBeerOrderDto());
        try {
            Boolean allocationResult = allocationService.allocateOrder(request.getBeerOrderDto());
            builder.pendingInventory(!Boolean.TRUE.equals(allocationResult));

            builder.allocationError(false);
        }catch (Exception e){
            log.info("Allocation Failed for Order id: " + request.getBeerOrderDto().getId());
            builder.allocationError(true);
        }

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, builder.build());
    }

}
