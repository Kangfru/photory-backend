package com.ot.model.ticket;

import com.ot.model.CommonResponse;
import com.ot.repository.challenge.entity.Challenge;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SavePhotoTicketResponse extends CommonResponse {

    public SavePhotoTicketResponse() {
        super();
    }

    // contents seq
    private String contentsId;

    // tv or movie
    private String contentsType;

    private List<Challenge> challengeList;
    
}
