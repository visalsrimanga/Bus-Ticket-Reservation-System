package com.enactor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private String reservationNumber;
    private List<String> bookedSeatList;
    private String origin;
    private String destination;
    private int totalPrice;
}
