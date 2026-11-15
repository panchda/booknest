package com.booknest.reviews.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BookClient {

    private final RestClient restClient;

    public BookClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder builder
    ) {
        this.restClient = builder
                .baseUrl("http://book-service")
                .build();
    }

    @CircuitBreaker(
            name = "bookService",
            fallbackMethod = "bookServiceFallback"
    )
    public boolean bookExists(Long bookId) {
        try {
            restClient.get()
                    .uri("/api/v1/books/{id}", bookId)
                    .retrieve()
                    .toBodilessEntity();

            return true;
        } catch (HttpClientErrorException.NotFound exception) {
            return false;
        }
    }

    private boolean bookServiceFallback(
            Long bookId,
            Throwable exception
    ) {
        if (exception instanceof ResourceAccessException
                || exception instanceof HttpServerErrorException
                || exception instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Book Service is temporarily unavailable",
                    exception
            );
        }

        if (exception instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }

        throw new IllegalStateException(
                "Unexpected error while checking book " + bookId,
                exception
        );
    }
}
