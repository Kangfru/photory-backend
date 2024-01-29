package com.ot.service;

import com.ot.code.MemberStatusCode;
import com.ot.exception.ServiceException;
import com.ot.model.CommonResponse;
import com.ot.model.ticket.PhotoTicketResponse;
import com.ot.model.ticket.SavePhotoTicketRequest;
import com.ot.model.ticket.SavePhotoTicketResponse;
import com.ot.repository.challenge.entity.Challenge;
import com.ot.repository.member.MemberRepository;
import com.ot.repository.photo_tikcet.PhotoTicketRepository;
import com.ot.repository.photo_tikcet.entity.PhotoTicket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoTicketService {

    private final PhotoTicketRepository photoTicketRepository;

    private final MemberRepository memberRepository;

    private final SearchService searchService;

    private final ChallengeService challengeService;

    public SavePhotoTicketResponse savePhotoTicket(SavePhotoTicketRequest savePhotoTicketRequest) throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));

        photoTicketRepository.findByMemberSeqAndContentsTypeAndContentsId(member.getMemberSeq(), savePhotoTicketRequest.getContentsType(), savePhotoTicketRequest.getContentsId())
                .ifPresent(p -> { throw  new ServiceException("409", "Conflict : photo ticket is already exists. please do update."); });

        PhotoTicket photoTicket = PhotoTicket.builder()
                .contentsId(savePhotoTicketRequest.getContentsId())
                .contentsType(savePhotoTicketRequest.getContentsType())
                .fileId(StringUtils.hasText(savePhotoTicketRequest.getFileId()) ? savePhotoTicketRequest.getFileId() : null)
                .memberSeq(member.getMemberSeq())
                .comment(savePhotoTicketRequest.getComment())
                .rating(savePhotoTicketRequest.getRating())
                .regDate(LocalDateTime.now())
                .detailComment(savePhotoTicketRequest.getDetailComment())
                .viewDate(LocalDate.parse(savePhotoTicketRequest.getViewDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .detailComment(savePhotoTicketRequest.getDetailComment())
                .exposed(savePhotoTicketRequest.isExposed())
                .giphyList(savePhotoTicketRequest.getGiphyList())
                .build();

        photoTicket = photoTicketRepository.save(photoTicket);

        // 도장깨기 검색해서 해당 컨텐츠 포함된 도장깨기 존재한다면 done 으로 업데이트
        List<Challenge> challengeList = challengeService.saveDoneChallengeContentAfterSavePhotoTicket(photoTicket, true);
        SavePhotoTicketResponse savePhotoTicketResponse = new SavePhotoTicketResponse();
        savePhotoTicketResponse.setContentsId(photoTicket.getContentsId());
        savePhotoTicketResponse.setChallengeList(challengeList);
        savePhotoTicketResponse.setContentsType(photoTicket.getContentsType());
        return savePhotoTicketResponse;

    }

    public PhotoTicket getPhotoTicketVoBySeq(String photoTicketId) {
        return photoTicketRepository.findByIdWithImage(photoTicketId).orElseThrow(() -> new ServiceException("404", "포토티켓을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

    public List<PhotoTicketResponse> getMyPhotoTickets() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        List<PhotoTicket> lists = photoTicketRepository.findByMemberSeq(member.getMemberSeq());
        List<PhotoTicketResponse> responses = new ArrayList<>();
        for (PhotoTicket photoTicket : lists) {
            PhotoTicketResponse response = new PhotoTicketResponse();
            BeanUtils.copyProperties(photoTicket, response);
            response.setContentsInformation(searchService.getMovieOrTvSummary(response.getContentsId(), response.getContentsType()));
            responses.add(response);
        }
        return responses;
    }

    public List<PhotoTicketResponse> getPhotoTicketsPopular() {
        List<PhotoTicketResponse> responses = photoTicketRepository.findPopularPhotoTicketsByCountContentsId();
        for (PhotoTicketResponse response : responses) {
            response.setContentsInformation(searchService.getMovieOrTvSummary(response.getContentsId(), response.getContentsType()));
        }
        return responses;
    }

    public CommonResponse deletePhotoTicketVoBySeq(String photoTicketId) {
        PhotoTicket photoTicket = photoTicketRepository.findById(photoTicketId).orElseThrow(() -> new ServiceException("404", "Can't find photo ticket"));
        // 도장깨기 검색해서 해당 컨텐츠 포함된 도장깨기 존재한다면 미완성으로 업데이트
        challengeService.saveDoneChallengeContentAfterSavePhotoTicket(photoTicket, false);

        photoTicketRepository.deleteById(photoTicketId);
        return new CommonResponse();
    }

    public CommonResponse updatePhotoTicketById(String photoTicketId, SavePhotoTicketRequest savePhotoTicketRequest) {
        PhotoTicket photoTicket = photoTicketRepository.findById(photoTicketId).orElseThrow(() -> new ServiceException("404", "Can't find photo ticket"));
        photoTicket.changeComment(savePhotoTicketRequest.getComment());
        photoTicket.changeDetailComment(savePhotoTicketRequest.getDetailComment());
        photoTicket.changeFileId(savePhotoTicketRequest.getFileId());
        photoTicket.changeRating(savePhotoTicketRequest.getRating());
        photoTicket.changeExposed(savePhotoTicketRequest.isExposed());
        photoTicket.changeGiphyList(savePhotoTicketRequest.getGiphyList());
        photoTicketRepository.save(photoTicket);
        return new CommonResponse();
    }
}
