package com.avionmission.bookingservice.controller;

import com.avionmission.bookingservice.request.BookingRequest;
import com.avionmission.bookingservice.response.BookingResponse;
import com.avionmission.bookingservice.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/booking")
    public BookingResponse createBooking(BookingRequest request) {
        return bookingService.createBooking(request);
    }
}
