package com.enactor.initialize;

import com.enactor.util.BaseException;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BusData {
    private static final Map<String, Integer> ticketPrices = new HashMap<>();
    private static final Map<String, List<String>> seatBookingDetails = new HashMap<>();

    static {
        // Initialize ticket prices
        ticketPrices.put("A-B", 50);
        ticketPrices.put("A-C", 100);
        ticketPrices.put("A-D", 150);
        ticketPrices.put("B-C", 50);
        ticketPrices.put("B-D", 100);
        ticketPrices.put("C-D", 50);

        // Initialize 40 seats in the bus
        for (int i = 1; i <= 10; i++) {
            seatBookingDetails.put(i + "A", new ArrayList<>());
            seatBookingDetails.put(i + "B", new ArrayList<>());
            seatBookingDetails.put(i + "C", new ArrayList<>());
            seatBookingDetails.put(i + "D", new ArrayList<>());
        }
    }

    private BusData() {
    }

    public static int getTicketPrice(String route) {
        return ticketPrices.getOrDefault(route, 0);
    }

    public static synchronized Set<String> getAvailableSeats(String start, String end) {
        List<String> journeySegments = getJourneySegments(start, end);
        HashSet<String> availableSeats = new HashSet<>();

        boolean booked = false;
        for (String seatNum : seatBookingDetails.keySet()) {
            booked = false;
            List<String> bookedSegments = seatBookingDetails.get(seatNum);
            if (!bookedSegments.isEmpty()){
                for (String bookedSegment : bookedSegments) {
                    if (journeySegments.contains(bookedSegment)){
                        booked = true;
                        break;
                    }
                }
            }

            if (!booked) {
                availableSeats.add(seatNum);
            }
        }
        return availableSeats;
    }

    public static synchronized List<String> reserveSeats(String start, String end, int numberOfPassengers) throws BaseException {
        List<String> bookedSeatList = new ArrayList<>();

        if (getAvailableSeats(start, end).size() >= numberOfPassengers) {
            List<String> journeySegments = getJourneySegments(start, end);

            switch (journeySegments.size()) {
                case 3:
                    bookSeatFrom3AvailableSegment(start, end, numberOfPassengers, journeySegments, bookedSeatList);
                    break;
                case 2:
                    bookSeatFrom2AvailableSegment(start, end, numberOfPassengers, journeySegments, bookedSeatList);
                    if (bookedSeatList.size() != numberOfPassengers)
                        bookSeatFrom3AvailableSegment(start, end, numberOfPassengers, journeySegments, bookedSeatList);
                    break;
                case 1:
                    bookSeatFrom1AvailableSegment(start, end, numberOfPassengers, journeySegments, bookedSeatList);
                    if (bookedSeatList.size() != numberOfPassengers)
                        bookSeatFrom2AvailableSegment(start, end, numberOfPassengers, journeySegments, bookedSeatList);
                    if (bookedSeatList.size() != numberOfPassengers)
                        bookSeatFrom3AvailableSegment(start, end, numberOfPassengers, journeySegments, bookedSeatList);
                    break;
                default:
                    break;
            }
        }
        if (bookedSeatList.isEmpty()) throw new BaseException(HttpServletResponse.SC_BAD_REQUEST,
                "Sorry, There are no available seats for your selected route");

        return bookedSeatList;
    }

    private static void bookSeatFrom1AvailableSegment(String start, String end, int numberOfPassengers, List<String> journeySegments, List<String> bookedSeatList) {
        for (String seatNum : getAvailableSeats(start, end)) {
            if (bookedSeatList.size() == numberOfPassengers) {
                return;
            }
            if (seatBookingDetails.get(seatNum).size() == 2){
                bookTheSeat(seatNum, journeySegments, bookedSeatList);
            }
        }
    }

    private static void bookSeatFrom2AvailableSegment(String start, String end, int numberOfPassengers, List<String> journeySegments, List<String> bookedSeatList) {
        for (String seatNum : getAvailableSeats(start, end)) {
            if (bookedSeatList.size() == numberOfPassengers) {
                return;
            }
            if (seatBookingDetails.get(seatNum).size() == 1){
                bookTheSeat(seatNum, journeySegments, bookedSeatList);
            }
        }
    }

    private static void bookSeatFrom3AvailableSegment(String start, String end, int numberOfPassengers, List<String> journeySegments, List<String> bookedSeatList) {
        for (String seatNum : getAvailableSeats(start, end)) {
            if (bookedSeatList.size() == numberOfPassengers){
                return;
            }
            bookTheSeat(seatNum, journeySegments, bookedSeatList);
        }
    }

    private static void bookTheSeat(String seatNum, List<String> journeySegments, List<String> bookedSeatList) {
        seatBookingDetails.get(seatNum).addAll(journeySegments);
        bookedSeatList.add(seatNum);
    }


    private static List<String> getJourneySegments(String startStop, String endStop) {
        List<String> journeySegments = new ArrayList<>();
        switch (startStop) {
            case "A":
                journeySegments.add("A-B");
                if (endStop.equals("C") || endStop.equals("D")) journeySegments.add("B-C");
                if (endStop.equals("D")) journeySegments.add("C-D");
                break;
            case "B":
                journeySegments.add("B-C");
                if (endStop.equals("D")) journeySegments.add("C-D");
                break;
            case "C":
                journeySegments.add("C-D");
                break;
            default:
                break;
        }
        return journeySegments;
    }

    public static void clearAllBooking(){
        for (Map.Entry<String, List<String>> stringListEntry : seatBookingDetails.entrySet()) {
            stringListEntry.getValue().clear();
        }
    }

}
