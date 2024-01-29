package com.ot.client.model.search.person;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.ot.client.model.ContainResultMixin;
import com.ot.client.model.search.ResultInterface;
import com.ot.client.model.search.multi.MultiSearchRes;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonTypeName("person")
public class PersonResult implements ResultInterface {

    private String profilePath;

    private boolean adult;

    private int id;

    private List<KnownFor> knownFor;

    private int gender;

    private String name;

    private BigDecimal popularity;

    private String knownForDepartment;

    private String mediaType = "person";

    private List<Integer> genreIds;

}