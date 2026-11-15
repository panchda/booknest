import { useState } from "react";
import type { Book, BookInput } from "../types/book";
import { BookForm } from "./BookForm";
import {
  useCreateBook,
  useUpdateBook,
  useDeleteBook,
} from "../hooks/useBookMutations";

interface AdminBookManagerProps {
  books: Book[];
}

export function AdminBookManager({ books }: AdminBookManagerProps) {
  const [editingBook, setEditingBook] = useState<Book | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const createMutation = useCreateBook();
  const updateMutation = useUpdateBook();
  const deleteMutation = useDeleteBook();

  function closeForm() {
    setIsCreating(false);
    setEditingBook(null);
    createMutation.reset();
    updateMutation.reset();
  }

  function handleCreate(data: BookInput) {
    createMutation.mutate(data, {
      onSuccess: closeForm,
    });
  }

  function handleUpdate(data: BookInput) {
    if (!editingBook) return;

    updateMutation.mutate(
      { id: editingBook.id, data },
      { onSuccess: closeForm },
    );
  }

  function handleDelete(id: number) {
    if (!window.confirm("Are you sure you want to delete this book?")) {
      return;
    }

    setDeletingId(id);

    deleteMutation.mutate(id, {
      onSettled: () => setDeletingId(null),
    });
  }

  const isFormOpen = isCreating || editingBook !== null;

  return (
    <section className="admin-books">
      <div className="admin-books-header">
        <h2>Manage Books</h2>
        <button
          type="button"
          onClick={() => {
            setEditingBook(null);
            setIsCreating(true);
            createMutation.reset();
          }}
          disabled={isFormOpen}
        >
          + Add Book
        </button>
      </div>

      {isFormOpen && (
        <BookForm
          key={editingBook?.id ?? "new"}
          initialValues={
            editingBook
              ? {
                  title: editingBook.title,
                  author: editingBook.author,
                  genre: editingBook.genre,
                  description: editingBook.description,
                }
              : undefined
          }
          isPending={createMutation.isPending || updateMutation.isPending}
          error={createMutation.error?.message || updateMutation.error?.message}
          onSubmit={isCreating ? handleCreate : handleUpdate}
          onCancel={closeForm}
        />
      )}

      {deleteMutation.isError && (
        <p role="alert">{deleteMutation.error.message}</p>
      )}

      <div className="admin-books-list">
        {books.map((book) => (
          <div key={book.id} className="admin-book-row">
            <div>
              <strong>{book.title}</strong>
              <p>{book.author}</p>
            </div>

            <div className="admin-book-actions">
              <button
                type="button"
                onClick={() => {
                  setIsCreating(false);
                  setEditingBook(book);
                  updateMutation.reset();
                }}
                disabled={isFormOpen || deletingId !== null}
              >
                Edit
              </button>

              <button
                type="button"
                onClick={() => handleDelete(book.id)}
                disabled={deletingId !== null || isFormOpen}
              >
                {deletingId === book.id ? "Deleting..." : "Delete"}
              </button>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
