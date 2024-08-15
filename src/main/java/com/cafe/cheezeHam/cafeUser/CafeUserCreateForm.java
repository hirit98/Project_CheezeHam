package com.cafe.cheezeHam.cafeUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CafeUserCreateForm {

    private String id;

    private String username;

    private String password;

    private String email;

    //@Pattern(regexp="^[0-9]{10,11}$", message="유효한 전화번호 형식이어야 합니다.") // 예시 정규식, 필요에 따라 수정
    private String phone;
    //@Pattern(regexp="^[0-9]{8}$", message="생년월일은 8자리 숫자여야 합니다.") // 예시 정규식, 필요에 따라 수정
    private String birthday;

    private String gender;

}