package Ict.esgProject.controller;

import Ict.esgProject.service.NaverSignUpService;
import Ict.esgProject.service.SignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/esg/signUp")
public class SnsSignUpController {

    private final NaverSignUpService naverLoginService;
    private final SignUpService signUpService;

    @GetMapping("/naver")
    public ResponseEntity<?> naver(@RequestParam Map<String,String> params){
        log.info("naver - parameter : {}" , params);
        //프론트로부터 access_token 받아와서 Naver 에 UserInfo 요청
        // 이후 받아온 사용자 정보를 통한 db 확인, db 확인후 response return.
        return naverLoginService.getUserInfo(params.get("access_token"));
    }

    @PostMapping("/new/user")
    public ResponseEntity<?> singUp(@RequestBody Map<String,String> signUpInfo){
        log.info("new/user - parameter : {}" , signUpInfo);
        return signUpService.signUp(signUpInfo);
    }
}
