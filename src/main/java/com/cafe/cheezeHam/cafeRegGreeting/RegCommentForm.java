package com.cafe.cheezeHam.cafeRegGreeting;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegCommentForm {

    @NotEmpty
    private String content;

}
