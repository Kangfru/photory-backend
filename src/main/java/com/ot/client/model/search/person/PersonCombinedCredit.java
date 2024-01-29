package com.ot.client.model.search.person;

import com.ot.client.model.common.Cast;
import com.ot.client.model.common.Crew;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PersonCombinedCredit {

//    private String id;

    private List<Cast> cast;

    private List<Crew> crew;



}
