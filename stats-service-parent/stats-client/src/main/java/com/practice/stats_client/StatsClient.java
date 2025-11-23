package com.practice.stats_client;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.practice.stats_dto.EndpointHit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class StatsClient {
    private final ObjectMapper objectMapper;
    private static final String HOST = "localhost";
    private static final int PORT = 9090;
    private static final String APP = "events-service";

    public Boolean checkServiceAvailability() {
        try (Socket ignored = new Socket(HOST, PORT)) {
            return true;
        } catch (IOException e) {
            System.out.println("Сервер статистики недоступен");
            return false;
        }
    }

    public void post(HttpServletRequest request) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI postURI = URI.create(String.format("http://%s:%d/hit", HOST, PORT));

        EndpointHit endpointHit = EndpointHit.builder()
                .app(APP)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        String endpointHitJson = objectMapper.writeValueAsString(endpointHit);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(endpointHitJson))
                .uri(postURI)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .header("Accept", "*/*")
                .build();

        client.send(postRequest, HttpResponse.BodyHandlers.ofString());
    }

    public String get(String start, String end, String unique, String[] uris) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();

        String path = String.format("http://%s:%d/stats", HOST, PORT);

        URIBuilder uriBuilder = new URIBuilder(path);
        uriBuilder.addParameter("start", start);
        uriBuilder.addParameter("end", end);

        if (unique != null) {
            uriBuilder.addParameter("unique", unique);
        }

        if (uris != null) {
            for (String uri : uris) {
                uriBuilder.addParameter("uris", uri);
            }
        }

        URI getURI = URI.create(uriBuilder.toString());

        HttpRequest postRequest = HttpRequest.newBuilder()
                .GET()
                .uri(getURI)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "*/*")
                .build();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public Boolean checkIpExistsByUri(HttpServletRequest request) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String path = String.format("http://%s:%d/check", HOST, PORT);

        URIBuilder uriBuilder = new URIBuilder(path);
        uriBuilder.addParameter("uri", request.getRequestURI());
        uriBuilder.addParameter("ip", request.getRemoteAddr());

        URI getURI = URI.create(uriBuilder.toString());

        HttpRequest postRequest = HttpRequest.newBuilder()
                .GET()
                .uri(getURI)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "*/*")
                .build();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        return Boolean.valueOf(response.body());
    }
}
