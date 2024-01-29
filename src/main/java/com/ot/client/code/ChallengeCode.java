package com.ot.client.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ChallengeCode {

    @Getter
    @AllArgsConstructor
    public enum Status {
        ING("도전중"),
        DONE("도전완료");

        private final String description;
    }

    @Getter
    public enum ProviderType {
        rent, buy, flatrate
    }

}
