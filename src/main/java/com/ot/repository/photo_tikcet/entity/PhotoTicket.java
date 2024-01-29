package com.ot.repository.photo_tikcet.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Document("photoTickets")
@Setter
public class PhotoTicket {

    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String photoTicketId;

    @Field(targetType = FieldType.OBJECT_ID)
    private String memberSeq;

    // movie or tv type
    private String contentsId;

    // movie or tv
    private String contentsType;

    private String contentsDetail;

    private LocalDate viewDate;

    private String comment;

    private int rating;

    @Field(targetType = FieldType.OBJECT_ID)
    private String fileId;

    private LocalDateTime regDate;

    private String detailComment;

    private boolean exposed;

    private List<Giphy> giphyList;

    public void changeComment(String comment) {
        this.comment = comment;
    }

    public void changeDetailComment(String detailComment) {
        this.detailComment = detailComment;
    }

    public void changeFileId(String fileId) {
        this.fileId = fileId;
    }

    public void changeRating(int rating) {
        this.rating = rating;
    }

    public void changeExposed(boolean exposed) {
        this.exposed = exposed;
    }

    public void changeGiphyList(List<Giphy> giphyList) {
        this.giphyList = giphyList;
    }
}
