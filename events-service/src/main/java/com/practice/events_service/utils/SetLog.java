package com.practice.events_service.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;

public class SetLog {
    public static void setLog(Logger log, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
    }
}
