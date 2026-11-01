import { Link } from "react-router";
import type { Book } from "../types/book";

interface BookCardProps {
  book: Book;
}

export function BookCard({ book }: BookCardProps) {
  return (
    <Link to={`/books/${book.id}`} className="book-card-link">
      <article className="book-card">
        <span className="book-genre">{book.genre}</span>
        <h2>{book.title}</h2>
        <p className="book-author">by {book.author}</p>
        <p>{book.description}</p>
      </article>
    </Link>
  );
}
