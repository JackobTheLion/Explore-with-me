package ru.practicum.explore.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.practicum.explore.dto.EndpointHitResponseDto;
import ru.practicum.explore.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsRepositoryTest {

    private final StatsRepository statsRepository;
    private final HitRepository hitRepository;
    private final JdbcTemplate jdbcTemplate;

    private EndpointHit endpointHit1;
    private EndpointHit endpointHit2;

    @BeforeEach
    public void cleanData() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "endpoint_hit");
        jdbcTemplate.update("ALTER TABLE endpoint_hit ALTER COLUMN endpoint_hit_id RESTART WITH 1");
        init();
    }

    public void init() {
        endpointHit1 = EndpointHit.builder()
                .app("app")
                .uri("/uri")
                .ip("192.168.1.1")
                .timestamp(LocalDateTime.now())
                .build();
        hitRepository.addHit(endpointHit1);

        endpointHit2 = EndpointHit.builder()
                .app("app")
                .uri("/uri")
                .ip("192.168.1.2")
                .timestamp(LocalDateTime.now().minusHours(1))
                .build();
        hitRepository.addHit(endpointHit2);
    }

    @Test
    public void getAllHitsByUri_Normal() {
        EndpointHitResponseDto hit1 = EndpointHitResponseDto.builder()
                .app(endpointHit1.getApp())
                .uri(endpointHit1.getUri())
                .hits(1)
                .build();
        List<EndpointHitResponseDto> expected = List.of(hit1);


        List<String> uris = List.of(endpointHit1.getUri());

        List<EndpointHitResponseDto> actual = statsRepository.getAllHitsByUri(LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusMinutes(1), uris);

        assertEquals(expected, actual);
    }

    @Test
    public void getUniqueHitsByUri_Normal() {
        EndpointHitResponseDto hit1 = EndpointHitResponseDto.builder()
                .app(endpointHit1.getApp())
                .uri(endpointHit1.getUri())
                .hits(1)
                .build();
        List<EndpointHitResponseDto> expected = List.of(hit1);

        List<String> uris = List.of(endpointHit1.getUri());

        List<EndpointHitResponseDto> actual = statsRepository.getUniqueHitsByUri(LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusMinutes(1), uris);

        assertEquals(expected, actual);
    }

    @Test
    public void getUniqueHits_Normal() {
        EndpointHitResponseDto hit1 = EndpointHitResponseDto.builder()
                .app(endpointHit1.getApp())
                .uri(endpointHit1.getUri())
                .hits(1)
                .build();
        List<EndpointHitResponseDto> expected = List.of(hit1);

        List<EndpointHitResponseDto> actual = statsRepository.getUniqueHits(LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusMinutes(1));

        assertEquals(expected, actual);
    }

    @Test
    public void getAllHits_Normal() {
        EndpointHitResponseDto hit1 = EndpointHitResponseDto.builder()
                .app(endpointHit1.getApp())
                .uri(endpointHit1.getUri())
                .hits(1)
                .build();
        List<EndpointHitResponseDto> expected = List.of(hit1);

        List<EndpointHitResponseDto> actual = statsRepository.getAllHits(LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusMinutes(1));

        assertEquals(expected, actual);
    }


}
