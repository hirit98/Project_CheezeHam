package com.cafe.cheezeHam.cafeQna;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class QnaCommentForm {
    @NotEmpty
    private String content;
}
