export interface Review {
  id: number;
  bookId: number;
  reviewerName: string;
  rating: number;
  comment: string;
}

export interface CreateReviewRequest {
  bookId: number;
  reviewerName: string;
  rating: number;
  comment: string;
}
