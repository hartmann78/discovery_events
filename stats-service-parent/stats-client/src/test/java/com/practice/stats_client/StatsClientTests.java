package com.practice.stats_client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatsClientTests {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StatsClient client = new StatsClient(objectMapper);

    private static final String HOST = "localhost";
    private static final int PORT = 9090;

    private static MockHttpServletRequest request;

    @BeforeAll
    static void createMockHttpServletRequest() {
        request = new MockHttpServletRequest();
        request.setRequestURI("/events/1");
    }

    @Test
    @Order(1)
    void post() {
        assertTrue(checkServiceAvailability());
        assertDoesNotThrow(() -> client.post(request));
    }

    @Test
    @Order(2)
    void get() {
        String start = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String unique = String.valueOf(false);
        String[] uris = new String[]{request.getRequestURI()};

        assertDoesNotThrow(() -> client.get(start, end, unique, uris));
    }

    @Test
    @Order(3)
    void checkIpExistsByUri() throws URISyntaxException, IOException, InterruptedException {
        assertTrue(client.checkIpExistsByUri(request));
    }

    public Boolean checkServiceAvailability() {
        try (Socket ignored = new Socket(HOST, PORT)) {
            return true;
        } catch (IOException e) {
            System.out.println("Сервер статистики недоступен");
            return false;
        }
    }
}
