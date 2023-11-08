const insertBtn = document.getElementById("insertBtn");

if(insertBtn != null){
// 로그인이 된 경우만 .. ( 예외 처리 )
// 로그인 여부에 따라 insertBtn이 있는가 없는가에 대한 예외처리 

    insertBtn.addEventListener("click", () => {
        // 해당 주소로 요청을 보내주세요. 

        // JS 객체 중 location (pathname 혹은 href 이용)
        // location.herf = "주소"
        // 해당 주소로 요청 ( GET 방식 ) 

        location.href = `/board2/${location.pathname.split("/")[2]}/insert`;
        				//   /board2/1/insert


    });




}









