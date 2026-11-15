package com.booknest.reviews.controller;

import com.booknest.reviews.dto.CreateReviewRequest;
import com.booknest.reviews.model.Review;
import com.booknest.reviews.service.ReviewService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<Review> getReviews(
        @RequestParam @Positive Long bookId
    ) {
        return reviewService.getReviewsByBookId(bookId);
    }

    @PostMapping
    public ResponseEntity<Review> createReview(
        @Valid @RequestBody CreateReviewRequest request
    ) {
        Review createdReview = reviewService.createReview(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
        @PathVariable @Positive Long id
    ) {
        boolean deleted = reviewService.deleteReview(id);

        return deleted
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }
}
