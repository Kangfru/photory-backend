package com.ot.client.model.search.multi;

import com.ot.client.model.CommonRes;
import com.ot.model.CommonResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultiRuntimeRes extends CommonResponse {

    private List<RuntimeRes> runtimes;

    @Getter
    @Setter
    public static class RuntimeRes {
        private String contentsType;

        private String contentsId;

        private long runtime;
    }
}
