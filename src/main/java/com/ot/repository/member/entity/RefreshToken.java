package com.ot.repository.member.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Document("refreshTokens")
public class RefreshToken {

    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String _id;

    private String refreshToken;

    private String email;

}
