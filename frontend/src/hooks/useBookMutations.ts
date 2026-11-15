import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createBook, updateBook, deleteBook } from "../api/books";
import type { BookInput } from "../types/book";
import { useAuthStore } from "../stores/useAuthStore";

function useAdminToken() {
  const token = useAuthStore((state) => state.token);

  if (!token) {
    throw new Error("You must be signed in.");
  }

  return token;
}

export function useCreateBook() {
  const queryClient = useQueryClient();
  const token = useAdminToken();

  return useMutation({
    mutationFn: (data: BookInput) => createBook(data, token),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ["books"] });
    },
  });
}

export function useUpdateBook() {
  const queryClient = useQueryClient();
  const token = useAdminToken();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: BookInput }) =>
      updateBook(id, data, token),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ["books"] });
    },
  });
}

export function useDeleteBook() {
  const queryClient = useQueryClient();
  const token = useAdminToken();

  return useMutation({
    mutationFn: (id: number) => deleteBook(id, token),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ["books"] });
    },
  });
}
