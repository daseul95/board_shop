package com.shop.repository;


import com.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Member findByEmail(String email);
    Member findByName(String name);
    Boolean existsByName(String name);
    Boolean existsByEmail(String email);

}
