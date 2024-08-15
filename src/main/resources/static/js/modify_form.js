$(document).ready(function() {
    var passwordValid = false;
    var pwdCheckValid = false;
    var phoneValid = true;

    $('#phone').on('input', function() {
        var phoneNumber = $(this).val();
        var phonePattern = /^\d{3}-\d{3,4}-\d{4}$/;

        if (phoneNumber.length === 0) {
            $('.phone_error_message').text('전화번호를 입력해주세요.');
            phoneValid = false;
        } else if (!phonePattern.test(phoneNumber)) {
            $('.phone_error_message').text('유효한 전화번호 형식이 아닙니다. (예: 010-0000-0000)');
            phoneValid = false;
        } else {
            $('.phone_error_message').text('');
            phoneValid = true;
        }
    });

    $('#password').on('input', function() {
        var passwordValue = $(this).val().trim();

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
    });

    $('#password, #pwd_check').on('input', function() {
        var passwordValue = $('#password').val().trim();
        var pwdCheckValue = $('#pwd_check').val().trim();

        if (passwordValue !== pwdCheckValue) {
            $('.check_error_message').text('비밀번호가 일치하지 않습니다.');
            pwdCheckValid = false;
        } else {
            $('.check_error_message').text('');
            pwdCheckValid = true;
        }
    });

    var modifyForm = $('.myInfo_modify_form');
    modifyForm.on('input', 'input', function() {
        checkFormValidity();
    })

    function checkFormValidity() {
        var modify_btn = $('#btnModify');

        if (passwordValid && pwdCheckValid && phoneValid) {
            modify_btn.prop('disabled', false);
            modify_btn.css({background:"#ff6347b5"})
            modify_btn.mouseover(function() {
                $(this).css('cursor', 'pointer');
            });
        } else {
            modify_btn.prop('disabled', true);
            modify_btn.css({background:"gray"})
            modify_btn.mouseover(function() {
                $(this).css('cursor', 'default');
            });
        }
    }
})