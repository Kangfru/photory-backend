package com.ot.client.model.search.person;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ot.client.model.ContainResultMixin;
import com.ot.client.model.CommonRes;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonSearchRes extends CommonRes {
    private List<PersonResult> results;

}
