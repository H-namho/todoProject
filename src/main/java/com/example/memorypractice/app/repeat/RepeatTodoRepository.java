package com.example.memorypractice.app.repeat;

import com.example.memorypractice.app.todo.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepeatTodoRepository extends JpaRepository<RepeatTodoEntity,Long> {

    // Daily는 dayOfWeek에 없으므로 이너조인하면 rt의 값 누락됌 -> left join 해야됌
    @Query("""
            SELECT DISTINCT rt
            FROM RepeatTodoEntity rt LEFT JOIN FETCH rt.dayOfWeek
            WHERE rt.user.id = :userId
            ORDER BY rt.startDate DESC, rt.id DESC
            
            """)
    List<RepeatTodoEntity> findAllWithDayOfWeek(Long userId);

    Optional<RepeatTodoEntity> findByIdAndUser_Id(Long repeatId, Long userId);


    @Query("""
            SELECT rt
            FROM RepeatTodoEntity rt
            WHERE rt.user.id = :userId
            AND rt.startDate <= :date AND (rt.endDate IS NULL OR rt.endDate >= :date)
            AND (rt.repeatType=:daily OR (rt.repeatType=:weekly AND :dayOfWeek MEMBER OF rt.dayOfWeek))
        """)
    List<RepeatTodoEntity> findByUserIdAndDate(Long userId, LocalDate date, DayOfWeek dayOfWeek,RepeatType daily, RepeatType weekly);
}
