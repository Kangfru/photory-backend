package com.ot.repository.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ot.util.FileUtils;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Document("file")
public class File {

    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String fileId;

    private String fileName;


    private String originalFileName;

    private String path;

    private long bytes;

    @Builder.Default
    @CreatedDate
    private LocalDateTime regDate = LocalDateTime.now();

    public static File multipartOf(MultipartFile multipartFile) {
        final String id = FileUtils.generateFileId();
        final String name = FileUtils.generateFileName(id, multipartFile.getOriginalFilename());
        final String path = FileUtils.generateUploadPath();
        return File.builder()
//                .fileId(id)
                .fileName(name)
                .originalFileName(multipartFile.getOriginalFilename())
                .path(path)
                .bytes(multipartFile.getSize())
                .build();
    }

}
