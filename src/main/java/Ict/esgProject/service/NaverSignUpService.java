package Ict.esgProject.service;

import Ict.esgProject.model.Administrator;
import Ict.esgProject.model.EnterprisesInfo;
import Ict.esgProject.repository.AdministratorMapper;
import Ict.esgProject.repository.EnterprisesInfoMapper;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Service
@AllArgsConstructor
public class NaverSignUpService {
    private final EnterprisesInfoMapper enterprisesInfoMapper;
    private final AdministratorMapper administratorMapper;

    //Naver 에서 UserInfo 받아온 후, 최초가입 인지 아닌지 확인 후 적절한 응답 리턴.
    public ResponseEntity<?> getUserInfo(String access_token) {
        //URL 객체 생성 시 절대 경로로 생성하기 위한 지정.
        String requestURL = "https://openapi.naver.com/v1/nid/me";

        try {
            URL url = new URL(requestURL);
            /* openConnection() 메소드는 실제 네트워크 연결 설정 X. URLConnection 클래스의 인스턴스 반환.
            실제 네트워크 연결은 connect() 메소드에 의해 (명시적) or
            헤더 필드를 읽거나, 입력 스트림/출력 스트림을 가져올때 이루어짐(암시적)
            */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            //URLConnection 이 서버에 데이터를 보내는데 사용할 수 있는지 여부를 지정. Default -> false
            connection.setDoOutput(true);
            //key = value 쌍으로 지정된 요청 속성을 지정해줌.
            connection.setRequestProperty("Authorization", "Bearer " + access_token);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            br.close();

            JSONObject userInfo = new JSONObject(result).getJSONObject("response");

            boolean flag = flag(userInfo);

            Map<String,String> response = new HashMap<>();

            if(flag){ //flag 가 true 인 경우 가입 절차 진행. 기업 담당자(사용자) 인 경우만 생각. -> 추후 관리자 회원가입 고려해야함.
                //true 인 경우 flag() 에서 db 에 데이터 존재 유무 확인.
                EnterprisesInfo enterprisesMrg = new EnterprisesInfo();
                response.put("ent_mrg_email",userInfo.getString("email"));
                response.put("ent_mrg_name",userInfo.getString("name"));
                response.put("ent_mrg_mobile",userInfo.getString("mobile"));
                response.put("ent_mrg_sns","NAVER");
                response.put("message","Get UserInfo Success!");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Get UserInfo Fail!");
/*
            //Response Body 에 전달할 데이터 가공.
            Map<String,String> response = new HashMap<>();

            if(flag){ //flag 가 true 일 경우 -> 기관 담당자 계정 리턴.
                EnterprisesMrg enterprisesMrg = enterPrisesMrgMapper.findByEmail(userInfo.getString("email"));
                response.put("Email",enterprisesMrg.getEntMrgEmail());
                response.put("Name",enterprisesMrg.getEntMrgName());
                response.put("Mobile",enterprisesMrg.getEntMrgMobile());
                response.put("Role", "EnterprisesMrg");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                Administrator administrator = administratorMapper.findByEmail("email");
                response.put("Email",administrator.getAdminEmail());
                response.put("Name",administrator.getAdminName());
                response.put("Mobile",administrator.getAdminMobile());
                response.put("Role", "Administrator");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

 */
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //db에 사용자가 존재하는지 체크.
    public boolean flag(JSONObject userInfo){
        String userEmail = userInfo.getString("email");

//        enterprisesInfo.setEntMrgEmail(userInfo.getString("email"));
//        enterprisesMrg.setEntMrgPw(userInfo.getString("name"));
//        enterprisesMrg.setEntMrgMobile(userInfo.getString("mobile"));
//        enterprisesMrg.setEntMrgSns("NAVER");

        EnterprisesInfo checkEnterprisesInfo = enterprisesInfoMapper.findByEmail(userEmail);
        Administrator checkAdministrator = administratorMapper.findByEmail(userEmail);

        /* if(checkEnterprisesMrg == null && checkAdministrator != null){ //관리자 테이블에 데이터가 있을 때
            return false;
        } else if( checkEnterprisesMrg != null && checkAdministrator == null){ // 기업 담당자(사용자) 테이블에 데이터가 있을 때
            return true;
        }
        else { //db 에 저장된 데이터가 없을 경우
            enterPrisesMrgMapper.createEnterprisesMrg(enterprisesMrg);
            return true;
        } */


        //db에 기업 담당자 or 관리자 데이터가 있는지 확인
//        System.out.println("checkAdministrator : " + checkAdministrator);
//        System.out.println("checkEnterprisesInfo : " + checkEnterprisesInfo);
//        if(checkAdministrator == null && checkEnterprisesInfo == null){
//            return true; //어느 한쪽에라도 null 이 아닌 경우(저장된 data 가 있는 경우) 가입 페이지로 넘어가지 않게 설정.
//        } else {
//            return false; //어느 한쪽에도 data 가 없는 경우
//        }
        return checkAdministrator == null && checkEnterprisesInfo == null;
    }


}
