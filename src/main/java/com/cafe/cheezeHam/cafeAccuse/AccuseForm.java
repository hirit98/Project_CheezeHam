package com.cafe.cheezeHam.cafeAccuse;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccuseForm {
    @NotEmpty
    @Size(max=200)
    private String title;

    @NotEmpty
    private String content;

}
