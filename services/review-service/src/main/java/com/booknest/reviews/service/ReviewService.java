package com.booknest.reviews.service;

import com.booknest.reviews.client.BookClient;
import com.booknest.reviews.dto.CreateReviewRequest;
import com.booknest.reviews.model.Review;
import com.booknest.reviews.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookClient bookClient;

    public ReviewService(
            ReviewRepository reviewRepository,
            BookClient bookClient
    ) {
        this.reviewRepository = reviewRepository;
        this.bookClient = bookClient;
    }

    public List<Review> getReviewsByBookId(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    public Review createReview(CreateReviewRequest request) {
        if (!bookClient.bookExists(request.bookId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Book with ID " + request.bookId() + " does not exist"
            );
        }

        Review review = new Review();

        review.setBookId(request.bookId());
        review.setReviewerName(request.reviewerName().trim());
        review.setRating(request.rating());
        review.setComment(request.comment().trim());

        return reviewRepository.save(review);
    }

    public boolean deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            return false;
        }

        reviewRepository.deleteById(id);
        return true;
    }
}
