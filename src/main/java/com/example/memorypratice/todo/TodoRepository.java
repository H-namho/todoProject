package com.example.memorypratice.todo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity,Long> {

    Optional<TodoEntity> findByIdAndUser_Id(Long todoId, Long userId);

    Page<TodoEntity> findByUser_Id(Long userId, Pageable pageable);

    long countByUser_IdAndCompleted(Long userId, boolean completed);
}
