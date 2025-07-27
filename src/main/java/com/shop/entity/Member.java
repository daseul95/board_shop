package com.shop.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="member")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity implements UserDetails {
    private List<GrantedAuthority> authorities;

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique= true)
    private String email;

    @OneToMany(mappedBy = "writerId",cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
    private List<Board> boards;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Nullable
    private String refreshToken;


    public static Member createMember(MemberFormDto memberFormDto
    , PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password= passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.ADMIN);
        return member;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public String getEmail(){
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword(){
        return this.password;
    }



}
