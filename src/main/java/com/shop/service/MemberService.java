package com.shop.service;


import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService implements UserDetailsService {
    @Autowired
    private MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member= memberRepository.findByEmail(email);
        System.out.println("email: " + email);
        System.out.println("Member email: " + member.getEmail());
        System.out.println("encodedPassword: " + member.getPassword());
        if(member==null){
            throw new UsernameNotFoundException(email);
        }

        return member;
    }

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    public Member resaveMember(Member member){
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember!=null){
            throw new IllegalStateException("이미 가입된 회원입니다");
        }
    }

    public Member findByEmail(String email){
        return memberRepository.findByEmail(email);
    }

    public Member findByName(String name){
        return memberRepository.findByName(name);
    }
}
