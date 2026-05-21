package com.example.memorypratice.user.ReqDto;

import jakarta.validation.constraints.NotBlank;

public record ReqSignUp(@NotBlank String username,
                        @NotBlank String password,
                        @NotBlank String nickname) {
}
