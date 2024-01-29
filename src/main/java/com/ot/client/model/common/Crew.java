package com.ot.client.model.common;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Crew {

    private boolean adult;

    private int gender;

    private int id;

    private String knownForDepartment;

    private String name;

    private String originalName;

    private String originalTitle;

    private BigDecimal popularity;

    private String profilePath;

    private String creditId;

    private String department;

    private String job;

    private String mediaType;

    private String posterPath;

    private String backdropPath;

}
