package com.example.memorypractice.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepeatCompleteRepository extends JpaRepository<RepeatTodoCompletionEntity,Long> {
}
