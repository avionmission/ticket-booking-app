package com.avionmission.bookingservice.service;

import com.avionmission.bookingservice.entity.Customer;
import com.avionmission.bookingservice.repository.CustomerRepository;
import com.avionmission.bookingservice.request.BookingRequest;
import com.avionmission.bookingservice.response.BookingResponse;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final CustomerRepository customerRepository;

    public BookingService(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public BookingResponse createBooking(final BookingRequest request) {
        // check if the user exists
        final Customer customer = customerRepository.findById(request.getUserId()).orElse(null);
        if(customer == null) {
            throw new RuntimeException("User Not Found");
        }
        // check if there is enough inventory
        // -- get event information to also get Venue information
        // create booking
        // send booking to Order Service on a Kafka topic

        return BookingResponse.builder().build();
    }
}
