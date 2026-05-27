package com.example.memorypractice.app.user.reqdto;

import jakarta.validation.constraints.NotBlank;

public record ReqSignUp(@NotBlank String username,
                        @NotBlank String password,
                        @NotBlank String nickname) {
}
