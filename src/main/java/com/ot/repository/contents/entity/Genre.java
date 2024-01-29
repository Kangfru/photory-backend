package com.ot.repository.contents.entity;

import com.ot.client.model.search.ResultInterface;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;


@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Document("genre")
public class Genre {

    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String _id;

    @Schema(name = "genreId", description = "장르 아이디")
    private String genreId;

    @Schema(name = "genreName", description = "장르 명")
    private String genreName;


    @Schema(name = "genreName", description = "장르 타입, (tv or movie)")
    private String genreType;

    private List<ResultInterface> results;

    public void changeResults(List<ResultInterface> results) {
        this.results = results;
    }

}
