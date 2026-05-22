package com.example.memorypractice.user.reqdto;

import jakarta.validation.constraints.NotBlank;

public record ReqRereshToken(@NotBlank String refreshToken) {
}
