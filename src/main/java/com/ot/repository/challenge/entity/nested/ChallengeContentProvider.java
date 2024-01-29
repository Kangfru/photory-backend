package com.ot.repository.challenge.entity.nested;

import com.ot.client.code.ChallengeCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeContentProvider {

    private ChallengeCode.ProviderType type;
    private int displayPriority;
    private String logoPath;
    private String providerName;
    private String providerId;

    public void init(ChallengeCode.ProviderType type, int displayPriority, String logoPath, String providerName,
                     String providerId) {
        this.type = type;
        this.displayPriority = displayPriority;
        this.logoPath = logoPath;
        this.providerName = providerName;
        this.providerId = providerId;
    }

}
