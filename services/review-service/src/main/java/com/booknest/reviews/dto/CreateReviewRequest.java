package com.booknest.reviews.dto;

import jakarta.validation.constraints.*;

public record CreateReviewRequest(
    @NotNull
    @Positive
    Long bookId,

    @NotBlank
    String reviewerName,

    @NotNull
    @Min(1)
    @Max(5)
    Integer rating,

    @NotBlank
    String comment
) {}
