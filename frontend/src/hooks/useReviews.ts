import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { createReview, fetchReviews } from "../api/reviews";
import { useAuthStore } from "../stores/useAuthStore";
import type { CreateReviewRequest } from "../types/review";

export function useReviews(bookId: number) {
  return useQuery({
    queryKey: ["reviews", bookId],
    queryFn: () => fetchReviews(bookId),
    enabled: Number.isSafeInteger(bookId) && bookId > 0,
  });
}

export function useCreateReview(bookId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: CreateReviewRequest) => {
      const token = useAuthStore.getState().token;

      if (!token) {
        throw new Error("Please sign in to write a review.");
      }

      return createReview(request, token);
    },

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["reviews", bookId],
      });
    },
  });
}
