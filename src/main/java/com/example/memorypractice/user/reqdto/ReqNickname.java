package com.example.memorypractice.user.reqdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReqNickname(
        @NotBlank
        @Size(max = 50)
        String nickname
) {
}
