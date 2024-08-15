$(document).ready(function() {
    // CSRF 토큰 가져오기
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        url: "/user/check",
        type: "POST",
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function(username){
            if (username != null && username != "") {
                // 사용자가 로그인한 경우
                $(".login_join_box_inner").append(
                    '<li class="nav-item">' +
                    '<a class="login_btn">' + username + '</a>' +
                    '</li>' +
                    '<li class="nav-item_bar">' +
                    '<div class="nav_bar"></div>' +
                    '</li>' +
                    '<li class="nav-item">' +
                    '<a class="login_btn" href="/user/logout">로그아웃</a>' +
                    '</li>'
                );
            } else {
                // 사용자가 로그인하지 않은 경우
                $(".login_join_box_inner").append(
                    '<li class="nav-item">' +
                    '<a class="login_btn" href="/user/login">로그인</a>' +
                    '</li>' +
                    '<li class="nav-item_bar">' +
                    '<div class="nav_bar"></div>' +
                    '</li>' +
                    '<li class="nav-item">' +
                    '<a class="join_btn" href="/user/signup">회원가입</a>' +
                    '</li>'
                );
            }
        },
        error: function(xhr, status, error) {
            console.log(xhr.responseText);
        }
    })
})