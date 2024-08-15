package com.cafe.cheezeHam.cafeBoast;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoastForm {
    @NotEmpty
    @Size(max=200)
    private String title;

    @NotEmpty
    private String content;

    private String type;
}
