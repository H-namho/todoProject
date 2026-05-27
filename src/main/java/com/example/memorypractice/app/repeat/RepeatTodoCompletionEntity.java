package com.example.memorypractice.app.repeat;

import com.example.memorypractice.app.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Entity
@Table(
        name = "repeat_todo_completions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_repeat_todo_completion",
                        columnNames = {"repeat_todo_id", "completed_date"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepeatTodoCompletionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repeat_todo_id", nullable = false)
    private RepeatTodoEntity repeatTodo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private LocalDate completedDate;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public RepeatTodoCompletionEntity(RepeatTodoEntity repeatTodo, UserEntity user, LocalDate completedDate) {
        this.repeatTodo = repeatTodo;
        this.user = user;
        this.completedDate = completedDate;
    }
}
