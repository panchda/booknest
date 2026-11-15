package com.booknest.books.config;

import com.booknest.books.entity.Book;
import com.booknest.books.repository.BookRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BookDataInitializer {

    @Bean
    CommandLineRunner initializeBooks(BookRepository bookRepository) {
        return args -> {
            if (bookRepository.count() > 0) {
                return;
            }

            bookRepository.saveAll(List.of(
                createBook(
                    "1984",
                    "George Orwell",
                    "Dystopian",
                    "A dystopian novel about a totalitarian society."
                ),
                createBook(
                    "Pride and Prejudice",
                    "Jane Austen",
                    "Romance",
                    "A novel about love, class and social expectations."
                ),
                createBook(
                    "The Hobbit",
                    "J.R.R. Tolkien",
                    "Fantasy",
                    "A fantasy adventure about Bilbo Baggins."
                ),
                createBook(
                    "The Great Gatsby",
                    "F. Scott Fitzgerald",
                    "Classic",
                    "A novel about wealth and the American Dream."
                ),
                createBook(
                    "To Kill a Mockingbird",
                    "Harper Lee",
                    "Classic",
                    "A novel exploring justice and prejudice."
                )
            ));

            System.out.println("Demo books initialized successfully.");
        };
    }

    private Book createBook(
            String title,
            String author,
            String genre,
            String description
    ) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setDescription(description);
        return book;
    }
}
