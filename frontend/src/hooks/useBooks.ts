import { useQuery } from "@tanstack/react-query";
import { fetchBooks } from "../api/books";

export function useBooks(search = "") {
  return useQuery({
    queryKey: ["books", search],
    queryFn: () => fetchBooks(search),
    staleTime: 60_000,
  });
}
