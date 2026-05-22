package com.example.memorypractice.todo;

import com.example.memorypractice.todo.resdto.TodayTodo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity,Long> {

    Optional<TodoEntity> findByIdAndUser_Id(Long todoId, Long userId);

    Page<TodoEntity> findByUser_Id(Long userId, Pageable pageable);

    long countByUser_IdAndCompleted(Long userId, boolean completed);

    @Query("""
        SELECT new com.example.memorypractice.todo.resdto.TodayTodo(
            t.user.id,
            t.title
        )
        FROM TodoEntity t
        WHERE t.dueDate = :now AND t.completed = :completed
        """)
    List<TodayTodo> findTodayTodo(LocalDate now, boolean completed);
}
