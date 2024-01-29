package com.ot.model.ticket;

import com.ot.client.model.search.ResultInterface;
import com.ot.model.member.ReturnableMember;
import com.ot.model.search.ContentsSummary;
import com.ot.repository.common.entity.File;
import com.ot.repository.member.entity.Member;
import com.ot.repository.photo_tikcet.entity.PhotoTicket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoTicketResponse extends PhotoTicket {

    //    @Transient
    private File image;

    //    @Transient
    private ReturnableMember member;

    // @Transient
    private ContentsSummary contentsInformation;

}
