package com.ot.client.model.search.multi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultiRuntimeReq {

    private List<RuntimeReq> runtimes;

    @Getter
    @Setter
    public static class RuntimeReq {

        private String contentsType;

        private String contentsId;

    }

}
