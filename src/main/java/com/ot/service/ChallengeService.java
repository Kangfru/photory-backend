package com.ot.service;

import com.ot.client.code.ChallengeCode;
import com.ot.code.MemberStatusCode;
import com.ot.exception.ServiceException;
import com.ot.model.challenge.*;
import com.ot.model.common.Pagination;
import com.ot.repository.challenge.entity.Challenge;
import com.ot.repository.challenge.entity.ChallengeContent;
import com.ot.repository.challenge.entity.ChallengeRecord;
import com.ot.repository.challenge.entity.nested.ChallengeContentProvider;
import com.ot.repository.challenge.repository.ChallengeContentRepository;
import com.ot.repository.challenge.repository.ChallengeRecordRepository;
import com.ot.repository.challenge.repository.ChallengeRepository;
import com.ot.repository.member.MemberRepository;
import com.ot.repository.photo_tikcet.PhotoTicketRepository;
import com.ot.repository.photo_tikcet.entity.PhotoTicket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChallengeService {

    private final MemberRepository memberRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeContentRepository challengeContentRepository;
    private final ChallengeRecordRepository challengeRecordRepository;
    private final ModelMapper mapper;
    private final PhotoTicketRepository photoTicketRepository;
    private final AsyncService asyncService;

    public ChallengeDetailResponse getChallenge(Map<String, String> header, String id, Boolean isIncreaseReadCount) {
        val challenge = challengeRepository.findById(id).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        if (isIncreaseReadCount) {
            challenge.increaseReadCount();
            challengeRepository.save(challenge);
        }
        return mapper.map(challenge, ChallengeDetailResponse.class);
    }

    public String saveChallenge(Map<String, String> header, SaveChallengeRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        Challenge challenge = new Challenge();
        challenge.init(member, request.getTitle(), request.getDescription(), request.getExposed());
        challengeRepository.save(challenge);

        val list = new ArrayList<ChallengeContent>();
        var seq = 0;
        for (SaveChallengeRequest.Content content : request.getContents()) {
            ChallengeContent challengeContent = new ChallengeContent();
            challengeContent.init(challenge, member, content.getContentsId(), content.getContentsType(), content.getTitle(), content.getOriginalTitle(), content.getPosterPath(), content.getBackdropPath(), content.getRunTime(), seq++);
            photoTicketRepository.findByMemberSeqAndContentsTypeAndContentsId(member.getMemberSeq(), content.getContentsType(), content.getContentsId()).ifPresent(photoTicket -> {
                challengeContent.updateDone(photoTicket);
                challenge.updateLastContentDoneDate(challengeContent.getDoneDate());
            });

            list.add(challengeContent);
        }
        challengeContentRepository.saveAll(list);
        challenge.updateContents(list);
        challengeRepository.save(challenge);
        // 비동기 처리
        asyncService.updateProvider(list);
        return challenge.getChallengeId();
    }

    public String modifyChallenge(Map<String, String> header, String id, ModifyChallengeRequest request) {
        val user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        if (!challenge.getMember().getMemberSeq().equals(member.getMemberSeq())) {
            throw new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST);
        }
        challenge.updateInfo(request.getTitle(), request.getDescription(), request.getExposed());
        challengeRepository.save(challenge);
        return challenge.getChallengeId();
    }

    public String addChallengeContent(Map<String, String> header, String id, SaveChallengeRequest.Content content) {
        val user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        if (!challenge.getMember().getMemberSeq().equals(member.getMemberSeq())) {
            throw new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST);
        }
        ChallengeContent challengeContent = new ChallengeContent();
        challengeContent.init(challenge, member, content.getContentsId(), content.getContentsType(), content.getTitle(), content.getOriginalTitle(), content.getPosterPath(), content.getBackdropPath(), content.getRunTime(), challenge.getContents().size());

        photoTicketRepository.findByMemberSeqAndContentsTypeAndContentsId(member.getMemberSeq(), content.getContentsType(), content.getContentsId()).ifPresent(photoTicket -> {
            challengeContent.updateDone(photoTicket);
            challenge.updateLastContentDoneDate(challengeContent.getDoneDate());
        });

        challengeContentRepository.save(challengeContent);
        challenge.addContent(challengeContent);
        challenge.updateContents(challenge.getContents());
        challengeRepository.save(challenge);
        // 비동기 처리
        asyncService.updateProvider(Collections.singletonList(challengeContent));
        return challenge.getChallengeId();
    }

    public String removeChallengeContent(Map<String, String> header, String id, String contentId) {
        val user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        if (!challenge.getMember().getMemberSeq().equals(member.getMemberSeq())) {
            throw new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST);
        }

        val challengeContent = challengeContentRepository.findById(contentId).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        List<ChallengeRecord> challengeRecords = new ArrayList<>();
        for (ChallengeContentProvider provider : challengeContent.getProviders()) {
            val challengeRecordId = ChallengeRecord.generateId(challengeContent.getMemberSeq(), provider.getType(), provider.getProviderId());
            val challengeRecord = challengeRecordRepository.findById(challengeRecordId).orElseGet(() -> {
                val record = new ChallengeRecord();
                record.init(challengeContent.getMember(), provider);
                return record;
            });
            challengeRecord.decreaseTotalInfo(challengeContent);
            challengeRecord.decreaseDoneInfo(challengeContent);
            challengeRecords.add(challengeRecord);
        }
        challengeRecordRepository.saveAll(challengeRecords);

        challengeContentRepository.deleteById(contentId);
        challenge.removeContent(contentId);
        challengeRepository.save(challenge);

        val contents = challengeContentRepository.findByChallengeIdOrderBySeqAsc(id);
        int index = 0;
        for (ChallengeContent content : contents) {
            content.updateSeq(index++);
            challengeContentRepository.save(content);
        }

        return challenge.getChallengeId();
    }

    public PageChallengeResponse getChallengeList(Map<String, String> header, Integer pageNumber, Integer size, String status) {
        val user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        if (size < 0) {
            size = Integer.MAX_VALUE;
        }
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Challenge> challenges = null;
        if (status == null || !status.isEmpty()) {
            challenges = challengeRepository
                    .findByMemberSeqAndStatusOrderByLastContentDoneDateDescUpdateDateDescCreateDateDesc(member.getMemberSeq(), ChallengeCode.Status.valueOf(status), pageable);
        } else {
            challenges = challengeRepository
                    .findByMemberSeqOrderByLastContentDoneDateDescUpdateDateDescCreateDateDesc(member.getMemberSeq(), pageable);
        }
        List<ChallengeResponse> list = challenges.getContent().stream()
                // 정렬 안하고 content seq 기준 오름차순
//                .peek(row -> {
//                    row.getContents().sort((o1, o2) -> {
//                        if (o1.getDoneDate() == null) {
//                            if (o1.getSeq() > o2.getSeq()) {
//                                return 1;
//                            } else {
//                                return -1;
//                            }
//                        } else if (o2.getDoneDate() == null) {
//                            return -1;
//                        }
//                        if (o1.getDoneDate().isAfter(o2.getDoneDate())) {
//                            return 1;
//                        } else if (o1.getDoneDate().isEqual(o2.getDoneDate())) {
//                            if (o1.getSeq() > o2.getSeq()) {
//                                return 1;
//                            } else if (o1.getSeq().equals(o2.getSeq())) {
//                                return 0;
//                            } else {
//                                return -1;
//                            }
//                        } else {
//                            return -1;
//                        }
//                    });
//                })
                .map(row -> mapper.map(row, ChallengeResponse.class)).collect(Collectors.toList());
        Pagination pagination = new Pagination();
        pagination.setSize(challenges.getSize());
        pagination.setTotalElements(challenges.getTotalElements());
        pagination.setPageNumber(challenges.getNumber());
        pagination.setTotalPages(challenges.getTotalPages());
        val responseData = new PageChallengeResponse();
        responseData.setChallenges(list);
        responseData.setPagination(pagination);
        return responseData;
    }

    public ChallengeDashboardResponse getDashboard(Map<String, String> header) {
        val user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        val contents = challengeContentRepository.findByMemberSeq(member.getMemberSeq());
        ChallengeDashboardResponse responseData = new ChallengeDashboardResponse();
        contents.forEach(content -> {
            responseData.setTotalCount(responseData.getTotalCount() + 1);
            if (content.getContentsType().equals("tv")) {
                val tvData = responseData.getTv();
                tvData.setTotalCount(tvData.getTotalCount() + 1);
                tvData.setTotalRunTime(tvData.getTotalRunTime() + content.getRunTime().intValue());
                if (content.getIsDone()) {
                    tvData.setDoneCount(tvData.getDoneCount() + 1);
                    tvData.setDoneRunTime(tvData.getDoneRunTime() + content.getRunTime().intValue());
                }
            } else if (content.getContentsType().equals("movie")) {
                val movieData = responseData.getMovie();
                movieData.setTotalCount(movieData.getTotalCount() + 1);
                movieData.setTotalRunTime(movieData.getTotalRunTime() + content.getRunTime().intValue());
                if (content.getIsDone()) {
                    movieData.setDoneCount(movieData.getDoneCount() + 1);
                    movieData.setDoneRunTime(movieData.getDoneRunTime() + content.getRunTime().intValue());
                }
            }
        });
        // 일단 "구독" 업체만
        val records = challengeRecordRepository.findByMemberSeqAndType(member.getMemberSeq(), ChallengeCode.ProviderType.flatrate);
        records.forEach(record -> {
            val data = new ChallengeDashboardResponse.DataDashboard();
            data.setTotalCount(record.getTotalCount().intValue());
            data.setTotalRunTime(record.getTotalRunTime().intValue());
            data.setDoneCount(record.getDoneCount().intValue());
            data.setDoneRunTime(record.getDoneRunTime().intValue());
            responseData.getRecord().put(record.getProviderName(), data);
        });
        return responseData;
    }

    public ChallengeDetailResponse saveSeqChallengeContent(Map<String, String> header, String id, String contentId, Integer seq) {
        val user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        if (!challenge.getMember().getMemberSeq().equals(member.getMemberSeq())) {
            throw new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST);
        }
        val challengeContent = challengeContentRepository.findById(contentId).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        if (!challengeContent.getChallenge().getChallengeId().equals(challenge.getChallengeId())) {
            throw new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST);
        }
        if (challengeContent.getSeq().equals(seq)) {
            throw new ServiceException("400", "이미 같은 순번", HttpStatus.BAD_REQUEST);
        }

        val contents = challenge.getContents();
        contents.remove(challengeContent.getSeq().intValue());
        contents.add(seq, challengeContent);
        int index = 0;
        for (ChallengeContent content : contents) {
            content.updateSeq(index++);
            challengeContentRepository.save(content);
        }
        return getChallenge(header, id, false);
    }

    public List<String> removeChallengeList(Map<String, String> header, RemoveChallengeRequest request) {
        val challengeContentList = challengeContentRepository.findByChallengeIdIn(request.getIds());
        List<ChallengeRecord> challengeRecords = new ArrayList<>();
        challengeContentList.forEach(challengeContent -> {
            for (ChallengeContentProvider provider : challengeContent.getProviders()) {
                val id = ChallengeRecord.generateId(challengeContent.getMemberSeq(), provider.getType(), provider.getProviderId());
                val challengeRecord = challengeRecordRepository.findById(id).orElseGet(() -> {
                    val record = new ChallengeRecord();
                    record.init(challengeContent.getMember(), provider);
                    return record;
                });
                challengeRecord.decreaseTotalInfo(challengeContent);
                challengeRecord.decreaseDoneInfo(challengeContent);
                challengeRecords.add(challengeRecord);
            }
        });
        challengeRecordRepository.saveAll(challengeRecords);
        challengeContentRepository.deleteAllByChallengeIdIn(request.getIds());
        challengeRepository.deleteAllById(request.getIds());
        return request.getIds();
    }

    /**
     * 포토티켓 생성 또는 제거 후 기존 도장깨기판 깨기완료 업데이트 처리
     *
     * @param photoTicket 해당 photoTicket
     * @param isMake      생성시 true, 제거시 false
     * @return 생성 또는 제거로 인해 변경된 도장깨기 리스트 반환("XXX 외 3개의 다른 도장깨기판에 추가로 도장이 찍혔습니다." 노출시 필요) -> 이중 status == Done 된게 있는지 체크해서 도장깨기판 완성시 노출팝업용 api 호출 여부 체크
     */
    public List<Challenge> saveDoneChallengeContentAfterSavePhotoTicket(PhotoTicket photoTicket, boolean isMake) {
        List<Challenge> challengeList = challengeRepository.findByMemberSeq(photoTicket.getMemberSeq());
        List<Challenge> updatedChallengeList = new ArrayList<>();
        challengeList.forEach(challenge -> {
            boolean checkFlag = false;
            for (ChallengeContent content : challenge.getContents()) {
                if ((content.getIsDone() &&  isMake) || !content.getContentsId().equals(photoTicket.getContentsId())) {
                    continue;
                }
                if (isMake) {
                    content.updateDone(photoTicket);
                } else {
                    content.updateDone(null);
                }
                challengeContentRepository.save(content);

                List<ChallengeRecord> challengeRecords = new ArrayList<>();
                for (ChallengeContentProvider provider : content.getProviders()) {
                    val challengeRecordId = ChallengeRecord.generateId(content.getMemberSeq(), provider.getType(), provider.getProviderId());
                    val challengeRecord = challengeRecordRepository.findById(challengeRecordId).orElseGet(() -> {
                        val record = new ChallengeRecord();
                        record.init(content.getMember(), provider);
                        return record;
                    });
                    if (isMake) {
                        challengeRecord.increaseDoneInfo(content);
                    } else {
                        challengeRecord.decreaseDoneInfo(content);
                    }
                    challengeRecords.add(challengeRecord);
                }
                challengeRecordRepository.saveAll(challengeRecords);

                checkFlag = true;
                break;
            }
            if (checkFlag) {
                challenge.updateLastContentDoneDate(LocalDateTime.now());
                challenge.isCheckDoneAll();
                challengeRepository.save(challenge);
                updatedChallengeList.add(challenge);
            }
        });
        return updatedChallengeList;
    }

    public ChallengeDoneResponse getChallengeDone(Map<String, String> header) {
        val user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        List<Challenge> challengeList = challengeRepository.findByMemberSeqAndStatusAndIsNotiDone(member.getMemberSeq(), ChallengeCode.Status.DONE, false);
        val notiChallengeList = challengeList.stream().map(challenge -> mapper.map(challenge, ChallengeDoneResponse.Challenge.class)).collect(Collectors.toList());
        challengeList.forEach(Challenge::updateIsNotiDone);
        challengeRepository.saveAll(challengeList);
        val responseData = new ChallengeDoneResponse();
        responseData.setChallengeList(notiChallengeList);
        val doneChallengeList = challengeRepository.findByMemberSeqAndStatusAndIsNotiDone(member.getMemberSeq(), ChallengeCode.Status.DONE, true);
        responseData.setTotalDoneCount(doneChallengeList.size());
        return responseData;
    }

    public PageChallengeResponse getRecommendChallengeList(Map<String, String> header, Integer pageNumber,
                                                           Integer size, String contentsId, String contentsType) {
        val user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        List<ChallengeContent> challengeContentList = challengeContentRepository.findByContentsIdAndContentsTypeAndMemberSeqNot(contentsId, contentsType, member.getMemberSeq());
        val challengeIds = challengeContentList.stream().map(content -> content.getChallenge().getChallengeId()).distinct().collect(Collectors.toList());
        if (size < 0) {
            size = Integer.MAX_VALUE;
        }
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Challenge> challenges = challengeRepository
                .findByChallengeIdInAndExposedOrderByCreateDateDesc(challengeIds, true, pageable);
        List<ChallengeResponse> list = challenges.stream().map(row -> mapper.map(row, ChallengeResponse.class)).collect(Collectors.toList());
        Pagination pagination = new Pagination();
        pagination.setSize(challenges.getSize());
        pagination.setTotalElements(challenges.getTotalElements());
        pagination.setPageNumber(challenges.getNumber());
        pagination.setTotalPages(challenges.getTotalPages());
        val responseData = new PageChallengeResponse();
        responseData.setChallenges(list);
        responseData.setPagination(pagination);
        return responseData;
    }

    public Boolean checkExistChallenge(String contentsId, String contentsType) {
        val user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        val member = memberRepository.findByEmailAndStatus(user.getUsername(), MemberStatusCode.NORMAL.getCode()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
        return challengeContentRepository.existsByContentsIdAndContentsTypeAndMemberSeq(contentsId, contentsType, member.getMemberSeq());
    }
}
