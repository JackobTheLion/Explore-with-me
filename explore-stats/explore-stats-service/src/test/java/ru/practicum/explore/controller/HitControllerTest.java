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
import ru.practicum.explore.dto.EndpointHitRequestDto;
import ru.practicum.explore.dto.EndpointHitSavedDto;
import ru.practicum.explore.service.StatsService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {HitController.class})
public class HitControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService statsService;

    private EndpointHitRequestDto endpointHitRequestDto;
    private EndpointHitSavedDto endpointHitSavedDto;

    @BeforeEach
    public void init() {
        endpointHitRequestDto = EndpointHitRequestDto.builder()
                .app("testApp")
                .uri("/test")
                .ip("192.168.1.0")
                .timestamp(LocalDateTime.parse("2022-09-06T11:00:23"))
                .build();

        endpointHitSavedDto = EndpointHitSavedDto.builder()
                .id(1L)
                .app(endpointHitRequestDto.getApp())
                .uri(endpointHitRequestDto.getUri())
                .ip(endpointHitRequestDto.getIp())
                .timestamp(endpointHitRequestDto.getTimestamp())
                .build();
    }

    @SneakyThrows
    @Test
    public void addEndpointHit_Normal() {
        when(statsService.addEndpointHit(any())).thenReturn(endpointHitSavedDto);
        String json =
                "{\n" +
                        "  \"app\": \"testApp\",\n" +
                        "  \"uri\": \"/test\",\n" +
                        "  \"ip\": \"192.168.1.0\",\n" +
                        "  \"timestamp\": \"2022-09-06 11:00:23\"\n" +
                        "}";

        String result = mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(statsService, times(1)).addEndpointHit(any());
        assertEquals(objectMapper.writeValueAsString(endpointHitSavedDto), result);
    }

    @SneakyThrows
    @Test
    public void addEndpointHit_EmptyApp() {
        endpointHitRequestDto.setApp("");

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endpointHitRequestDto)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).addEndpointHit(any());

        endpointHitRequestDto.setApp("   ");
        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endpointHitRequestDto)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).addEndpointHit(any());
    }

    @SneakyThrows
    @Test
    public void addEndpointHit_EmptyUri() {
        endpointHitRequestDto.setUri("");

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endpointHitRequestDto)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).addEndpointHit(any());

        endpointHitRequestDto.setUri("   ");
        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endpointHitRequestDto)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).addEndpointHit(any());
    }

    @SneakyThrows
    @Test
    public void addEndpointHit_EmptyIp() {
        endpointHitRequestDto.setIp("");

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endpointHitRequestDto)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).addEndpointHit(any());

        endpointHitRequestDto.setIp("   ");
        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endpointHitRequestDto)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).addEndpointHit(any());
    }

    @SneakyThrows
    @Test
    public void addEndpointHit_WrongIp() {
        endpointHitRequestDto.setIp("11111");

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endpointHitRequestDto)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).addEndpointHit(any());
    }

    @SneakyThrows
    @Test
    public void addEndpointHit_TimeStampInPast() {
        endpointHitRequestDto.setTimestamp(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(endpointHitRequestDto)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).addEndpointHit(any());
    }
}
