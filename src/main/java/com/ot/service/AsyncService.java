package com.ot.service;

import com.ot.client.code.ChallengeCode;
import com.ot.client.model.search.provider.ContentsProviderSearchRes;
import com.ot.client.model.search.provider.ProviderDetail;
import com.ot.exception.ServiceException;
import com.ot.repository.challenge.entity.ChallengeContent;
import com.ot.repository.challenge.entity.ChallengeRecord;
import com.ot.repository.challenge.entity.nested.ChallengeContentProvider;
import com.ot.repository.challenge.repository.ChallengeContentRepository;
import com.ot.repository.challenge.repository.ChallengeRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncService {

    private final SearchService searchService;
    private final ChallengeContentRepository challengeContentRepository;
    private final ChallengeRecordRepository challengeRecordRepository;

    @Async
    public void updateProvider(List<ChallengeContent> contents) {

        for (ChallengeContent content : contents) {
            List<ChallengeContentProvider> providers = new ArrayList<>();
            try {
                val apiRes = searchService.getProviderByContentsId(content.getContentsId(), content.getContentsType());
                if (apiRes.getBuy() != null && !apiRes.getBuy().isEmpty()) {
                    for (ProviderDetail providerDetail : apiRes.getBuy()) {
                        val provider = new ChallengeContentProvider();
                        provider.init(
                                ChallengeCode.ProviderType.buy,
                                providerDetail.getDisplayPriority(),
                                providerDetail.getLogoPath(),
                                providerDetail.getProviderName(),
                                providerDetail.getProviderId()
                        );
                        providers.add(provider);
                    }


                }
                if (apiRes.getRent() != null && !apiRes.getRent().isEmpty()) {
                    for (ProviderDetail providerDetail : apiRes.getRent()) {
                        val provider = new ChallengeContentProvider();
                        provider.init(
                                ChallengeCode.ProviderType.rent,
                                providerDetail.getDisplayPriority(),
                                providerDetail.getLogoPath(),
                                providerDetail.getProviderName(),
                                providerDetail.getProviderId()
                        );
                        providers.add(provider);
                    }


                }
                if (apiRes.getFlatrate() != null && !apiRes.getFlatrate().isEmpty()) {
                    for (ProviderDetail providerDetail : apiRes.getFlatrate()) {
                        val provider = new ChallengeContentProvider();
                        provider.init(
                                ChallengeCode.ProviderType.flatrate,
                                providerDetail.getDisplayPriority(),
                                providerDetail.getLogoPath(),
                                providerDetail.getProviderName(),
                                providerDetail.getProviderId()
                        );
                        providers.add(provider);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            val challengeContent = challengeContentRepository.findById(content.getChallengeContentId()).orElseThrow(() -> new ServiceException("400", "no_data", HttpStatus.BAD_REQUEST));
            challengeContent.updateProviders(providers);
            challengeContentRepository.save(challengeContent);

            List<ChallengeRecord> challengeRecords = new ArrayList<>();
            for (ChallengeContentProvider provider : challengeContent.getProviders()) {
                val id = ChallengeRecord.generateId(challengeContent.getMemberSeq(), provider.getType(), provider.getProviderId());
                val challengeRecord = challengeRecordRepository.findById(id).orElseGet(() -> {
                    val record = new ChallengeRecord();
                    record.init(challengeContent.getMember(), provider);
                    return record;
                });
                challengeRecord.increaseTotalInfo(content);
                challengeRecord.increaseDoneInfo(content);
                challengeRecords.add(challengeRecord);
            }
            challengeRecordRepository.saveAll(challengeRecords);

        }


    }

}
