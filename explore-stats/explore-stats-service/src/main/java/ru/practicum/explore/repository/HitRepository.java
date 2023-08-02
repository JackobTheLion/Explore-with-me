package ru.practicum.explore.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.model.EndpointHit;

@Repository
@Slf4j
public class HitRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public HitRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public EndpointHit addHit(EndpointHit endpointHit) {
        log.debug("Adding endpoint hit: {}.", endpointHit);
        String sql = "INSERT INTO endpoint_hit (app, uri, ip, sent) VALUES (:app, :uri, :ip, :timestamp)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(endpointHit);
        namedParameterJdbcTemplate.update(sql, parameterSource, keyHolder, new String[]{"endpoint_hit_id"});
        endpointHit.setId(keyHolder.getKeyAs(Long.class));
        log.info("Endpoint hit added: {}.", endpointHit);
        return endpointHit;
    }
}
