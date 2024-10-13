package com.enactor.util;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class JsonConvertor {
    public static void sendJsonError(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");

        String jsonErrorResponse = new Gson().toJson(Map.of("code", statusCode, "message", errorMessage));
        response.getWriter().write(jsonErrorResponse);
    }

    public static void sendJsonResponse(HttpServletResponse response, Object responseDto) throws IOException {
        String jsonResponse = new Gson().toJson(responseDto);

        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }
}
