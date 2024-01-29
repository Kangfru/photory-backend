package com.ot.repository.challenge.repository;

import com.ot.client.code.ChallengeCode;
import com.ot.repository.challenge.entity.ChallengeRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRecordRepository extends MongoRepository<ChallengeRecord, String> {

    List<ChallengeRecord> findByMemberSeqAndType(String memberSeq, ChallengeCode.ProviderType type);

}
