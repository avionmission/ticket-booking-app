package com.avionmission.bookingservice.service;

import com.avionmission.bookingservice.client.InventoryServiceClient;
import com.avionmission.bookingservice.entity.Customer;
import com.avionmission.bookingservice.event.BookingEvent;
import com.avionmission.bookingservice.repository.CustomerRepository;
import com.avionmission.bookingservice.request.BookingRequest;
import com.avionmission.bookingservice.response.BookingResponse;
import com.avionmission.bookingservice.response.InventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class BookingService {

    private final CustomerRepository customerRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    public BookingService(final CustomerRepository customerRepository,
                          final InventoryServiceClient inventoryServiceClient,
                          final KafkaTemplate<String, BookingEvent> kafkaTemplate
    ) {
        this.customerRepository = customerRepository;
        this.inventoryServiceClient = inventoryServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public BookingResponse createBooking(final BookingRequest request) {
        // check if the user exists
        final Customer customer = customerRepository.findById(request.getUserId()).orElse(null);
        if(customer == null) {
            throw new RuntimeException("User Not Found");
        }
        // check if there is enough inventory
        final InventoryResponse inventoryResponse = inventoryServiceClient.getInventory(request.getEventId());
        System.out.println("Inventory Service Response:" + inventoryResponse);
        log.info("Inventory Service Response: {}", inventoryResponse);
        if(inventoryResponse.getCapacity() < request.getTicketCount()) {
            throw new RuntimeException("Not Enough Inventory");
        }
        // create booking
        final BookingEvent bookingEvent = createBookingEvent(request, customer, inventoryResponse);
        // send booking to Order Service on a Kafka topic
        kafkaTemplate.send("booking", bookingEvent);
        log.info("Booking sent to Kafka: {}", bookingEvent);
        return BookingResponse.builder()
                .userId(bookingEvent.getUserId())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }

    private BookingEvent createBookingEvent(final BookingRequest request,
                                            final Customer customer,
                                            final InventoryResponse inventoryResponse) {
        return BookingEvent.builder()
                .userId(customer.getId())
                .eventId(request.getEventId())
                .ticketCount(request.getTicketCount())
                .totalPrice(inventoryResponse.getTicketPrice().multiply(BigDecimal.valueOf(request.getTicketCount())))
                .build();
    }
}
