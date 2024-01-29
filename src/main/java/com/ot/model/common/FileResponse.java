package com.ot.model.common;

import com.ot.model.CommonResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileResponse extends CommonResponse {

    private List<com.ot.repository.common.entity.File> fileList;

}
