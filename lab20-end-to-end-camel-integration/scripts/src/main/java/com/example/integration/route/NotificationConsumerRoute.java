package com.alnafi.integration.route;

import org.apache.camel.builder.RouteBuilder;

public class NotificationConsumerRoute extends RouteBuilder {

 @Override
 public void configure() throws Exception {

 from("jms:queue:order.notifications.priority")
 .routeId("priority-notification-consumer")
 .log("MQ PRIORITY NOTIFICATION RECEIVED: ${body}")
 .to("file:data/output?fileName=mq_priority_${date:now:yyyyMMdd_HHmmssSSS}.txt");

 from("jms:queue:order.notifications.standard")
 .routeId("standard-notification-consumer")
 .log("MQ STANDARD NOTIFICATION RECEIVED: ${body}")
 .to("file:data/output?fileName=mq_standard_${date:now:yyyyMMdd_HHmmssSSS}.txt");
 }
}
