$(function() {
    var idValid = false;
    var passwordValid = false;
    var usernameValid = false;
    var emailValid = false;
    var phoneValid = false;
    var birthdayValid = false;
    var genderValid = false;
    var checkboxValid = false;

    $('#id').on('input', function() {
        var idValue = $(this).val().trim(); // 입력 필드의 값 (공백 제거)

        if (idValue.length === 0) {
            $('.id_error_message').text('아이디를 입력해주세요.');
            idValid = false;
        } else if (idValue.length < 3 || idValue.length > 25) {
            $('.id_error_message').text('아이디는 3자에서 25자 사이어야 합니다.');
            idValid = false;
        } else {
            $('.id_error_message').text('');
            idValid = true;
        }

        checkFormValidity(); // 유효성 검사 함수 호출
    });

    $('#password').on('input', function() {

        checkFormValidity();

        var passwordValue = $(this).val().trim(); // 입력 필드의 값 (공백 제거)
        if (passwordValue.length === 0) {
            $('.password_error_message').text('비밀번호를 입력해주세요.');
            passwordValid = false;
        }else if (passwordValue.length < 4 || passwordValue.length > 16) {
            $('.password_error_message').text('비밀번호는 4자에서 16자 사이어야 합니다.');
            passwordValid = false;
        } else {
            $('.password_error_message').text('');
            passwordValid = true;
        }
        checkFormValidity();
    });

    $('#email').on('input', function() {
        checkFormValidity();
        var emailValue = $(this).val().trim();
        var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;

        if (emailValue.length === 0) {
            $('.email_error_message').text('이메일을 입력해주세요.');
            emailValid = false;
        } else if (!emailPattern.test(emailValue)) {
            $('.email_error_message').text('유효한 이메일 형식이 아닙니다.');
            emailValid = false;
        } else {
            $('.email_error_message').text('');
            emailValid = true;
        }
        checkFormValidity();
    });

    $('#username').on('input', function() {
        checkFormValidity();

        var usernameValue = $(this).val().trim(); // 입력 필드의 값 (공백 제거)

        if (usernameValue.length === 0) {
            $('.username_error_message').text('사용자명을 입력해주세요.');
            usernameValid = false;
        }else if (usernameValue.length < 3 || usernameValue.length > 25) {
            $('.username_error_message').text('사용자명은 3자에서 25자 사이어야 합니다.');
            usernameValid = false;
        } else {
            $('.username_error_message').text('');
            usernameValid = true;
        }
        checkFormValidity();
    });

    $('#phone').on('input', function() {
        checkFormValidity();

        var phoneValue = $(this).val().trim(); // 입력 필드의 값 (공백 제거)
        var phonePattern = /^\d{3}-\d{3,4}-\d{4}$/;

        if (phoneValue.length === 0) {
            $('.phone_error_message').text('전화번호를 입력해주세요.');
            phoneValid = false;
        } else if (!phonePattern.test(phoneValue)) {
            $('.phone_error_message').text('유효한 전화번호 형식이 아닙니다. (예: 010-0000-0000)');
            phoneValid = false;
        } else {
            $('.phone_error_message').text('');
            phoneValid = true;
        }

        checkFormValidity();
    });

    $('#birthday').on('input', function() {
        checkFormValidity();
        var birthdayValue = $(this).val().trim(); // 입력 필드의 값 (공백 제거)
        var birthdayPattern = /^\d{4}-\d{2}-\d{2}$/;

        if (birthdayValue.length === 0) {
            $('.birthday_error_message').text('생년월일을 입력해주세요.');
            birthdayValid = false;
        } else if (!birthdayPattern.test(birthdayValue)) {
            $('.birthday_error_message').text('유효한 생년월일 형식이 아닙니다. (예: YYYY-mm-dd)');
            birthdayValid = false;
        } else {
            // 추가적으로 날짜의 유효성을 체크할 수도 있지만, 이 예제에서는 형식만 검사합니다.
            $('.birthday_error_message').text('');
            birthdayValid = true;
        }

        checkFormValidity();
    });

    $('input[type="radio"][name="gender"]').on('change', function() {
        var genderValue = $(this).val(); // 선택된 성별 값

        if (genderValue == null) {
            $('.gender_error_message').text('성별을 선택해주세요.');
            genderValid = false;
        } else {
            $('.gender_error_message').text('');
            genderValid = true;
        }

        checkFormValidity(); // 유효성 검사 함수 호출
    });


    $('#btnJoin').on('click', function() {
        // 성별 선택 유효성 검사
        if (!genderValid) {
            $('.gender_error_message').text('성별을 선택해주세요.');
            return false; // 폼 제출을 막기 위해 false를 반환합니다.
        }else{
            $('.gender_error_message').text('');
            return true
        }
    });




    $('#agree_all').click(function() {
        var isChecked = $(this).prop('checked'); // 체크박스의 체크 상태를 가져옵니다.
        $('.check_list_item input[type="checkbox"]').prop('checked', isChecked); // 모든 체크박스의 상태를 전체 동의 체크박스와 동기화합니다.
    });

    // 개별 약관 동의 체크박스를 클릭할 때
    $('.check_list_item input[type="checkbox"]').click(function() {
        var allChecked = true;
        $('.check_list_item input[type="checkbox"]').each(function() {
            if (!$(this).prop('checked')) {
                allChecked = false;
            }
        });
        $('#agree_all').prop('checked', allChecked); // 모든 개별 체크박스가 체크되었을 경우 전체 동의 체크박스도 체크합니다.
    });

    $('#agree_all').on('change', function() {
        var isCheckedAll = $('#agree_all').prop('checked');
        if (!isCheckedAll) {
            $('.check_box_error_message').text('모든 약관에 동의해야 합니다.');
        }else{
            $('.check_box_error_message').text('');
        }
        checkFormValidity(); // 유효성 검사 함수 호출
    });



    // 회원가입 버튼 클릭 시 유효성 검사 예시
    $('#btnJoin').on('click', function() {
        var isCheckedAll = $('#agree_all').prop('checked');
        if (!isCheckedAll) {
            $('.check_box_error_message').text('모든 약관에 동의해야 합니다.');
            return false; // 폼 제출을 막기 위해 false를 반환합니다.
        }else{
            $('.check_box_error_message').text('');
            return true
        }

    });

    function checkFormValidity() {
        if (idValid && passwordValid && usernameValid && emailValid && phoneValid && birthdayValid) {

            $('#btnJoin').prop('disabled', false);
            $('#btnJoin').css({background:"#ff6347b5"})
            $('#btnJoin').mouseover(function() {
                $(this).css('cursor', 'pointer');
            });
        } else {
            $('#btnJoin').prop('disabled', true);
            $('#btnJoin').css({background:"gray"})
            $('#btnJoin').mouseover(function() {
                $(this).css('cursor', 'default');
            });
        }
    }

});