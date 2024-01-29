package com.ot.repository.contents;

import com.ot.repository.contents.entity.Genre;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GenreRepository extends MongoRepository<Genre, Integer> {

    Optional<Genre> findByGenreIdAndGenreType(String genreId, String genreType);

}
