package com.booknest.books.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 255)
    String title,

    @NotBlank(message = "Author is required")
    @Size(max = 255)
    String author,

    String genre,

    String description
) {
}
