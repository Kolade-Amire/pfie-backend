package com.kay.pfie.common.web;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestIdUtil {
    private RequestIdUtil() {}

    public static String getRequestId(HttpServletRequest request) {
        Object attr = request.getAttribute(RequestIdFilter.REQUEST_ID_KEY);
        if (attr instanceof String value && !value.isBlank()) {
            return value;
        }
        String header = request.getHeader(RequestIdFilter.REQUEST_ID_HEADER);
        if (header != null && !header.isBlank()) {
            return header;
        }
        return "unknown";
    }
}
