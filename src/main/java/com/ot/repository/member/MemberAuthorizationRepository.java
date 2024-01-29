package com.ot.repository.member;

import com.ot.repository.member.entity.MemberAuthorization;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface MemberAuthorizationRepository extends MongoRepository<MemberAuthorization, String> {


    List<MemberAuthorization> findAllByEmail(String email);

    List<MemberAuthorization> findAllByEmailAndType(String email, String type);

    Optional<MemberAuthorization> findByAuthorizationKey(String authorizationKey);

    Optional<MemberAuthorization> findByAuthorizationKeyAndType(String authorizationKey, String type);
}
