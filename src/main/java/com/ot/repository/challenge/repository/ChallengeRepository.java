package com.ot.repository.challenge.repository;

import com.ot.client.code.ChallengeCode;
import com.ot.repository.challenge.entity.Challenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends MongoRepository<Challenge, String> {

    Page<Challenge> findByMemberSeqOrderByLastContentDoneDateDescUpdateDateDescCreateDateDesc(String memberSeq, Pageable pageable);

    Page<Challenge> findByMemberSeqAndStatusOrderByLastContentDoneDateDescUpdateDateDescCreateDateDesc(String memberSeq, ChallengeCode.Status status, Pageable pageable);

    Long countByMemberSeq(String memberSeq);

    Long countByMemberSeqAndStatus(String memberSeq, ChallengeCode.Status status);

    List<Challenge> findByMemberSeq(String memberSeq);

    List<Challenge> findByMemberSeqAndStatusAndIsNotiDone(String memberSeq, ChallengeCode.Status status, boolean isNotiDone);

    Page<Challenge> findByChallengeIdInAndExposedOrderByCreateDateDesc(List<String> ids, boolean exposed, Pageable pageable);

}
