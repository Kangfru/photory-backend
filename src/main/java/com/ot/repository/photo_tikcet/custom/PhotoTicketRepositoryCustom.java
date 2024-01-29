package com.ot.repository.photo_tikcet.custom;

import com.ot.model.ticket.PhotoTicketResponse;
import com.ot.repository.photo_tikcet.entity.PhotoTicket;

import java.util.List;
import java.util.Optional;

public interface PhotoTicketRepositoryCustom {
    List<PhotoTicketResponse> findPopularPhotoTicketsByCountContentsId();

    Optional<PhotoTicket> findByIdWithImage(String photoTicketId);

    List<PhotoTicket> findByMemberSeq(String memberSeq);

}
