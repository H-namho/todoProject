package com.example.memorypractice.app.user.reqdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReqPassword(
        @NotBlank
        String nowPassword,

        @NotBlank
        @Size(min = 8, max = 100)
        String newPassword
) {
}
