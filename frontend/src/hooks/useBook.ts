import { useQuery } from "@tanstack/react-query";
import { fetchBookById } from "../api/books";

export function useBook(id: number) {
  return useQuery({
    queryKey: ["books", id],
    queryFn: () => fetchBookById(id),
    enabled: Number.isSafeInteger(id) && id > 0,
    staleTime: 60_000,
    retry: (failureCount, error) =>
      error.message !== "Book not found" && failureCount < 2,
  });
}
