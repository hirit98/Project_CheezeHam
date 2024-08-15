package com.cafe.cheezeHam.cafeHamBuy;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class HamBuyCommentForm {
    @NotEmpty
    private String content;
}
