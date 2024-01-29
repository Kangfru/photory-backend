package com.ot.client.model.search.credits;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ot.client.model.common.Cast;
import com.ot.client.model.common.Crew;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditSearchRes {

    private String id;

    private List<Cast> cast;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Crew> crew;

    private List<GroupedCrew> groupedCrew;

}
