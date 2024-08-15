package com.cafe.cheezeHam.cafeSuggest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SuggestForm {
    @NotEmpty
    @Size(max=200)
    private String title;

    @NotEmpty
    private String content;
}
