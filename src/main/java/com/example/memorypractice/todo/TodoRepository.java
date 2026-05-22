package com.example.memorypractice.todo;

import com.example.memorypractice.todo.resdto.TodayTodo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
        SELECT t
        FROM TodoEntity t
        WHERE t.user.id = :userId
          AND (:completed IS NULL OR t.completed = :completed)
          AND (:priority IS NULL OR t.priority = :priority)
          AND (
                :keyword IS NULL
                OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.memo) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        """)
    Page<TodoEntity> searchTodos(
            @Param("userId") Long userId,
            @Param("completed") Boolean completed,
            @Param("priority") TodoPriority priority,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
        SELECT new com.example.memorypractice.todo.resdto.TodayTodo(
            t.user.id,
            t.title
        )
        FROM TodoEntity t
        WHERE t.dueDate = :now AND t.completed = :completed
        """)
    List<TodayTodo> findTodayTodo(LocalDate now, boolean completed);

    @Modifying(clearAutomatically = true)
    @Query("""
            DELETE FROM TodoEntity t
            WHERE t.user.id=:userId AND t.id IN :todoIds
            
            """)
    void deleteTodoIds(@Param("userId") Long userId, @Param("todoIds") List<Long> todoIds);
}
