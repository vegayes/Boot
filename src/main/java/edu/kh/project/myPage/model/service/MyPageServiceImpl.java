package edu.kh.project.myPage.model.service;

import java.io.File;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.common.utility.Util;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dao.MyPageMapper;

@Service
@PropertySource("classpath:/config.properties")
public class MyPageServiceImpl implements MyPageService{
	
	@Value("${my.member.webpath}")
	private String webPath;
	
	@Value("${my.member.location}")
	private String filePath;
	
	
	@Autowired
	private MyPageMapper mapper;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	
	// 회원 정보 수정 서비스
	@Transactional
	@Override
	public int updateInfo(Member updateMember) {
		return mapper.updateInfo(updateMember);
	}

	// 프로필 이미지 수정 서비스
	@Override
	public int updateProfile(MultipartFile profileImage,  Member loginMember) throws Exception {
		
		// 프로필 이미지 변경 실패 대비
		String temp = loginMember.getProfileImage(); // 기존에 가지고 있던 이전 이미지 저장
		
		
		String rename = null; // 변경 이름 저장 변수
		
		if(profileImage.getSize() > 0) { // 업로드된 이미지가 있을 경우
			
			// 1) 파일 이름 변경
			rename = Util.fileRename(profileImage.getOriginalFilename());
			
			// 2) 바뀐 이름 loginMember에 세팅
			loginMember.setProfileImage(webPath + rename);
			
			
			
		} else { // 업로드된 이미지가 없는 경우 (x버튼) 
			
			loginMember.setProfileImage(null);
			
		}
		
		
		// 프로필 이미지 수정 DAO 메서드 호출
		int result = mapper.updateProfileImage(loginMember);
		
		
		if(result > 0) { // DB에 이미지 경로 업데이트 성공했다면
			
			// 업로드된 새 이미지가 있을 경우
			if(rename != null) {
				
				// 메모리에 임시 저장되어있는 파일을 서버에 진짜로 저장하는 것
				profileImage.transferTo(new File(filePath + rename));
			}
			
			
		} else { // 실패
			
			// 이전 이미지로 프로필 다시 세팅
			loginMember.setProfileImage(temp);
			
			
		}
		
		
		return result;
		
		
		
		
	}

	// 비밀번호 변경 서비스
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int changePw(String currentPw, String newPw, int memberNo) {
		
		// 1. 현재 비밀번호, DB에 저장된 비밀번호 비교
		// 1) 회원번호가 일치하는 MEMBER 테이블 행의 MEMBER_PW 조회
		String encPw = mapper.selectEncPw(memberNo);
		
		// 2) bcrypt.matches(평문, 암호문) -> 같으면 true -> 이 때 비번 수정
		if(bcrypt.matches(currentPw, encPw)) {
		
			Member member = new Member();
			member.setMemberNo(memberNo);
			member.setMemberPw(bcrypt.encode(newPw));
			
			// 2. 비밀번호 변경(UPDATE DAO 호출) -> 결과 반환
			return mapper.changePw( member );
		}
		
		// 3) 비밀번호가 일치하지 않으면 0 반환
		return 0;
	}


	// 회원 탈퇴 서비스
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int secession(String memberPw, int memberNo) {
		
		// 1. 회원 번호가 일치하는 회원의 비밀번호 조회
		String encPw = mapper.selectEncPw(memberNo);
		
		// 2.비밀번호가 일치하면 
		if(bcrypt.matches(memberPw, encPw)) {
			// MEMBER_DEL_FL -> 'Y'로 바꾸고 1 반환
			return mapper.secession(memberNo);
		}
		
		//  3. 비밀번호가 일치하지 않으면 -> 0 반환
		return 0;
	}
	

	@Override
	public String pwCheck(Member loginMember) {

//		return mapper.pwCheck(loginMember);
		return null;
	}

	@Override
	public int memberSecession(Member loginMember) {
//		return mapper.memberSecession(loginMember);
		return 0;
	}
	
	@Transactional
	@Override
	public int updatePw(Member loginMember) {
//		return mapper.updatePw(loginMember);
		return 0;
	}

	
/*
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int updatePw(Member loginMember) {
		
		// 1. 현재 비밀번호 , DB에 저장된 비밀번호 비교
		// 1) 회원번호가 일치하는 MEMBER 테이블 행의 MEMBER_PW 조회
		String encPw = mapper.selectEnPw(memberNo);
		
		// 2) bcrypt.matches(평문, 암호문) ->  같으면 true -> 이 때 비번 수정
//		if(bcrypt.matches(currentPw, encPw)) {
			
			Member member = new Member();
			member.setMemberNo(memberNo);
			member.setMemberPw(bcrypt.encode(newPw));
			
			
			// 2. 비밀번호 변경 (UPDATE DAO 호출) -> 결과 반환
			return mapper.updatePw(member);
		}
		
		// 3. 비밀번호 일치하지 않은 경우 ->  0 반환 
		return 0;
	}
*/


}
