package com.ot.repository.common;

import com.ot.repository.common.entity.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface FileRepository extends MongoRepository<File, String> {
    Optional<File> findOneByFileId(String fileId);
}
