package com.example.memorypractice.app.repeat;

import com.example.memorypractice.app.user.UserEntity;
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

    public void update(RepeatType repeatType, Set<DayOfWeek> dayOfWeek, LocalDate startDate, LocalDate endDate,
                       Boolean clearEndDate) {

        // Daily경우 요일지정 x
        if (repeatType == RepeatType.DAILY) {
            this.repeatType = RepeatType.DAILY;
            this.dayOfWeek.clear();
        }
        // Weekly경우 요일지정 o
        if (repeatType == RepeatType.WEEKLY) {
            if (dayOfWeek == null || dayOfWeek.isEmpty()) {
                throw new IllegalArgumentException("매주 반복은 요일을 지정해야 합니다.");
            }

            this.repeatType = RepeatType.WEEKLY;
            this.dayOfWeek.clear();
            this.dayOfWeek.addAll(dayOfWeek);
        }
        //
        if (repeatType == null && dayOfWeek != null) {
            if (this.repeatType != RepeatType.WEEKLY) {
                throw new IllegalArgumentException("매일 반복은 요일을 지정할 수 없습니다.");
            }

            if (dayOfWeek.isEmpty()) {
                throw new IllegalArgumentException("매주 반복은 요일을 지정해야 합니다.");
            }

            this.dayOfWeek.clear();
            this.dayOfWeek.addAll(dayOfWeek);
        }

        if (Boolean.TRUE.equals(clearEndDate) && endDate != null) {
            throw new IllegalArgumentException("종료일 변경과 종료일 삭제는 동시에 요청할 수 없습니다.");
        }

        LocalDate changedStartDate = startDate != null ? startDate : this.startDate;
        LocalDate changedEndDate = this.endDate;

        if (Boolean.TRUE.equals(clearEndDate)) {
            changedEndDate = null;
        } else if (endDate != null) {
            changedEndDate = endDate;
        }

        if (changedEndDate != null && changedEndDate.isBefore(changedStartDate)) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }

        this.startDate = changedStartDate;
        this.endDate = changedEndDate;

    }

    public boolean chkComplete(LocalDate date){
        if(!active){
            return false;
        }
        if(startDate != null && date.isBefore(startDate)){
            return false;
        }
        if(endDate != null &&date.isAfter(endDate)){
            return false;
        }
        if(repeatType == RepeatType.DAILY){
            return true;
        }
        if(repeatType == RepeatType.WEEKLY){
            return dayOfWeek.contains(date.getDayOfWeek());
        }
        return false;
    }


    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }
}
