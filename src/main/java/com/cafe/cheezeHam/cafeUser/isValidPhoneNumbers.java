package com.cafe.cheezeHam.cafeUser;

import java.util.regex.Pattern;


public class isValidPhoneNumbers {

    private static final String Phone_PATTERN = "^[0-9]{10,11}$";
    public static boolean isValidPhoneNumber(String phone) {
        return Pattern.compile(Phone_PATTERN).matcher(phone).matches();
    }
}
