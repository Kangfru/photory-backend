package com.ot.repository.challenge.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ot.repository.challenge.entity.nested.ChallengeContentProvider;
import com.ot.repository.member.entity.Member;
import com.ot.repository.photo_tikcet.entity.PhotoTicket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document("challengeContent")
public class ChallengeContent {

    @Id
    private String challengeContentId;
    private String contentsId;
    private String contentsType;
    private String title;
    private String originalTitle;
    private String posterPath;
    private String backdropPath;

    private String challengeId;

    @DocumentReference(lazy = true)
    private Challenge challenge;

    @JsonIgnore
    private String memberSeq;

    @DocumentReference(lazy = true)
    private Member member;

    private Boolean isDone = false;
    private LocalDateTime doneDate;

    @CreatedDate
    private LocalDateTime createDate;
    @LastModifiedDate
    private LocalDateTime updateDate;

    private Long runTime = 0L;

    private Integer seq;

    @DocumentReference(lazy = true)
    private PhotoTicket photoTicket;

    private List<ChallengeContentProvider> providers = new ArrayList<>();


    public void init(Challenge challenge, Member member, String contentsId, String contentsType, String title, String originalTitle,
                     String posterPath, String backdropPath, Long runTime, Integer seq) {
        this.challenge = challenge;
        this.challengeId = challenge.getChallengeId();
        this.member = member;
        this.memberSeq = member.getMemberSeq();
        this.contentsId = contentsId;
        this.contentsType = contentsType;
        this.title = title;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.runTime = runTime;
        this.seq = seq;

    }

    public void updateDone(PhotoTicket photoTicket) {
        this.photoTicket = photoTicket;
        if (photoTicket != null) {
//            this.doneDate = photoTicket.getRegDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            this.doneDate = photoTicket.getRegDate();
            this.isDone = true;
        } else {
            this.doneDate = null;
            this.isDone = false;
        }
    }

    public void updateSeq(Integer seq) {
        this.seq = seq;
    }

    public void updateProviders(List<ChallengeContentProvider> providers) {
        this.providers = providers;

    }
}
