package ru.practicum.explore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByIdInOrderById(List<Long> ids, PageRequest page);

    Page<User> findAllByOrderById(PageRequest page);

}
