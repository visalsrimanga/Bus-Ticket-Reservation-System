package com.enactor.api;

import com.enactor.dto.AvailabilityResponse;
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


@WebServlet(name = "availabilityServlet", value = "/check-availability")
public class AvailabilityServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String origin = request.getParameter("origin");
            String destination = request.getParameter("destination");
            int passengerCount = Integer.parseInt(request.getParameter("passengers"));

            DataValidation.isValidRequest(origin, destination, passengerCount);

            String route = origin + "-" + destination;
            int ticketPrice = BusData.getTicketPrice(route);
            int totalPrice = ticketPrice * passengerCount;
            int availableSeats = BusData.getAvailableSeats(origin, destination).size();

            boolean isAvailable = availableSeats >= passengerCount;

            AvailabilityResponse availabilityResponse = new AvailabilityResponse(isAvailable, availableSeats, totalPrice);
            JsonConvertor.sendJsonResponse(response, availabilityResponse);

        }catch (BaseException e) {
            JsonConvertor.sendJsonError(response, e.getStatusCode(), e.getErrorMessage());
        }catch (Exception e){
            JsonConvertor.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
