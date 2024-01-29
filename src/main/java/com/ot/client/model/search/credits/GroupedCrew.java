package com.ot.client.model.search.credits;

import com.ot.client.model.common.Crew;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GroupedCrew {

    private String department;

    private List<Crew> list;

}
