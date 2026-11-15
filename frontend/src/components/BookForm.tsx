import { useState, type FormEvent } from "react";
import type { BookInput } from "../types/book";

interface BookFormProps {
  initialValues?: BookInput;
  isPending: boolean;
  error?: string;
  onSubmit: (data: BookInput) => void;
  onCancel: () => void;
}

const emptyBook: BookInput = {
  title: "",
  author: "",
  genre: "",
  description: "",
};

export function BookForm({
  initialValues = emptyBook,
  isPending,
  error,
  onSubmit,
  onCancel,
}: BookFormProps) {
  const [values, setValues] = useState<BookInput>(initialValues);

  function updateField(field: keyof BookInput, value: string) {
    setValues((current) => ({
      ...current,
      [field]: value,
    }));
  }

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    onSubmit({
      title: values.title.trim(),
      author: values.author.trim(),
      genre: values.genre.trim(),
      description: values.description.trim(),
    });
  }

  return (
    <form className="book-form" onSubmit={handleSubmit}>
      <h2>{initialValues === emptyBook ? "Add Book" : "Edit Book"}</h2>

      {(["title", "author", "genre"] as const).map((field) => (
        <label key={field}>
          <span>{field.charAt(0).toUpperCase() + field.slice(1)}</span>
          <input
            required
            value={values[field]}
            onChange={(event) => updateField(field, event.target.value)}
            disabled={isPending}
          />
        </label>
      ))}

      <label>
        <span>Description</span>
        <textarea
          required
          rows={4}
          value={values.description}
          onChange={(event) => updateField("description", event.target.value)}
          disabled={isPending}
        />
      </label>

      {error && <p role="alert">{error}</p>}

      <div className="book-form-actions">
        <button type="button" onClick={onCancel} disabled={isPending}>
          Cancel
        </button>

        <button type="submit" disabled={isPending}>
          {isPending ? "Saving..." : "Save Book"}
        </button>
      </div>
    </form>
  );
}
