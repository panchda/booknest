import type { Book, BookInput } from "../types/book";

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

async function bookMutation(
  url: string,
  method: "POST" | "PUT" | "DELETE",
  token: string,
  data?: BookInput,
): Promise<Response> {
  const response = await fetch(url, {
    method,
    headers: {
      Authorization: `Bearer ${token}`,
      ...(data ? { "Content-Type": "application/json" } : {}),
    },
    ...(data ? { body: JSON.stringify(data) } : {}),
  });

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("Your session has expired. Please sign in again.");
    }

    if (response.status === 403) {
      throw new Error("Administrator access is required.");
    }

    if (response.status === 404) {
      throw new Error("Book not found.");
    }

    throw new Error(`Request failed: ${response.status}`);
  }

  return response;
}

export async function createBook(
  data: BookInput,
  token: string,
): Promise<Book> {
  const response = await bookMutation("/api/v1/books", "POST", token, data);

  return response.json() as Promise<Book>;
}

export async function updateBook(
  id: number,
  data: BookInput,
  token: string,
): Promise<Book> {
  const response = await bookMutation(
    `/api/v1/books/${id}`,
    "PUT",
    token,
    data,
  );

  return response.json() as Promise<Book>;
}

export async function deleteBook(id: number, token: string): Promise<void> {
  await bookMutation(`/api/v1/books/${id}`, "DELETE", token);
}
