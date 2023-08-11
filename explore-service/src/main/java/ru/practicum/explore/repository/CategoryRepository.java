package ru.practicum.explore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findAllBy(PageRequest page);

}
