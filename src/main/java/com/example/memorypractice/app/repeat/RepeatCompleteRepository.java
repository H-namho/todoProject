package com.example.memorypractice.app.repeat;

import com.example.memorypractice.app.repeat.resdto.ResCompleteDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface RepeatCompleteRepository extends JpaRepository<RepeatTodoCompletionEntity,Long> {

    boolean existsByRepeatTodo_IdAndCompletedDate(Long repeatId, LocalDate completedDate);

    @Query("""
            SELECT c.repeatTodo.id
            FROM RepeatTodoCompletionEntity c
            WHERE c.user.id = :userId AND c.completedDate = :completedDate
            """)
    Set<Long> findCompletedRepeatTodoIds( Long userId, LocalDate completedDate);

    void deleteByRepeatTodo_Id(Long id);

    void deleteByRepeatTodo_IdAndCompletedDate(Long repeatId, LocalDate today);

    long countByRepeatTodo_IdAndCompletedDateBetween(Long repeatId, LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT new com.example.memorypractice.app.repeat.resdto.ResCompleteDay(c.completedDate)
            FROM RepeatTodoCompletionEntity c
            WHERE c.repeatTodo.id = :repeatId
            ORDER BY c.completedDate DESC
            """)
    List<ResCompleteDay> findCompleteDaysByRepeatTodoId(@Param("repeatId") Long repeatId);
}
