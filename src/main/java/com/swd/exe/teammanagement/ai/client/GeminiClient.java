package com.swd.exe.teammanagement.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class GeminiClient {

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${gemini.api-key}")
    private String apiKey;

    public GeminiClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /** Hàm core: gọi Gemini và trả về JsonNode gốc */
    private Mono<JsonNode> callGemini(Map<String, Object> body) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-2.5-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build()
                )
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    try {
                        return mapper.readTree(json);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /** Dùng khi muốn Gemini trả TEXT bình thường */
    public Mono<String> generateText(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        return callGemini(body)
                .map(root -> root.path("candidates").get(0)
                        .path("content").path("parts").get(0)
                        .path("text").asText()
                )
                .onErrorResume(WebClientResponseException.class, e ->
                        Mono.just("Gemini error: " + e.getResponseBodyAsString())
                );
    }

    /**
     * Dùng khi muốn Gemini TRẢ VỀ JSON – dùng cho “function calling”.
     * Ta ép response_mime_type = application/json để model xuất ra đúng JSON.
     */
    public Mono<String> generateJson(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "response_mime_type", "application/json"
                )
        );

        return callGemini(body)
                .map(root -> root.path("candidates").get(0)
                        .path("content").path("parts").get(0)
                        .path("text").asText()
                )
                .onErrorResume(WebClientResponseException.class, e ->
                        Mono.just("{\"error\":\"" + e.getResponseBodyAsString() + "\"}")
                );
    }
}
