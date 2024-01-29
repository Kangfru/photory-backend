package com.ot.repository.photo_tikcet.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
public class Giphy {

    private String id;

    private BigDecimal centerX;

    private BigDecimal centerY;

}
