package ru.practicum.explore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.model.Area;
import ru.practicum.explore.model.AreaStatus;

import java.util.Optional;


public interface AreaRepository extends JpaRepository<Area, Long> {

    Page<Area> findAllByAreaStatusOrderById(Pageable page, AreaStatus areaStatus);

    Page<Area> findAllByOrderById(Pageable page);

    Optional<Area> findByIdAndAreaStatus(Long id, AreaStatus areaStatus);

}
