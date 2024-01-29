package com.ot.repository.challenge.repository;

import com.ot.repository.challenge.entity.ChallengeContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeContentRepository extends MongoRepository<ChallengeContent, String> {
    List<ChallengeContent> findByContentsIdAndContentsTypeAndMemberSeqNot(String contentsId, String contentsType, String memberSeq);

    List<ChallengeContent> findByChallengeIdOrderBySeqAsc(String challengeId);

    void deleteAllByChallengeIdIn(List<String> challengeIds);

    boolean existsByContentsIdAndContentsTypeAndMemberSeq(String contentsId, String contentsType, String memberSeq);

    List<ChallengeContent> findByChallengeIdIn(List<String> challengeIds);

    List<ChallengeContent> findByMemberSeq(String memberSeq);
}
