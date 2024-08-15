package com.cafe.cheezeHam.cafeCatBuy;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CatBuyForm {

    @NotEmpty
    @Size(max=200)
    private String title;

    @NotEmpty
    private String content;

    private String type;
}

