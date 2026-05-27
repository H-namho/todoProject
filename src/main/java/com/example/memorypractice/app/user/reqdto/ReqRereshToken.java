package com.example.memorypractice.app.user.reqdto;

import jakarta.validation.constraints.NotBlank;

public record ReqRereshToken(@NotBlank String refreshToken) {
}
