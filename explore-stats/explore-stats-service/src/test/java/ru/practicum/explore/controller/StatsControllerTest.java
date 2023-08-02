package ru.practicum.explore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explore.dto.EndpointHitResponseDto;
import ru.practicum.explore.exception.handler.ErrorHandler;
import ru.practicum.explore.service.StatsService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {StatsController.class, ErrorHandler.class})
public class StatsControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService statsService;

    private EndpointHitResponseDto endpointHitResponseDto;
    private List<EndpointHitResponseDto> hits;

    @BeforeEach
    public void init() {
        endpointHitResponseDto = EndpointHitResponseDto.builder()
                .app("app")
                .uri("/test")
                .hits(2)
                .build();

        hits = List.of(endpointHitResponseDto);
    }

    @SneakyThrows
    @Test
    public void getStats_Normal() {
        when(statsService.getHits(any(), any(), any(), any())).thenReturn(hits);
        String start = "2020-07-30 00:00:00";
        String end = "2025-07-30 00:00:00";

        String result = mockMvc.perform(get("/stats")
                        .param("start", start)
                        .param("end", end)
                        .param("uris", "/test, /test1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(statsService, times(1)).getHits(any(), any(), any(), any());
        assertEquals(objectMapper.writeValueAsString(hits), result);
    }

    @SneakyThrows
    @Test
    public void getStats_WrongStart() {
        String start = URLEncoder.encode("2020-07-30T00:00:00", StandardCharsets.UTF_8);
        String end = URLEncoder.encode("2025-07-30 00:00:00", StandardCharsets.UTF_8);

        String result = mockMvc.perform(get("/stats")
                        .param("start", start)
                        .param("end", end)
                        .param("uris", "/test, /test1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(statsService, never()).getHits(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getStats_WrongEnd() {
        String start = URLEncoder.encode("2020-07-30 00:00:00", StandardCharsets.UTF_8);
        String end = URLEncoder.encode("2025-07-30T00:00:00", StandardCharsets.UTF_8);

        String result = mockMvc.perform(get("/stats")
                        .param("start", start)
                        .param("end", end)
                        .param("uris", "/test, /test1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(statsService, never()).getHits(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getStats_EndBeforeStart() {
        String start = URLEncoder.encode("2020-07-30 00:00:00", StandardCharsets.UTF_8);
        String end = URLEncoder.encode("1990-07-30 00:00:00", StandardCharsets.UTF_8);

        String result = mockMvc.perform(get("/stats")
                        .param("start", start)
                        .param("end", end)
                        .param("uris", "/test, /test1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(statsService, never()).getHits(any(), any(), any(), any());
    }

    @SneakyThrows
    @Test
    public void getStats_WrongUnique() {
        String start = URLEncoder.encode("2020-07-30 00:00:00", StandardCharsets.UTF_8);
        String end = URLEncoder.encode("1990-07-30 00:00:00", StandardCharsets.UTF_8);

        String result = mockMvc.perform(get("/stats")
                        .param("start", start)
                        .param("end", end)
                        .param("uris", "/test, /test1")
                        .param("unique", "wrongValue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(statsService, never()).getHits(any(), any(), any(), any());
    }
}
