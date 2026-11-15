import { useState, type FormEvent } from "react";
import { useCreateReview, useReviews } from "../hooks/useReviews";
import { Link } from "react-router";
import { useAuthStore } from "../stores/useAuthStore";

interface ReviewsSectionProps {
  bookId: number;
}

export function ReviewsSection({ bookId }: ReviewsSectionProps) {
  const token = useAuthStore((state) => state.token);
  const user = useAuthStore((state) => state.user);
  const { data: reviews = [], isPending, isError } = useReviews(bookId);
  const createReview = useCreateReview(bookId);

  const [reviewerName, setReviewerName] = useState("");
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");

  const averageRating = reviews.length
    ? reviews.reduce((sum, review) => sum + review.rating, 0) / reviews.length
    : null;

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!token || !user) return;
    if (!reviewerName.trim() || !comment.trim()) return;

    try {
      await createReview.mutateAsync({
        bookId,
        reviewerName: reviewerName.trim(),
        rating,
        comment: comment.trim(),
      });

      setReviewerName("");
      setRating(5);
      setComment("");
    } catch {
      // The mutation error is displayed below the form.
    }
  }

  return (
    <section className="reviews-section">
      <h2>Reviews & Ratings</h2>

      {isPending && <p>Loading reviews...</p>}
      {isError && <p>Failed to load reviews.</p>}

      {!isPending && !isError && (
        <>
          <div className="rating-summary">
            <span className="rating-stars">
              {"★".repeat(Math.round(averageRating ?? 0))}
              {"☆".repeat(5 - Math.round(averageRating ?? 0))}
            </span>
            <strong>
              {averageRating === null
                ? "No ratings yet"
                : averageRating.toFixed(1)}
            </strong>
            <span>
              ({reviews.length} {reviews.length === 1 ? "review" : "reviews"})
            </span>
          </div>

          {reviews.length === 0 && <p>Be the first to review this book!</p>}

          <div className="reviews-list">
            {reviews.map((review) => (
              <article className="review-item" key={review.id}>
                <div className="review-header">
                  <strong>{review.reviewerName}</strong>
                  <span className="rating-stars">
                    {"★".repeat(review.rating)}
                    {"☆".repeat(5 - review.rating)}
                  </span>
                </div>
                <p>{review.comment}</p>
              </article>
            ))}
          </div>
        </>
      )}

      {token && user ? (
        <form className="review-form" onSubmit={handleSubmit}>
          <h3>Write a review</h3>

          <label htmlFor="reviewer-name">Your name</label>
          <input
            id="reviewer-name"
            value={reviewerName}
            onChange={(event) => setReviewerName(event.target.value)}
            maxLength={100}
            required
          />

          <label htmlFor="review-rating">Your rating</label>
          <select
            id="review-rating"
            value={rating}
            onChange={(event) => setRating(Number(event.target.value))}
          >
            {[5, 4, 3, 2, 1].map((value) => (
              <option key={value} value={value}>
                {value} {value === 1 ? "star" : "stars"}
              </option>
            ))}
          </select>

          <label htmlFor="review-comment">Your review</label>
          <textarea
            id="review-comment"
            value={comment}
            onChange={(event) => setComment(event.target.value)}
            maxLength={2000}
            rows={4}
            required
          />

          {createReview.isError && (
            <p role="alert">{createReview.error.message}</p>
          )}

          <button type="submit" disabled={createReview.isPending}>
            {createReview.isPending ? "Submitting..." : "Submit review"}
          </button>
        </form>
      ) : (
        <div className="review-form">
          <h3>Want to share your thoughts?</h3>
          <p>Sign in to write a review for this book.</p>
          <Link
            to="/login"
            state={{ from: `/books/${bookId}` }}
            className="review-login-link"
          >
            Sign in to write a review
          </Link>
        </div>
      )}
    </section>
  );
}
