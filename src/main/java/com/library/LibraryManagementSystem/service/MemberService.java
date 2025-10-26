package com.library.LibraryManagementSystem.service;

import com.library.LibraryManagementSystem.model.Member;
import com.library.LibraryManagementSystem.model.Role;
import com.library.LibraryManagementSystem.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // This method is required by UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Spring Security will call this to find a user by their email
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    // New method for registering a member
    public Member registerNewMember(String name, String email, String password) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        Member member = new Member();
        member.setName(name);
        member.setEmail(email);
        // CRITICAL: Always encode the password!
        member.setPassword(passwordEncoder.encode(password));
        member.setRole(Role.ROLE_MEMBER); // Default role

        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        try {
            memberRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete member. They may have active borrowed books.");
        }
    }

    public Member createMemberByAdmin(Member newMember) {
        if (memberRepository.findByEmail(newMember.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        // Admin form provides all details, including role
        newMember.setPassword(passwordEncoder.encode(newMember.getPassword()));
        return memberRepository.save(newMember);
    }

    // You'll also need to update your MemberRepository to add this method:
    // Optional<Member> findByEmail(String email);
}