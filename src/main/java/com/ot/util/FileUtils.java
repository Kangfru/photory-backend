package com.ot.util;

import com.ot.config.AppConfig;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RequiredArgsConstructor
public class FileUtils {

    private static String BASE_PATH = "upload";

    public static String generateFileName(String fileId, String fileName) {
        return fileId + "-" + fileName;
    }

    public static String generateFileId() {
        return UUID.randomUUID().toString().replace("-" ,"");
    }

    public static String generateUploadPath() {
        return BASE_PATH + "/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

}
