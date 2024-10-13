package com.enactor.api;

import com.enactor.dto.ReservationResponse;
import com.enactor.initialize.BusData;
import com.enactor.util.BaseException;
import com.enactor.util.DataValidation;
import com.enactor.util.JsonConvertor;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet("/reserve-ticket")
public class ReservationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String origin = request.getParameter("origin");
            String destination = request.getParameter("destination");
            int passengerCount = Integer.parseInt(request.getParameter("passengers"));

            DataValidation.isValidRequest(origin, destination, passengerCount);

            List<String> bookedSeatList = BusData.reserveSeats(origin, destination, passengerCount);
            String reservationNumber = UUID.randomUUID().toString();

            String route = origin + "-" + destination;
            int ticketPrice = BusData.getTicketPrice(route);
            int paymentAmount = ticketPrice * passengerCount;

            ReservationResponse reservationResponse = new ReservationResponse(reservationNumber, bookedSeatList, origin, destination, paymentAmount);
            JsonConvertor.sendJsonResponse(response, reservationResponse);

        }catch (BaseException e) {
            JsonConvertor.sendJsonError(response, e.getStatusCode(), e.getErrorMessage());
        }catch (Exception e){
            JsonConvertor.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}

