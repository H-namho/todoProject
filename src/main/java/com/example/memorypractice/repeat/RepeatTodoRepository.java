package com.example.memorypractice.repeat;

import com.example.memorypractice.repeat.resdto.ResRepeatList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
