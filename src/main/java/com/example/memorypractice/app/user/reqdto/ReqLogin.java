package com.example.memorypractice.app.user.reqdto;

import jakarta.validation.constraints.NotBlank;

public record ReqLogin(@NotBlank String username,
                       @NotBlank String password) {
}
