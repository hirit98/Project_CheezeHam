package com.cafe.cheezeHam.cafeUser;

import java.util.regex.Pattern;

public class EmailValid {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    // 패턴과 매칭되는지 확인하는 정적 메서드
    public static boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }
}
