package com.ot.repository.photo_tikcet;

import com.ot.repository.photo_tikcet.custom.PhotoTicketRepositoryCustom;
import com.ot.repository.photo_tikcet.entity.PhotoTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PhotoTicketRepository extends MongoRepository<PhotoTicket, String>, PhotoTicketRepositoryCustom {
//    List<PhotoTicket> findByMemberSeq(String memberSeq);

    Optional<PhotoTicket> findByMemberSeqAndContentsTypeAndContentsId(String memberSeq, String contentsType, String contentsId);

    Page<PhotoTicket> findAllPhotoTicketByContentsTypeAndContentsId(String contentsType, String contentsId, Pageable pageable);

}
