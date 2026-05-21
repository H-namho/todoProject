package com.example.memorypratice.todo;

import com.example.memorypratice.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Entity
@Table(name = "todos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class  TodoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String memo;

    @Column(nullable = false)
    private boolean completed;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TodoPriority priority;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public TodoEntity(UserEntity user, String title, String memo, LocalDate dueDate, TodoPriority priority) {
        this.user = user;
        this.title = title;
        this.memo = memo;
        this.completed = false;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public void update(String title, String memo, LocalDate dueDate, TodoPriority priority) {
        if(title!=null){
            this.title = title;
        }
        if(memo!=null){
            this.memo = memo;
        }
        if(dueDate!=null){
            this.dueDate = dueDate;
        }
        if(priority!=null){
            this.priority = priority;
        }
    }

    public void complete() {
        this.completed = true;
    }

    public void reopen() {
        this.completed = false;
    }
}
