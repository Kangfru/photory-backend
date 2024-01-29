package com.ot.client.model.search.person;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PersonDetail extends PersonResult {

    private String birthDay;

    private String knownForDepartment;

    private String deathDay;

    private List<String> alsoKnownAs;

    private String biography;

    private String placeOfBirth;

    private String imdbId;

    private String homepage;

    private PersonCombinedCredit knownForCastAndCrew;

}
