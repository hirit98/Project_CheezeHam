package com.cafe.cheezeHam.cafeCatBuy;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CatBuyCommentForm {
    @NotEmpty
    private String content;
}
