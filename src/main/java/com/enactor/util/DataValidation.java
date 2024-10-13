package com.enactor.util;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataValidation {
    private DataValidation() {
    }

    private static boolean isValidRoute(String origin, String destination) {
        if (origin == null || destination == null || origin.isBlank() || destination.isBlank() || origin.equals(destination)) {
            return false;
        }

        List<String> startStopList = new ArrayList<>(Arrays.asList("A", "B", "C"));
        List<String> endStopList = new ArrayList<>(Arrays.asList("B", "C", "D"));

        if (!startStopList.contains(origin) || !endStopList.contains(destination)) {
            return false;
        }

        if ((origin.equals("B") && destination.equals("A")) ||
                (origin.equals("C") && (destination.equals("A") || destination.equals("B"))) ||
                (origin.equals("D") && (destination.equals("A") || destination.equals("B") || destination.equals("C")))) {
            return false;
        }
        return true;
    }

    private static boolean isValidPassengerCount(int passengerCount) {
        return passengerCount > 0 && passengerCount <= 40;
    }

    public static void isValidRequest(String origin, String destination, int passengerCount) throws BaseException {
        if (!isValidRoute(origin, destination)) {
            throw new BaseException(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid route! Origin should be A, B, or C, and destination should be B, C, or D. Origin and destination cannot be the same, and the destination cannot be before the origin");
        }
        if (!isValidPassengerCount(passengerCount)){
            throw new BaseException(HttpServletResponse.SC_BAD_REQUEST, "Invalid Passengers Count! Please enter a value between 1 and 40");
        }
    }
}
