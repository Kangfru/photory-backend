package com.ot.controller;

import com.ot.config.AppConfig;
import com.ot.exception.ServiceException;
import com.ot.model.CommonResponse;
import com.ot.model.common.FileResponse;
import com.ot.repository.common.entity.File;
import com.ot.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/common")
public class CommonController {

    private final AppConfig appConfig;

    private final FileService fileService;

    @PostMapping(value="/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Multipart File 리스트를 받아와 저장 후 파일 정보를 List로 리턴")
    public FileResponse uploadMultiPartFile(@RequestPart("files") List<MultipartFile> multipartFiles) {
        FileResponse fileResponse = null;
        try {
            fileResponse = fileService.uploadFiles(multipartFiles);
        } catch (Exception e) {
            log.error("", e);
            throw new ServiceException("400", "Failed to file upload", HttpStatus.BAD_REQUEST);
        }
        return fileResponse;
    }

    @GetMapping(value="/files/{fileId}")
    @Operation(description = "file data 가져오기")
    public File getFile(@PathVariable String fileId) {
        return fileService.getFile(fileId);
    }

}
