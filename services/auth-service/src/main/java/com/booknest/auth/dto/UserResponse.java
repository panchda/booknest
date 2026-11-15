package com.booknest.auth.dto;

import com.booknest.auth.model.Role;

public record UserResponse(
        Long id,
        String email,
        Role role
) {
}
