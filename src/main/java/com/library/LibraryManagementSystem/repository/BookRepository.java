package com.library.LibraryManagementSystem.repository;

import com.library.LibraryManagementSystem.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query; // <-- ADD THIS IMPORT
import org.springframework.data.repository.query.Param; //
import java.util.List; // <-- ADD THIS IMPORT

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // You can add custom finders here, e.g.:
    // List<Book> findByAuthor(String author);
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.genre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchBooks(@Param("keyword") String keyword);
}