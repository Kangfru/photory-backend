package com.ot.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ot.config.AppConfig;
import com.ot.exception.ServiceException;
import com.ot.model.common.FileResponse;
import com.ot.repository.common.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final FileRepository fileRepository;

    private final AppConfig appConfig;

    private final AmazonS3 amazonS3;

    public FileResponse uploadFiles(List<MultipartFile> multipartFiles) throws IOException {
        FileResponse fileResponse = new FileResponse();
        List<com.ot.repository.common.entity.File> fileVoList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                    .orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
            com.ot.repository.common.entity.File fileVo = com.ot.repository.common.entity.File.multipartOf(multipartFile);
            upload(uploadFile, fileVo);
            fileVoList.add(fileVo);
        }
        fileRepository.saveAll(fileVoList);
        fileResponse.setFileList(fileVoList);
        return fileResponse;
    }

    public String upload(File uploadFile, com.ot.repository.common.entity.File fileVo) {
        String uploadImageUrl = putS3(uploadFile, fileVo.getFileName()); // s3로 업로드
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(appConfig.getAws().getS3().getBucket(), fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(appConfig.getAws().getS3().getBucket(), fileName).toString();
    }

    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.error("File delete fail");
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(appConfig.getUpload().getLocal() + "/" + file.getOriginalFilename());
        if (convertFile.exists()) {
            return Optional.of(convertFile);
        }
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                log.error("", e);
                throw e;
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    public com.ot.repository.common.entity.File getFile(String fileId) {
        return fileRepository.findOneByFileId(fileId).orElseThrow(() -> new ServiceException("404", "Can't find file"));
    }
}
