package com.practice.stats_service.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.stats_dto.EndpointHit;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatsControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String app = "events-service";
    private static final String uri = "/events/1";
    private static final String ip = "192.168.1.1";
    private static final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    private static EndpointHit endpointHit;

    @Test
    @Order(1)
    void postEndpoint() throws Exception {
        endpointHit = new EndpointHit(null, app, uri, ip, timestamp);
        String endpointHitJson = objectMapper.writeValueAsString(endpointHit);

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(endpointHitJson))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    void getViewStats() throws Exception {
        String[] uris = new String[]{uri};
        String urisJson = objectMapper.writeValueAsString(uris);

        mockMvc.perform(get("/stats")
                        .param("start", LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("end", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("unique", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(urisJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value(app))
                .andExpect(jsonPath("$[0].uri").value(uri))
                .andExpect(jsonPath("$[0].hits").value(1));
    }

    @Test
    @Order(3)
    void checkIpExistsByUri() throws Exception {
        mockMvc.perform(get("/check")
                        .param("uri", endpointHit.getUri())
                        .param("ip", endpointHit.getIp()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
