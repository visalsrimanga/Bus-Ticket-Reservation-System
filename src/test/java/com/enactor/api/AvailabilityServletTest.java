package com.enactor.api;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.enactor.initialize.BusData;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

public class AvailabilityServletTest {

    private AvailabilityServlet availabilityServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() {
        availabilityServlet = new AvailabilityServlet();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
    }

    @Test
    public void testDoGet_ValidRequest() throws ServletException, IOException {
        // Mock request parameters
        when(request.getParameter("origin")).thenReturn("A");
        when(request.getParameter("destination")).thenReturn("B");
        when(request.getParameter("passengers")).thenReturn("2");

        // Mock response writer
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // Mock BusData methods
        mockStatic(BusData.class);
        when(BusData.getTicketPrice("A-B")).thenReturn(100);
        when(BusData.getAvailableSeats("A", "B")).thenReturn(Set.of("1A", "1B", "1C"));

        // Call the doGet method
        availabilityServlet.doGet(request, response);

        // Verify the response
        String expectedResponse = "{\"isAvailable\":true,\"availableSeats\":3,\"totalPrice\":200}";

        System.out.println(responseWriter.toString());
        assertTrue(responseWriter.toString().contains(expectedResponse));
    }

    @Test
    public void testDoGet_InvalidRequest() throws ServletException, IOException {
        // Mock request parameters with invalid data
        when(request.getParameter("origin")).thenReturn("A");
        when(request.getParameter("destination")).thenReturn("B");
        when(request.getParameter("passengers")).thenReturn("41");

        // Mock response writer
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // Call the doGet method
        availabilityServlet.doGet(request, response);

        // Verify the error response
        assertTrue(responseWriter.toString().contains("Invalid Passengers Count! Please enter a value between 1 and 40"));
    }

    @Test
    public void testDoGet_ExceptionThrown() throws ServletException, IOException {
        // Mock request parameters to trigger an exception
        when(request.getParameter("origin")).thenReturn("A");
        when(request.getParameter("destination")).thenReturn("B");
        when(request.getParameter("passengers")).thenReturn("invalidNumber");

        // Mock response writer
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // Call the doGet method
        availabilityServlet.doGet(request, response);

        // Verify the internal server error response
        assertTrue(responseWriter.toString().contains("500"));
    }
}
