import type { Review, CreateReviewRequest } from "../types/review";

export async function fetchReviews(bookId: number): Promise<Review[]> {
  const response = await fetch(`/api/v1/reviews?bookId=${bookId}`);

  if (!response.ok) {
    throw new Error("Failed to fetch reviews");
  }

  return response.json() as Promise<Review[]>;
}

export async function createReview(
  request: CreateReviewRequest,
  token: string,
): Promise<Review> {
  const response = await fetch("/api/v1/reviews", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("Your session has expired. Please sign in again.");
    }

    if (response.status === 403) {
      throw new Error("You don't have permission to submit reviews.");
    }

    throw new Error("Could not submit your review. Please try again.");
  }

  return response.json() as Promise<Review>;
}
