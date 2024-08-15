package com.cafe.cheezeHam.cafeBoast;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BoastCommentForm {
    @NotEmpty
    private String content;

}
