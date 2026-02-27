package com.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class OrderProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        String processedBody = body.toUpperCase() + " - PROCESSED";
        exchange.getIn().setBody(processedBody);
        exchange.getIn().setHeader("ProcessedBy", "OrderProcessor");
    }
}
