package org.mourya.msscbeerinventoryservice.services.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mourya.brewery.model.events.AllocateOrderRequest;
import org.mourya.msscbeerinventoryservice.config.JmsConfig;
import org.mourya.msscbeerinventoryservice.services.AllocationService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeallocationListener {
    private final AllocationService allocationService;
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.DEALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderRequest request) {
        allocationService.deallocateOrder(request.getBeerOrderDto());
    }

}
