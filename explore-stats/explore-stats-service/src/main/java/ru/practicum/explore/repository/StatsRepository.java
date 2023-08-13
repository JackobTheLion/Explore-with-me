package ru.practicum.explore.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.dto.EndpointHitResponseDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class StatsRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public StatsRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<EndpointHitResponseDto> getUniqueHitsByUri(LocalDateTime start, LocalDateTime end, List<String> uriList) {
        log.info("Getting unique endpoints hit from {} to {} for the uris: {}", start, end, uriList);
        String sql = "SELECT a.app_name as app, eh.uri as uri, COUNT(DISTINCT eh.ip) as hits FROM endpoint_hit as eh " +
                "LEFT JOIN app as a ON eh.app_id = a.app_id WHERE eh.sent BETWEEN :start AND :end AND eh.uri " +
                "IN (:uriList) GROUP BY a.app_name, eh.uri ORDER BY hits DESC";
        SqlParameterSource parameterSource = new MapSqlParameterSource("uriList", uriList)
                .addValue("start", start)
                .addValue("end", end);
        List<EndpointHitResponseDto> allHits = namedParameterJdbcTemplate.query(sql, parameterSource, (rs, rowNum) ->
                mapHit(rs));
        log.info("Query result: {}", allHits);
        return allHits;
    }

    public List<EndpointHitResponseDto> getAllHitsByUri(LocalDateTime start, LocalDateTime end, List<String> uriList) {
        log.info("Getting all endpoints hit from {} to {} for the uris: {}", start, end, uriList);

        String sql = "SELECT a.app_name as app, eh.uri as uri, COUNT(eh.ip) as hits FROM endpoint_hit as eh " +
                "LEFT JOIN app as a ON eh.app_id = a.app_id WHERE eh.sent BETWEEN :start AND :end AND eh.uri " +
                "IN (:uriList) GROUP BY a.app_name, eh.uri ORDER BY hits DESC";

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("uriList", uriList)
                .addValue("start", start)
                .addValue("end", end);
        List<EndpointHitResponseDto> allHits = namedParameterJdbcTemplate.query(sql, parameterSource, (rs, rowNum) ->
                mapHit(rs));
        log.info("Query result: {}", allHits);
        return allHits;
    }

    public List<EndpointHitResponseDto> getAllHits(LocalDateTime start, LocalDateTime end) {
        log.info("Getting all endpoints hit from {} to {}.", start, end);
        String sql = "SELECT a.app_name as app, eh.uri as uri, COUNT(eh.ip) as hits FROM endpoint_hit as eh " +
                "LEFT JOIN app as a ON eh.app_id = a.app_id WHERE eh.sent BETWEEN :start AND :end " +
                "GROUP BY a.app_name, eh.uri ORDER BY hits DESC";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("start", start)
                .addValue("end", end);
        List<EndpointHitResponseDto> allHits = namedParameterJdbcTemplate.query(sql, parameterSource, (rs, rowNum) ->
                mapHit(rs));
        log.info("Query result: {}", allHits);
        return allHits;
    }

    public List<EndpointHitResponseDto> getUniqueHits(LocalDateTime start, LocalDateTime end) {
        log.info("Getting unique endpoints hit from {} to {}.", start, end);
        String sql = "SELECT a.app_name as app, eh.uri as uri, COUNT(DISTINCT eh.ip) as hits FROM endpoint_hit as eh " +
                "LEFT JOIN app as a ON eh.app_id = a.app_id WHERE eh.sent BETWEEN :start AND :end " +
                "GROUP BY a.app_name, eh.uri ORDER BY hits DESC";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("start", start)
                .addValue("end", end);
        List<EndpointHitResponseDto> allHits = namedParameterJdbcTemplate.query(sql, parameterSource, (rs, rowNum) ->
                mapHit(rs));
        log.info("Query result: {}", allHits);
        return allHits;
    }

    private EndpointHitResponseDto mapHit(ResultSet rs) throws SQLException {
        return EndpointHitResponseDto.builder()
                .app(rs.getString("app"))
                .uri(rs.getString("uri"))
                .hits(rs.getInt("hits"))
                .build();
    }
}
