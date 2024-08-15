package com.cafe.cheezeHam.cafeUser;

import java.util.regex.Pattern;

public class isValidBirthdays {

    private static final String Birthday_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static boolean isValidBirthday(String birthday) {
        return Pattern.compile(Birthday_PATTERN).matcher(birthday).matches();
    }
}
