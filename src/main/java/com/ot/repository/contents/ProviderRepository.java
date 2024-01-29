package com.ot.repository.contents;

import com.ot.repository.contents.entity.Provider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


public interface ProviderRepository extends MongoRepository<Provider, Integer> {
}
