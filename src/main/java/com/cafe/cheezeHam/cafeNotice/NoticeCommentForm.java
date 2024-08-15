package com.cafe.cheezeHam.cafeNotice;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class NoticeCommentForm {

    @NotEmpty
    private String content;

}
