package com.cafe.cheezeHam.cafeEvent;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class EventCommentForm {

    @NotEmpty
    private String content;

}

