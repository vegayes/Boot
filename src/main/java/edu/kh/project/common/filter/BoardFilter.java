package edu.kh.project.common.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class BoardFilter implements Filter{

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
         throws IOException, ServletException {
      
      HttpServletRequest req = (HttpServletRequest) request;
      
      //   /board/1 -> "/" 기준으로 나누면 {"", "board", "1"}로 나뉨(공백도 포함)
      //   /board/2/insert -> "/" 기준으로 나누면 {"", "board", "2", "insert"}
      // boardNo 를얻어야 되는데 boardCode는 2번 인덱스에 위치(boardLike 제외)
      
      String[] arr = req.getRequestURI().split("/");
      
      
      try {
         
         String boardCode = arr[2];
         
         List<Map<String, Object>> boardTypeList
         = (List<Map<String, Object>>)(req.getServletContext().getAttribute("boardTypeList"));
         // boardTypeList 라는 applicationscope 에서 가져온 BOARD_CODE 가 boardCode와 같으면
         for(Map <String,Object> boardType : boardTypeList) {
            
            if((boardType.get("BOARD_CODE") + "").equals(boardCode)) {
               req.setAttribute("boardName", boardType.get("BOARD_NAME"));
            }
         }

         
      }catch(Exception e){}
         
      chain.doFilter(request, response);
   }
   

}