package com.ot.model.ticket;

import com.ot.annotation.ValidObjectId;
import com.ot.repository.photo_tikcet.entity.Giphy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@ToString
public class SavePhotoTicketRequest {

//    @NotBlank
//    @Schema(name = "memberSeq", description = "회원 sequence")
//    private String memberSeq;

    @NotBlank
    @Schema(name = "contents_type", description = "타입 (movie or tv)", allowableValues = { "tv", "movie" })
    private String contentsType;

    @NotBlank
    private String contentsId;

    @NotNull
    @Schema(name = "view_date", description = "시청일자 (yyyy-MM-dd)")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "viewDate format must be yyyy-MM-dd")
    private String viewDate;

    @NotBlank
    @Schema(name = "comment", description = "한줊평")
    private String comment;

    @Max(5)
    @Min(0)
    @Schema(name = "rating", description = "별점")
    private int rating;

    // can be null.
    @Schema(name = "file_id", description = "파일 seq (photo ticket)")
    @ValidObjectId
    private String fileId;

    @Schema(name = "detail_comment", description = "상세 평")
    private String detailComment;

    @Schema(name = "exposed", description = "전체 공개 여부 (true : 전체 공개, false : 비공개")
    private boolean exposed;

    @Schema(name = "giphy_list", description = "포토티켓에 들어갈 giphy 정보 list")
    private List<Giphy> giphyList;

}

