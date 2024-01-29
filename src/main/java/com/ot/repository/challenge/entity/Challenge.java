package com.ot.repository.challenge.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ot.client.code.ChallengeCode;
import com.ot.repository.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document("challenge")
public class Challenge {

    @Id
    private String challengeId;

    @JsonIgnore
    private String memberSeq;

    @DocumentReference(lazy = true)
    private Member member;

    private String title;
    private String description;
    private Boolean exposed;

    private ChallengeCode.Status status = ChallengeCode.Status.ING;

    private Long totalCount = 0L;
    private Long doneCount = 0L;
    private Long totalRunTime = 0L;
    private Long doneRunTime = 0L;

    @CreatedDate
    private LocalDateTime createDate;
    @LastModifiedDate
    private LocalDateTime updateDate;
    private LocalDateTime doneDate;
    private Boolean isNotiDone = false;
    private LocalDateTime isNotiDoneDate;

    private LocalDateTime lastContentDoneDate;

    @DocumentReference(lazy = true, sort = "{ \"seq\": 1 }")
    private List<ChallengeContent> contents = new ArrayList<>();

    private Long readCount;


    public void init(Member member, String title, String description, Boolean exposed) {
        this.member = member;
        this.memberSeq = member.getMemberSeq();
        this.title = title;
        this.description = description;
        this.exposed = exposed;
        this.readCount = 0L;
    }

    public void updateInfo(String title, String description, Boolean exposed) {
        this.title = title;
        this.description = description;
        this.exposed = exposed;
    }

    public void updateContents(List<ChallengeContent> contents) {
        this.contents = contents;
        this.isCheckDoneAll();
    }

    public void isCheckDoneAll() {
        boolean isDoneAll = this.getContents().stream().allMatch(ChallengeContent::getIsDone);
        if (isDoneAll) {
            this.updateStatus(ChallengeCode.Status.DONE);
        }
        this.updateContentInfo();
    }

    public void updateContentInfo() {
        AtomicLong totalCount = new AtomicLong(0L);
        AtomicLong doneCount = new AtomicLong(0L);
        AtomicLong totalRunTime = new AtomicLong(0L);
        AtomicLong doneRunTime = new AtomicLong(0L);
        this.contents.forEach(
                content -> {
                    totalCount.incrementAndGet();
                    totalRunTime.addAndGet(content.getRunTime());
                    if (content.getIsDone()) {
                        doneRunTime.addAndGet(content.getRunTime());
                        doneCount.getAndIncrement();
                    }
                }
        );
        this.totalCount = totalCount.get();
        this.doneCount = doneCount.get();
        this.totalRunTime = totalRunTime.get();
        this.doneRunTime = doneRunTime.get();
    }

    public void addContent(ChallengeContent content) {
        this.contents.add(content);
        this.isCheckDoneAll();
    }

    public void updateStatus(ChallengeCode.Status status) {
        this.status = status;
        if (status == ChallengeCode.Status.ING) {
            this.doneDate = null;
        } else {
            this.doneDate = LocalDateTime.now();
        }
    }

    public void removeContent(String contentId) {
        this.contents = this.contents.stream()
                .filter(content -> !content.getChallengeContentId().equals(contentId))
                .collect(Collectors.toList());
        this.isCheckDoneAll();
    }

    public void updateLastContentDoneDate(LocalDateTime doneDate) {
        this.lastContentDoneDate = doneDate;
    }

    public void updateIsNotiDone() {
        this.isNotiDone = true;
        this.isNotiDoneDate = LocalDateTime.now();
    }

    public void increaseReadCount() {
        this.readCount++;
    }
}
