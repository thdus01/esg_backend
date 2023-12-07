package Ict.esgProject.service;

import Ict.esgProject.model.Administrator;
import Ict.esgProject.model.EnterprisesInfo;
import Ict.esgProject.repository.AdministratorMapper;
import Ict.esgProject.repository.EnterprisesInfoMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class LoginService {
    private final EnterprisesInfoMapper enterprisesInfoMapper;
    private final AdministratorMapper administratorMapper;

    public Map<String,Object> loginProcess(Map<String,String> loginInfo){
        String userEmail = loginInfo.get("email");
        String userPw = loginInfo.get("pw");

        //db 조회
        EnterprisesInfo enterprisesInfo = enterprisesInfoMapper.findByEmail(userEmail);
        Administrator administratorInfo = administratorMapper.findByEmail(userEmail);

        Map<String,Object> response = new HashMap<>();

        if (enterprisesInfo == null && administratorInfo == null) {
            response.put("status",401);
            response.put("message","가입 되지 않은 회원입니다!");
            return response;
        } else if(enterprisesInfo != null && administratorInfo == null){
            if(enterprisesInfo.getEntMrgPw().equals(userPw)){
                response.put("status",200);
                response.put("message","로그인 성공!");
                response.put("enterprisesInfo",enterprisesInfo);
                response.put("role","user");
                return response;
            } else {
                response.put("status",401);
                response.put("message","비밀번호가 틀렸습니다!");
                return response;
            }
        } else if(enterprisesInfo == null && administratorInfo != null){
            if(administratorInfo.getAdminPw().equals(userPw)){
                response.put("status",200);
                response.put("message","로그인 성공!");
                response.put("adminInfo",administratorInfo);
                response.put("role","admin");
                return response;
            }else {
                response.put("status",401);
                response.put("message","비밀번호가 틀렸습니다!");
                return response;
            }
        }
        return null;
    }

    public EnterprisesInfo checkInfo(Map<String,String> userInfo){
        EnterprisesInfo check = new EnterprisesInfo();
        check.setEntMrgEmail(userInfo.get("ent_mrg_email"));
        check.setEntMrgMobile(userInfo.get("ent_mrg_mobile"));
        EnterprisesInfo user = enterprisesInfoMapper.findEnterprisesInfo(check);
        if( user != null ) return user;
        else return null;
    }

    public boolean changePassword(EnterprisesInfo changeInfo){
        int res = enterprisesInfoMapper.changePw(changeInfo);

        if(res > 0 ) return true;
        else return false;
    }
}
