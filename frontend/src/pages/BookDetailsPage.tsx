import { Link, useParams } from "react-router";
import { useBook } from "../hooks/useBook";

export function BookDetailsPage() {
  const { id } = useParams<{ id: string }>();
  const bookId = Number(id);
  const isValidId = Number.isSafeInteger(bookId) && bookId > 0;

  const { data: book, isPending, isError, error } = useBook(bookId);

  if (!isValidId) {
    return (
      <main className="app">
        <Link to="/" className="back-link">
          ← Back to books
        </Link>
        <p>Invalid book ID.</p>
      </main>
    );
  }

  return (
    <main className="app">
      <Link to="/" className="back-link">
        ← Back to books
      </Link>

      {isPending && <p>Loading book...</p>}
      {isError && <p>{error.message}</p>}

      {book && (
        <article className="book-details">
          <span className="book-genre">{book.genre}</span>
          <h1>{book.title}</h1>
          <p className="details-author">by {book.author}</p>

          <div className="details-divider" />

          <h2>About this book</h2>
          <p className="details-description">{book.description}</p>
        </article>
      )}
    </main>
  );
}
