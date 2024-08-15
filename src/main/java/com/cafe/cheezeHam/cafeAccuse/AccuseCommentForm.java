package com.cafe.cheezeHam.cafeAccuse;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AccuseCommentForm {
    @NotEmpty
    private String content;
}
