package com.example.memorypractice.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepeatTodoRepository extends JpaRepository<RepeatTodoEntity,Long> {
}
