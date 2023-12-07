package Ict.esgProject.controller;

import Ict.esgProject.model.Administrator;
import Ict.esgProject.model.EnterprisesInfo;
import Ict.esgProject.model.EvalCat;
import Ict.esgProject.model.EvaluationResult;
import Ict.esgProject.service.AdminService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/esg")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/admin/get/result/all")
    public ResponseEntity<?> getAllResult(Administrator administrator){
        log.info("/admin/get/result/all - params : {}",administrator);
        Map<String,Object> response = new HashMap<>();
        if(adminService.checkAuthorization(administrator)){
            response = adminService.getAllEvalResultOfEnt();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else {
            response.put("status",401);
            response.put("message","admin 정보 에러!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/admin/get/result")
    public ResponseEntity<?> getResult(EvaluationResult evaluationResult){
        log.info(evaluationResult.toString());
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getResult(evaluationResult));
    }

    @GetMapping("/admin/get/enterprises/all")
    public ResponseEntity<?> getEnterprises(Administrator administrator){
        Map<String,Object> response = new HashMap<>();
        if(adminService.checkAuthorization(administrator)){
            response = adminService.getAllEnterprises();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("status",401);
            response.put("message","admin 정보 에러!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/admin/find/enterprises")
    public ResponseEntity<?> getEnterprisesByName(EnterprisesInfo enterprisesInfo){
        log.info("ent : {}",enterprisesInfo);
        JSONObject res = new JSONObject(enterprisesInfo.getEntName());
        String entName = res.getString("ent_name");
        return ResponseEntity.status(HttpStatus.OK).body(adminService.findEnterprisesInfoByName(entName));
    }

    @GetMapping("/admin/update/feedback")
    public ResponseEntity<?> updateFeedBack(EvaluationResult evaluationResult){
        log.info("evalResult : {}",evaluationResult);
        return ResponseEntity.status(HttpStatus.OK).body(adminService.updateEvalFeedBack(evaluationResult));
    }
}
