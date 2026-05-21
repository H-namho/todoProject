package com.example.memorypratice.user.ReqDto;

import jakarta.validation.constraints.NotBlank;

public record ReqLogin(@NotBlank String username,
                       @NotBlank String password) {
}
