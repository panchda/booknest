package com.booknest.books.controller;

import com.booknest.books.dto.BookRequest;
import com.booknest.books.entity.Book;
import com.booknest.books.service.BookService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> getAllBooks(
        @RequestParam(required = false) String search
    ) {
        return bookService.getAllBooks(search);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> createBook(
        @Valid @RequestBody BookRequest request
    ) {
        Book createdBook = bookService.createBook(request);

        return ResponseEntity
            .created(URI.create("/api/v1/books/" + createdBook.getId()))
            .body(createdBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
        @PathVariable Long id,
        @Valid @RequestBody BookRequest request
    ) {
        return bookService.updateBook(id, request)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBook(id);

        return deleted
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }
}
