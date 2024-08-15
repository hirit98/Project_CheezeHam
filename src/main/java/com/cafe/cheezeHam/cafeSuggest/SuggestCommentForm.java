package com.cafe.cheezeHam.cafeSuggest;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SuggestCommentForm {
    @NotEmpty
    private String content;
}
