import { useEffect, useState } from "react";
import { BookCard } from "../components/BookCard";
import { useBooks } from "../hooks/useBooks";

export function BooksPage() {
  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");

  useEffect(() => {
    const timeout = setTimeout(() => {
      setDebouncedSearch(search);
    }, 300);

    return () => clearTimeout(timeout);
  }, [search]);

  const {
    data: books = [],
    isPending,
    isError,
    error,
  } = useBooks(debouncedSearch);

  return (
    <main className="app">
      <header>
        <h1>BookNest 📚</h1>
        <p>Discover your next favorite book.</p>
      </header>

      <input
        className="search-input"
        type="search"
        aria-label="Search books"
        placeholder="Search by title or author..."
        value={search}
        onChange={(event) => setSearch(event.target.value)}
      />

      {isPending && <p>Loading books...</p>}
      {isError && <p>Failed to load books: {error.message}</p>}

      {!isPending && !isError && (
        <>
          <p className="results-count">
            {books.length} {books.length === 1 ? "book" : "books"} found
          </p>

          <section className="books-grid">
            {books.map((book) => (
              <BookCard key={book.id} book={book} />
            ))}
          </section>

          {books.length === 0 && <p>No books found.</p>}
        </>
      )}
    </main>
  );
}
