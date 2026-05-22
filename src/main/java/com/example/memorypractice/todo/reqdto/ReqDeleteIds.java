package com.example.memorypractice.todo.reqdto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ReqDeleteIds(@NotEmpty List<Long> todoIds) {
}
