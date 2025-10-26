package com.library.LibraryManagementSystem.service;

import com.library.LibraryManagementSystem.model.Book;
import com.library.LibraryManagementSystem.model.BorrowingRecord;
import com.library.LibraryManagementSystem.model.Member;
import com.library.LibraryManagementSystem.repository.BookRepository;
import com.library.LibraryManagementSystem.repository.BorrowingRecordRepository;
import com.library.LibraryManagementSystem.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;  // <-- ADD THIS IMPORT
import org.slf4j.LoggerFactory; // <-- ADD THIS IMPORT

import java.time.LocalDate;
import java.util.List;

@Service
public class LibraryService {
    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;
    private static final Logger log = LoggerFactory.getLogger(LibraryService.class);


    public List<BorrowingRecord> findActiveBorrowsByMember(Long memberId) {
        // 1. First, we must fetch the "managed" Member entity from the database using its ID.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found in findActiveBorrowsByMember"));

        // 2. NOW we can use this managed 'member' object to find their books.
        return borrowingRecordRepository.findByMemberAndReturnDateIsNull(member);
    }

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;



    private static final int BORROW_DAYS = 14; // Books are due in 14 days

    // --- Book Methods ---
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }



    // --- Member Methods ---
    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    // ... in LibraryService.java

    public List<BorrowingRecord> findAllBorrowingRecords() {
        return borrowingRecordRepository.findAll();
    }

    public Member findMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public List<BorrowingRecord> findHistoryForMember(Member member) {
        return borrowingRecordRepository.findByMemberOrderByBorrowDateDesc(member);
    }

    // --- Borrowing Logic ---
    // ... in LibraryService.java

    public void borrowBook(Long bookId, Long memberId) {
        log.info("Inside borrowBook service. Finding book...");
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        log.info("Found book: {}", book.getTitle());
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        log.info("Found member: {}", member.getName());

        // *** UPDATED LOGIC HERE ***
        if (book.getAvailableQuantity() <= 0) {
            throw new RuntimeException("Book is not available");
        }

        // Decrement quantity
        log.info("Updating book quantity...");

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);
        log.info("Book quantity updated.");
        // *** END OF UPDATE ***

        BorrowingRecord record = new BorrowingRecord();
        // ... (rest of the method is the same)
        record.setBook(book);
        record.setMember(member);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(BORROW_DAYS));

        log.info("Saving new borrowing record...");
        borrowingRecordRepository.save(record);
        log.info("Borrowing record SAVED successfully with ID: {}", record.getId());
        // --- END OF LOGS ---


    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return bookRepository.findAll();
        }
        return bookRepository.searchBooks(keyword);
    }

    public List<BorrowingRecord> findHistoryForBook(Book book) {
        return borrowingRecordRepository.findByBookOrderByBorrowDateDesc(book);
    }

    public void returnBook(Long recordId) {
        BorrowingRecord record = borrowingRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Borrowing record not found"));

        record.setReturnDate(LocalDate.now());
        borrowingRecordRepository.save(record);

        // *** UPDATED LOGIC HERE ***
        Book book = record.getBook();
        // Increment quantity
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.save(book);
        // *** END OF UPDATE ***
    }

    // ... in LibraryService.java

    public void deleteBook(Long id) {
        // This will fail if a book is currently borrowed (due to database constraints),
        // which is good! It prevents data corruption.
        try {
            bookRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete book. It may be currently borrowed.");
        }
    }

    // In LibraryService.java
    public Book findBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    // --- Overdue Logic ---
    public List<BorrowingRecord> findOverdueBooks() {
        return borrowingRecordRepository.findOverdueBooks(LocalDate.now());
    }
}