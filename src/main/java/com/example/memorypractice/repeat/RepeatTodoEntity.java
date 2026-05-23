package com.example.memorypractice.repeat;

import com.example.memorypractice.user.UserEntity;
import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Entity
@Table(name = "repeat_todos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepeatTodoEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RepeatType repeatType;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    @ElementCollection
    @CollectionTable(
            name = "repeat_todo_days",
            joinColumns = @JoinColumn(name = "repeat_todo_id")
    )
    private Set<DayOfWeek> dayOfWeek=new HashSet<>();

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public RepeatTodoEntity(UserEntity user, String title, String memo, RepeatType repeatType,Set<DayOfWeek>dayOfWeek,
                            LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.title = title;
        this.memo = memo;
        if(dayOfWeek!=null && !dayOfWeek.isEmpty()){
            this.dayOfWeek.addAll(dayOfWeek);
        }
        this.repeatType = repeatType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }

    public void update(String title, String memo, RepeatType repeatType,
                       Set<DayOfWeek> dayOfWeek, LocalDate startDate, LocalDate endDate) {
        if (title != null) {
            this.title = title;
        }
        if (memo != null) {
            this.memo = memo;
        }
        if (repeatType != null) {
            this.repeatType = repeatType;
        }
        if (dayOfWeek !=null && !dayOfWeek.isEmpty()){
            this.dayOfWeek.clear();
            this.dayOfWeek.addAll(dayOfWeek);
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }
}
