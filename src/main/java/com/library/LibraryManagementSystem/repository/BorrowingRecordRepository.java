package com.library.LibraryManagementSystem.repository;

import com.library.LibraryManagementSystem.model.Book;
import com.library.LibraryManagementSystem.model.BorrowingRecord;
import com.library.LibraryManagementSystem.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {
    List<BorrowingRecord> findByMemberAndReturnDateIsNull(Member member);

    // This is the query for overdue books!
    @Query("SELECT br FROM BorrowingRecord br WHERE br.returnDate IS NULL AND br.dueDate < ?1")
    List<BorrowingRecord> findOverdueBooks(LocalDate today);


    List<BorrowingRecord> findByMemberOrderByBorrowDateDesc(Member member);

    // *** ADD THIS METHOD ***
    // Finds a book's entire history, sorted by the most recent borrow date
    List<BorrowingRecord> findByBookOrderByBorrowDateDesc(Book book);
}