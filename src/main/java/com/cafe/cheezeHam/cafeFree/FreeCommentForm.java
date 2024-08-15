package com.cafe.cheezeHam.cafeFree;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FreeCommentForm {

    @NotEmpty
    private String content;
}
