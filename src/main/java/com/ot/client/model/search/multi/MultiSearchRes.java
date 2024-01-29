package com.ot.client.model.search.multi;

import com.ot.client.model.CommonRes;
import com.ot.client.model.ContainResultMixin;
import com.ot.client.model.search.ResultInterface;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MultiSearchRes extends CommonRes implements ContainResultMixin {

    private List<ResultInterface> results;

}
