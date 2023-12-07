package Ict.esgProject.service;

import Ict.esgProject.model.Administrator;
import Ict.esgProject.model.EnterprisesInfo;
import Ict.esgProject.model.EvaluationResult;
import Ict.esgProject.repository.AdministratorMapper;
import Ict.esgProject.repository.EnterprisesInfoMapper;
import Ict.esgProject.repository.EvaluationResultMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AdminService {
    private final EvaluationResultMapper evaluationResultMapper;
    private final AdministratorMapper administratorMapper;
    private final  EnterprisesInfoMapper enterprisesInfoMapper;

    public boolean checkAuthorization(Administrator administrator){
        Administrator checkAdmin = administratorMapper.findByEmail(administrator.getAdminEmail());
        // checkAdmin 이 not null 이면 true 리턴.
        return checkAdmin != null;
    }

    public List<EnterprisesInfo> findEnterprisesInfoByName(String entName){
        return enterprisesInfoMapper.findEnterprisesByName(entName);
    }

    public int updateEvalFeedBack(EvaluationResult evaluationResult){
        return evaluationResultMapper.updateFeedBack(evaluationResult);
    }

    public Map<String,Object> getAllEvalResultOfEnt(){
        List<EvaluationResult> lists = evaluationResultMapper.findAllByAdmin();
        log.info("/admin/get/result/all -  getAllEvalResultOfEnt() : {}",lists);
        Map<String,Object> response = new HashMap<>();
        response.put("response",lists);
        return response;
        }

        public Map<String,Object> getAllEnterprises(){
            List<EnterprisesInfo> lists = enterprisesInfoMapper.findAllByAdmin();
            log.info("/admin/get/enterprises/all - getAllEnterprises() : {}",lists);
            Map<String,Object> response = new HashMap<>();
            response.put("response",lists);
            return response;
        }

        public EvaluationResult getResult(EvaluationResult evaluationResult) {
            return evaluationResultMapper.findEvaluationByIdx(evaluationResult.getEvalResultIdx());
        }
    }
