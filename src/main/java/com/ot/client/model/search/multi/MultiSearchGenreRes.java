package com.ot.client.model.search.multi;

import com.ot.client.model.CommonRes;
import com.ot.client.model.search.ResultInterface;
import com.ot.client.model.search.person.PersonResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MultiSearchGenreRes extends CommonRes {

    private List<ResultInterface> movieResults;

    private List<ResultInterface> tvShowResults;

    private List<PersonResult> people;

}
