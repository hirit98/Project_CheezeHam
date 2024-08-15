$(document).ready(function() {
    function updateUI(tab) {
        if (tab === 'myInfo') {
            $(".aside_admin_profil").css({ zIndex: "-1" });
            $(".aside_user_profil").css({ zIndex: "2" });
            $(".aside_tab_btn_cafe_info").css({ fontWeight: "100", color: "#333" });
            $(".aside_tab_btn_my_info").css({ fontWeight: "600", color: "black" });
        } else {
            $(".aside_admin_profil").css({ zIndex: "2" });
            $(".aside_user_profil").css({ zIndex: "-1" });
            $(".aside_tab_btn_cafe_info").css({ fontWeight: "600", color: "black" });
            $(".aside_tab_btn_my_info").css({ fontWeight: "100", color: "#333" });
        }
    }

    // 로그인 상태 체크
    $.ajax({
        type: "GET",
        url: '/user/myInfo',
        xhrFields: { withCredentials: true },
        success: function(response) {
            console.log('User is logged in'); // 디버깅용 로그
            $.ajax({
                type: "GET",
                url: "/total/userInfo",
                success: function(info) {
                    if (info) {
                        $('.aside_icon_text_div_post').text(info.postCnt);
                        $('.aside_icon_text_div_comment').text(info.commentCnt);
                        $('.aside_banner_img').first().attr('src', info.images);
                        $('.user_regdate_text').first().text(info.regdate);
                    }
                },
                error: function (xhr, status, error) {
                    console.log(xhr.responseText);
                }
            })
            const activeTab = localStorage.getItem('activeTab') || 'cafeInfo';
            updateUI(activeTab);
        },
        error: function(xhr) {
            if (xhr.status === 403) {
                console.log('User is not logged in'); // 디버깅용 로그
                // 로그인 상태가 아닌 경우
                $(".aside_admin_profil").css({ zIndex: "2" });
                $(".aside_user_profil").css({ zIndex: "-1" });
                $(".aside_tab_btn_cafe_info").css({ fontWeight: "600", color: "black" });
                $(".aside_tab_btn_my_info").css({ fontWeight: "100", color: "#333" });

                // 상태 저장
                localStorage.setItem('activeTab', 'cafeInfo');
            }
        }
    });

    // 카페 활동 버튼 클릭 이벤트
    $('.aside_tab_btn_cafe_info').off('click').on('click', function() {
        console.log('Cafe Info button clicked'); // 디버깅용 로그
        $(".aside_admin_profil").css({ zIndex: "2" });
        $(".aside_user_profil").css({ zIndex: "-1" });
        $(".aside_tab_btn_cafe_info").css({ fontWeight: "600", color: "black" });
        $(".aside_tab_btn_my_info").css({ fontWeight: "100", color: "#333" });

        // 상태 저장
        localStorage.setItem('activeTab', 'cafeInfo');
    });

    // 나의 활동 버튼 클릭 이벤트
    $('.aside_tab_btn_my_info').off('click').on('click', function() {
        console.log('My Info button clicked'); // 디버깅용 로그
        $.ajax({
            type: "GET",
            url: '/user/myInfo',
            xhrFields: { withCredentials: true },
            success: function(response) {
                console.log('Login check successful'); // 디버깅용 로그
                $(".aside_admin_profil").css({ zIndex: "-1" });
                $(".aside_user_profil").css({ zIndex: "2" });
                $(".aside_tab_btn_cafe_info").css({ fontWeight: "100", color: "#333" });
                $(".aside_tab_btn_my_info").css({ fontWeight: "600", color: "black" });

                // 상태 저장
                localStorage.setItem('activeTab', 'myInfo');
            },
            error: function(xhr) {
                if (xhr.status === 403) {
                    alert('로그인 후 이용 가능합니다');
                    window.location.href = '/user/login'; // 로그인 페이지로 이동
                }
            }
        });
    });
    // 로그인 상태 체크 및 페이지 이동


    $.ajax({
        type: "GET",
        url: "/total/count",
        success: function(result) {
            if (result !== "" && result.length > 0) {
                $('.aside_total_board_count').text(result);
            } else {
                $('.aside_total_board_count').text(0);
            }
        },
        error: function(xhr, status, error) {
            console.log(xhr.responseText);
        }
    })
    $.ajax({
        type: "GET",
        url: "/total/userCnt",
        success: function(result) {
            var parsing = parseInt(result.replace(/,/g, ""));
            if (result !== "" && result.length > 0) {
                $('#visitor').val(result);
                if (parsing > 0 && parsing < 10) {
                    $(".aside_icon_img_rank").addClass("Tier1");
                    $("#admin_rank").val("씨앗1단계");
                } else if (parsing >= 10 && parsing < 100) {
                    $(".aside_icon_img_rank").addClass("Tier2");
                    $("#admin_rank").val("새싹1단계");
                } else if (parsing >= 100 && parsing < 500) {
                    $(".aside_icon_img_rank").addClass("Tier3");
                    $("#admin_rank").val("잎새1단계");
                } else if (parsing >= 500 && parsing < 700) {
                    $(".aside_icon_img_rank").addClass("Tier4");
                    $("#admin_rank").val("가지1단계");
                } else if (parsing >= 700 && parsing < 1000) {
                    $(".aside_icon_img_rank").addClass("Tier5");
                    $("#admin_rank").val("열매1단계");
                } else {
                    $(".aside_icon_img_rank").addClass("Tier6");
                    $("#admin_rank").val("나무1단계");
                }
            } else {
                $('#visitor').val(0);
            }
        },
        error: function(xhr, status, error) {
            console.log(xhr.responseText);
        }
    })


});
function openNewWindow(url, width, height) {
    var screenWidth = window.innerWidth;
    var screenHeight = window.innerHeight;

    var left = (screenWidth - width) / 2 + window.screenX;
    var top = (screenHeight - height) / 2 + window.screenY;

    window.open(url, '_blank', `width=${width},height=${height},top=${top},left=${left}`);
}