package com.ot.repository.challenge.entity;

import com.ot.client.code.ChallengeCode;
import com.ot.repository.challenge.entity.nested.ChallengeContentProvider;
import com.ot.repository.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@EqualsAndHashCode(exclude = {
        "displayPriority", "logoPath", "providerName", "member", "totalCount", "doneCount",
        "totalRunTime", "doneRunTime"
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document("challengeRecord")
public class ChallengeRecord {

    @Id
    private String challengeRecordId;

    private String memberSeq;
    private ChallengeCode.ProviderType type;
    private String providerId;
    private int displayPriority;
    private String logoPath;
    private String providerName;

    @DocumentReference(lazy = true)
    private Member member;

    private Long totalCount = 0L;
    private Long doneCount = 0L;
    private Long totalRunTime = 0L;
    private Long doneRunTime = 0L;

    public void init(Member member, ChallengeContentProvider provider) {
        this.member = member;
        this.memberSeq = member.getMemberSeq();
        this.type = provider.getType();
        this.providerId = provider.getProviderId();
        this.providerName = provider.getProviderName();
        this.displayPriority = provider.getDisplayPriority();
        this.logoPath = provider.getLogoPath();
        this.challengeRecordId = this.generateId();
    }

    private String generateId() {
        return String.format("%s_%s_%s", this.memberSeq, this.type, this.providerId);
    }

    public static String generateId(String memberSeq, ChallengeCode.ProviderType type, String providerId) {
        return String.format("%s_%s_%s", memberSeq, type, providerId);
    }

    public void increaseTotalInfo(ChallengeContent content) {
        this.totalCount++;
        this.totalRunTime += content.getRunTime();
    }

    public void decreaseTotalInfo(ChallengeContent content) {
        this.totalCount--;
        this.totalRunTime -= content.getRunTime();
    }

    public void increaseDoneInfo(ChallengeContent content) {
        if (content.getIsDone()) {
            this.doneCount++;
            this.doneRunTime += content.getRunTime();
        }
    }

    public void decreaseDoneInfo(ChallengeContent content) {
        if (content.getIsDone()) {
            this.doneCount--;
            this.doneRunTime -= content.getRunTime();
        }
    }
}
