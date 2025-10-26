package com.library.LibraryManagementSystem.controller;
// 1. Import RedirectAttributes at the top of your file
import com.library.LibraryManagementSystem.model.Book;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.library.LibraryManagementSystem.model.Member;
import com.library.LibraryManagementSystem.service.LibraryService;
import com.library.LibraryManagementSystem.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;  // <-- ADD THIS IMPORT
import org.slf4j.LoggerFactory; // <-- ADD THIS IMPORT

import java.util.List;

@Controller
@RequestMapping("/member") // All URLs in this controller start with /member
public class MemberController {

    @Autowired
    private LibraryService libraryService;

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    @GetMapping("/dashboard")
    public String memberDashboard(Model model, @AuthenticationPrincipal Member member, @RequestParam(required = false) String keyword) {

        List<Book> allBooks;
        // --- THIS IS THE NEW LOGIC ---
        if (keyword != null && !keyword.isEmpty()) {
            // If there is a search, use the search method
            allBooks = libraryService.searchBooks(keyword);
            // Add the keyword back to the model so the search bar doesn't go blank
            model.addAttribute("keyword", keyword);
        } else {
            // If there is no search, get all books
            allBooks = libraryService.findAllBooks();
        } // <-- FIX 1: Added the missing closing brace '}' for the 'else' block

        // --- These lines now run AFTER the if-else block ---

        // FIX 2: Use the 'allBooks' variable, which holds either the search results OR the full list
        model.addAttribute("allBooks", allBooks);

        // This line is correct and should be outside the if-else
        model.addAttribute("myBooks", libraryService.findActiveBorrowsByMember(member.getId()));

        return "member-dashboard";
    }



    // ...

    @PostMapping("/borrow")
    public String borrowBook(@RequestParam("bookId") Long bookId,
                             @AuthenticationPrincipal Member member,
                             RedirectAttributes redirectAttributes) { // 2. Add RedirectAttributes here

        log.info("Attempting to borrow book ID: {} for member ID: {}", bookId, member.getId());
        try {
            libraryService.borrowBook(bookId, member.getId());
            // 3. Add a success flash attribute
            log.info("borrowBook service call SUCCESSFUL");
            redirectAttributes.addFlashAttribute("success", "Book borrowed successfully!");
        } catch (RuntimeException ex) {
            // 4. Use flash attributes for errors too (it's cleaner)
            log.error("borrowBook service call FAILED: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/member/dashboard"; // 5. Always redirect to the same place
    }

    @PostMapping("/return")
    public String returnBook(@RequestParam("recordId") Long recordId) {
        libraryService.returnBook(recordId);
        return "redirect:/member/dashboard";
    }

}