package com.ot.repository.member;

import com.ot.repository.member.entity.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface MemberRepository extends MongoRepository<Member, String> {

    Optional<Member> findByEmailAndStatus(String email, String status);

    Optional<Member> findByNickName(String nickName);
}
