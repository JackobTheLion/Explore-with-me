package ru.practicum.explore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.model.Area;


public interface AreaRepository extends JpaRepository<Area, Long> {

    Page<Area> findAllBy(Pageable page);

}
