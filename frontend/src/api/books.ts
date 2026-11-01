import type { Book } from "../types/book";

export async function fetchBooks(search = ""): Promise<Book[]> {
  const params = new URLSearchParams();

  if (search.trim()) {
    params.set("search", search.trim());
  }

  const query = params.toString();
  const url = `/api/v1/books${query ? `?${query}` : ""}`;

  const response = await fetch(url);

  if (!response.ok) {
    throw new Error(`Failed to fetch books: ${response.status}`);
  }

  return response.json() as Promise<Book[]>;
}

export async function fetchBookById(id: number): Promise<Book> {
  const response = await fetch(`/api/v1/books/${id}`);

  if (!response.ok) {
    throw new Error(
      response.status === 404
        ? "Book not found"
        : `Failed to fetch book: ${response.status}`,
    );
  }

  return response.json() as Promise<Book>;
}
