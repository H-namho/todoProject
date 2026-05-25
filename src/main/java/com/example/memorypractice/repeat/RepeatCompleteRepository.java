package com.example.memorypractice.repeat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface RepeatCompleteRepository extends JpaRepository<RepeatTodoCompletionEntity,Long> {

    boolean existsByRepeatTodo_IdAndCompletedDate(Long repeatId, LocalDate completedDate);

    @Query("""
            SELECT c.repeatTodo.id
            FROM RepeatTodoCompletionEntity c
            WHERE c.user.id = :userId
              AND c.completedDate = :completedDate
            """)
    Set<Long> findCompletedRepeatTodoIds( Long userId, LocalDate completedDate);
}
