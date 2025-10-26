package com.library.LibraryManagementSystem.controller;

import com.library.LibraryManagementSystem.model.Book;
import com.library.LibraryManagementSystem.model.BorrowingRecord;
import com.library.LibraryManagementSystem.model.Member;
import com.library.LibraryManagementSystem.service.LibraryService;
import com.library.LibraryManagementSystem.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

import java.util.List;

@Controller
@RequestMapping("/admin") // All URLs in this controller start with /admin
public class AdminController {

    @Autowired
    private LibraryService libraryService; // Your original service

    @Autowired
    private MemberService memberService;

    // ... in AdminController.java
    @GetMapping("/dashboard")
    public String adminDashboard(Model model,@RequestParam(required = false) String keyword) {
        List<Book> allBooks;
        // --- ADD THIS SEARCH LOGIC ---
        if (keyword != null && !keyword.isEmpty()) {
            allBooks = libraryService.searchBooks(keyword);
            model.addAttribute("keyword", keyword); // Add keyword back to model
        } else {
            allBooks = libraryService.findAllBooks();
        }
        // --- END OF SEARCH LOGIC ---
        model.addAttribute("books", libraryService.findAllBooks());
        model.addAttribute("members", libraryService.findAllMembers());
        model.addAttribute("overdueBooks", libraryService.findOverdueBooks());
        model.addAttribute("newBook", new Book());

        // *** ADD THIS LINE ***
        model.addAttribute("allRecords", libraryService.findAllBorrowingRecords());

        // For the "Add Member" form we'll create next
        model.addAttribute("newMember", new Member());

        // Use the 'allBooks' variable (which is now filtered or full)
        model.addAttribute("books", allBooks);

        return "admin-dashboard";
    }

    // *** ADD THIS ENTIRE NEW METHOD ***
    @GetMapping("/books/details/{id}")
    public String showBookDetails(@PathVariable Long id, Model model) {
        // 1. Find the book
        Book book = libraryService.findBookById(id);

        // 2. Find its history
        List<BorrowingRecord> history = libraryService.findHistoryForBook(book);

        // 3. Add both to the model
        model.addAttribute("book", book);
        model.addAttribute("history", history);
        model.addAttribute("today", LocalDate.now()); // <-- ADD THIS LINE

        return "book-details"; // <-- A new HTML file we will create
    }

    // ... in AdminController.java

    @GetMapping("/members/history/{id}")
    public String showMemberHistory(@PathVariable Long id, Model model) {
        Member member = libraryService.findMemberById(id);
        List<BorrowingRecord> history = libraryService.findHistoryForMember(member);

        model.addAttribute("member", member);
        model.addAttribute("history", history);
        model.addAttribute("today", LocalDate.now()); // <-- ADD THIS LINE

        return "member-history"; // A new HTML file
    }
    // --- Book Management ---
    @PostMapping("/books/add")
    public String addBook(Book book) {
        libraryService.saveBook(book);
        return "redirect:/admin/dashboard";
    }

    // ... in AdminController.java

    // 1. Show the "Edit Book" page
    @GetMapping("/books/edit/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        Book book = libraryService.findBookById(id); // You need to create findBookById in LibraryService
        model.addAttribute("book", book);
        return "edit-book"; // A new HTML file we will create
    }

    // 2. Process the "Edit Book" form submission
    @PostMapping("/books/update")
    public String updateBook(@ModelAttribute("book") Book book) {
        // We must preserve the availableQuantity.
        // A better way would be a DTO, but this is simpler for now.
        Book existingBook = libraryService.findBookById(book.getId());
        book.setAvailableQuantity(existingBook.getAvailableQuantity());

        libraryService.saveBook(book); // save() works for both new and existing entities
        return "redirect:/admin/dashboard";
    }

    // 3. Delete a book
    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        try {
            libraryService.deleteBook(id);
            return "redirect:/admin/dashboard";
        } catch (RuntimeException ex) {
            return "redirect:/admin/dashboard?error=" + ex.getMessage();
        }
    }

    @PostMapping("/members/add")
    public String addMemberByAdmin(@ModelAttribute("newMember") Member newMember) {
        try {
            memberService.createMemberByAdmin(newMember);
            return "redirect:/admin/dashboard";
        } catch (RuntimeException ex) {
            return "redirect:/admin/dashboard?error=" + ex.getMessage();
        }
    }

    @GetMapping("/members/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        try {
            memberService.deleteMember(id); // Assumes you'll move deleteMember to LibraryService or autowire MemberService here
            return "redirect:/admin/dashboard";
        } catch (RuntimeException ex) {
            return "redirect:/admin/dashboard?error=" + ex.getMessage();
        }
    }

    // Add @GetMapping("/books/edit/{id}") and @GetMapping("/books/delete/{id}") here

    // --- Member Management ---
    // Add @GetMapping("/members/delete/{id}") etc. here
}