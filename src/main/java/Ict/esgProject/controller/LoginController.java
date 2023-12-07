package Ict.esgProject.controller;

import Ict.esgProject.model.EnterprisesInfo;
import Ict.esgProject.service.LoginService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Slf4j
@AllArgsConstructor
@RestController
//-> 프론트 단에셔 통신요청 실패
@RequestMapping("/esg")
public class LoginController {
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> loginInfo){
        log.info("login - parameter : {}" , loginInfo);
        Map<String,Object> response = loginService.loginProcess(loginInfo);
        if((Integer)response.get("status") == 200){
            return  ResponseEntity.status(HttpStatus.OK).body(response);
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    /*
     *  비밓번호 찾기 흐름
     *  checkInfo()
     *  1. 이메일, 휴대전화로 db 에 사용자 존재 여부 체크.
     *  2. 적절한 응답 리턴 -> 존재 한다면 해당 사용자 정보(현재 비밀번호 포함), 리턴 / 없다면 에러 리턴
     *  비밀번호 변경은 changePassword() 에서
     *  3. 새로운 요청을 통해 비밀번호 변경처리.
     */
    @PostMapping("/check")
    public ResponseEntity<?> checkInfo(@RequestBody Map<String,String> userInfo){
        log.info("check - parameter : {}" , userInfo);
        EnterprisesInfo user = loginService.checkInfo(userInfo);
        if(user != null) return ResponseEntity.status(HttpStatus.OK).body(user);
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("해당 정보의 사용자가 없습니다. 다시 확인해주세요!");
    }

    @PostMapping("/change/pw")
    public ResponseEntity<String> changePassword(@RequestBody Map<String,String> changeInfo){
        log.info("change/pw - parameter : {}" ,changeInfo);

        EnterprisesInfo user = new EnterprisesInfo();
        user.setEntMrgEmail(changeInfo.get("ent_mrg_email"));
        user.setEntMrgPw(changeInfo.get("ent_mrg_pw"));
        if(loginService.changePassword(user)){
            return ResponseEntity.status(HttpStatus.OK).body("비밀번호 변경 성공");
        }
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 변경 실패 - 해당 이메일이 정확하지 않음!");
    }
}
