package com.enactor.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ReservationServletTest {

    private ReservationServlet reservationServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() {
        reservationServlet = new ReservationServlet();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
    }

    @Test
    public void testDoPost_InvalidRequest() throws ServletException, IOException {
        // Mock request parameters with invalid data
        when(request.getParameter("origin")).thenReturn("A");
        when(request.getParameter("destination")).thenReturn("B");
        when(request.getParameter("passengers")).thenReturn("41");

        // Mock response writer
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // Call the doPost method
        reservationServlet.doPost(request, response);

        // Verify the error response
        assertTrue(responseWriter.toString().contains("Invalid Passengers Count! Please enter a value between 1 and 40"));
    }

    @Test
    public void testDoPost_ExceptionThrown() throws ServletException, IOException {
        // Mock request parameters to trigger an exception
        when(request.getParameter("origin")).thenReturn("A");
        when(request.getParameter("destination")).thenReturn("B");
        when(request.getParameter("passengers")).thenReturn("invalidNumber");

        // Mock response writer
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        // Call the doPost method
        reservationServlet.doPost(request, response);

        // Verify the internal server error response
        assertTrue(responseWriter.toString().contains("500"));
    }
}