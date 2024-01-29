package com.ot.repository.member.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Document("memberAuthorizations")
public class MemberAuthorization {


    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String authorizationSeq;

    private String authorizationKey;

    private String email;

    private String status;

    private LocalDateTime regDate;

    private LocalDateTime expireDate;

    // join or find
    private String type;

}
