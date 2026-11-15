package com.booknest.reviews.service;

import com.booknest.reviews.client.BookClient;
import com.booknest.reviews.dto.CreateReviewRequest;
import com.booknest.reviews.model.Review;
import com.booknest.reviews.repository.ReviewRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookClient bookClient;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void shouldReturnReviewsForBook() {
        Long bookId = 2L;

        Review review = new Review();
        review.setBookId(bookId);
        review.setReviewerName("Dasha");
        review.setRating(5);
        review.setComment("Great book!");

        when(reviewRepository.findByBookId(bookId))
                .thenReturn(List.of(review));

        List<Review> result = reviewService.getReviewsByBookId(bookId);

        assertEquals(1, result.size());
        assertEquals("Dasha", result.get(0).getReviewerName());
        assertEquals(5, result.get(0).getRating());

        verify(reviewRepository).findByBookId(bookId);
    }

    @Test
    void shouldCreateReviewForExistingBook() {
        Long bookId = 2L;

        CreateReviewRequest request = new CreateReviewRequest(
                bookId,
                " Dasha ",
                5,
                " Great book! "
        );

        when(bookClient.bookExists(bookId)).thenReturn(true);

        when(reviewRepository.save(any(Review.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Review result = reviewService.createReview(request);

        assertEquals(bookId, result.getBookId());
        assertEquals("Dasha", result.getReviewerName());
        assertEquals(5, result.getRating());
        assertEquals("Great book!", result.getComment());

        verify(bookClient).bookExists(bookId);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void shouldRejectReviewForNonexistentBook() {
        Long bookId = 99999L;

        CreateReviewRequest request = new CreateReviewRequest(
                bookId,
                "Test",
                5,
                "Testing nonexistent book"
        );

        when(bookClient.bookExists(bookId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reviewService.createReview(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(bookClient).bookExists(bookId);
        verify(reviewRepository, never()).save(any(Review.class));
        assertEquals(
            "Book with ID 99999 does not exist",
            exception.getReason()
        );
    }

    @Test
    void shouldDeleteExistingReview() {
        Long reviewId = 1L;

        when(reviewRepository.existsById(reviewId)).thenReturn(true);

        boolean result = reviewService.deleteReview(reviewId);

        assertTrue(result);

        verify(reviewRepository).existsById(reviewId);
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void shouldNotDeleteNonexistentReview() {
        Long reviewId = 99999L;

        when(reviewRepository.existsById(reviewId)).thenReturn(false);

        boolean result = reviewService.deleteReview(reviewId);

        assertFalse(result);

        verify(reviewRepository).existsById(reviewId);
        verify(reviewRepository, never()).deleteById(anyLong());
    }
}
