package com.cafe.cheezeHam.cafeUser;

import lombok.Getter;

@Getter
public enum CafeUserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    CafeUserRole(String value) {
        this.value = value;
    }

    private String value;
}
