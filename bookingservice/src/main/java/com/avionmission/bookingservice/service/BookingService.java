package com.avionmission.bookingservice.service;

import com.avionmission.bookingservice.client.InventoryServiceClient;
import com.avionmission.bookingservice.entity.Customer;
import com.avionmission.bookingservice.repository.CustomerRepository;
import com.avionmission.bookingservice.request.BookingRequest;
import com.avionmission.bookingservice.response.BookingResponse;
import com.avionmission.bookingservice.response.InventoryResponse;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final CustomerRepository customerRepository;
    private final InventoryServiceClient inventoryServiceClient;

    public BookingService(final CustomerRepository customerRepository, InventoryServiceClient inventoryServiceClient) {
        this.customerRepository = customerRepository;
        this.inventoryServiceClient = inventoryServiceClient;
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
        if(inventoryResponse.getCapacity() < request.getTicketCount()) {
            throw new RuntimeException("Not Enough Inventory");
        }
        // create booking
        // send booking to Order Service on a Kafka topic

        return BookingResponse.builder().build();
    }
}
