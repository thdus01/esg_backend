package Ict.esgProject.service;

import Ict.esgProject.model.*;
import Ict.esgProject.repository.EvaluationResultMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.sql.Date;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationResultService {
    private final EvaluationResultMapper evaluationResultMapper;

    @Transactional
    public Map<String,Object> createEvaluationResult(Map<String,Object> params) {
        EvaluationResult evaluationResult = new EvaluationResult();
        evaluationResult.setEntMrgEmail(String.valueOf(params.get("ent_mrg_email"))); // P.K 설정.
        java.util.Date now = new java.util.Date();
        long sqlDate = now.getTime();
        Date evalDate = new Date(sqlDate);
        /*
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        try {
            evalDate = formatter.parse(String.valueOf(new Date()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
         */
        evaluationResult.setEvalDate(evalDate); // 평가 날짜 설정.

        // 전달 받은 email 과 새로 생성한 date 를 통해 평가 결과 데이터 생성. 생성된다면  1 리턴.
        int result = evaluationResultMapper.createEvaluation(evaluationResult);
        Map<String,Object> response = new HashMap<>();
        if (result > 0) {
            int evalIdx = evaluationResult.getEvalResultIdx();
            List<Integer> publicList = processRequestParams(params, "public");
            List<Integer> socialList = processRequestParams(params, "social");
            List<Integer> environmentList = processRequestParams(params, "environment");
            List<Integer> governanceList = processRequestParams(params, "governance");

            for (int i = 0; i < publicList.size(); i++) {
                EvalCat pub = new EvalCat("p", evalIdx, i+1, publicList.get(i));
                if(!evaluationResultMapper.createPublic(pub)) response.put("status",400);
            }
            for (int i = 0; i < socialList.size(); i++) {
                EvalCat social = new EvalCat("s", evalIdx, i+1, socialList.get(i));
                if(!evaluationResultMapper.createSocial(social)) response.put("status",400);
            }
            for (int i = 0; i < environmentList.size(); i++) {
                EvalCat env = new EvalCat("e", evalIdx, i+1, environmentList.get(i));
                if(!evaluationResultMapper.createEnvironment(env)) response.put("status",400);
            }
            for (int i = 0; i < governanceList.size(); i++) {
                EvalCat gov = new EvalCat("g", evalIdx, i+1, governanceList.get(i));
                if(!evaluationResultMapper.createGovernance(gov)) response.put("status",400);
            }
            response.put("eval_result_idx",evalIdx);
            response.put("status",200);
            return response;
        }
        return response;
    }

    public Map<String,Object> getEvaluationResultList(Map<String,String> params){

        String entMrgEmail = params.get("ent_mrg_email");
        List<EvaluationResult> evaluationResultList = evaluationResultMapper.findAllByEmail(entMrgEmail);
        log.info("result/all - getEvaluationResultList() : {}",evaluationResultList);
        Map<String,Object> response = new HashMap<>();
        if(evaluationResultList.isEmpty()){ //데이터 조회 실패
            response.put("status",400);
            response.put("message","email 에 해당하는 정보가 없습니다. email 을 다시 확인해주세요!");
            return response;
        }else { //조회 성공
            response.put("status",200);
            response.put("response",evaluationResultList);
            return response;
        }


    }

    //상세 진단 결과 조회
    public Map<String,String> getEvaluationResult(Map<String,Object> params) throws ParseException {

        /*
        Date 설정
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date evalDate = formatter.parse(String.valueOf(params.get("eval_date")));
        */

        int evalIdx = Integer.parseInt(String.valueOf(params.get("eval_result_idx")));
        EvaluationResult result = evaluationResultMapper.findEvaluationByIdx(evalIdx);
        log.info("/result - getEvaluationResult() - EvalCat  : {}",result);
         // db 조회 성공시, parameter 로 받은 evalIdx 사용 가능.

        //응답에 사용할 객체 생성.
        Map<String,String> response = new HashMap<>();
        if( result != null ){
            //각각의 평가 항목을 받아옴.
            List<EvalCat> publicList = evaluationResultMapper.findListPublicByIdx(evalIdx);
            List<EvalCat> environmentList = evaluationResultMapper.findListEnvironmentByIdx(evalIdx);
            List<EvalCat> socialList = evaluationResultMapper.findListSocialByIdx(evalIdx);
            List<EvalCat> governanceList = evaluationResultMapper.findListGovernanceByIdx(evalIdx);

            // process 메소드에서 해당 결과 들을 점수화.

            /*
            각 p,e,s,g 항목에 대해 선택한 답변 번호 및 각 항목 개별 점수 리턴하는 방식
            Map<String,Object> response = new HashMap<>();
            response.put("public",process(publicList));
            response.put("environment",process(environmentList));
            response.put("social",process(socialList));
            response.put("governance",process(governanceList));
            */
            /*
             *  p,e,s,g 에서 각각의 개별 점수 및 총점 리턴.
             */
            float publicScore = (float) process(publicList).get("score");
            float environmentScore = (float) process(environmentList).get("score");
            float socialScore = (float) process(socialList).get("score");
            float governanceScore = (float) process(governanceList).get("score");

            float total = (environmentScore + socialScore + governanceScore )/ 3;

            response.put("status","200");
            response.put("public",String.valueOf(publicScore));
            response.put("environmentList",String.valueOf(environmentScore));
            response.put("social",String.valueOf(socialScore));
            response.put("governance",String.valueOf(governanceScore));
            response.put("total",String.valueOf(total));
            response.put("entName", String.valueOf(result.getEntName()));
            log.info("/result - getEvaluationResult() - response : {}",response);
            return response;
        }
        else {
            response.put("status","400");
            response.put("message","db 생성 실패");
            return response;
        }
    }
    //응답 데이터 만들기.
    public Map<String, Object> process(List<EvalCat> list){
        int sum = 0;
        int count = 0;
        int[] ansNo = new int[list.size()];  //생성 시 각 인덱스의 값은 (자동)0 으로 초기화.
        for(int i = 0; i<list.size();  i++){
                if(list.get(i).getAnsNo() != 0){ //답변이 0 일 경우, 해당 배점을 총점에서 제외하기 위한 if.
                    ansNo[i] = list.get(i).getAnsNo();
                    sum += list.get(i).getAnsNo();
                    count++;
                }
            }

        float score = sum * 100 / (float) (count * 5);
        Map<String, Object> result = new HashMap<>();
        result.put("score",score);
        result.put("ansNo",ansNo);

        return result;
    }

    //요청 데이터 가공
    public List<Integer> processRequestParams(Map<String,Object> params,String key){
        String[] requestPrams = String.valueOf(params.get(key))
                                .replace("[","")
                                .replace("]","")
                                .split(", ");

        List<Integer> catList = new ArrayList<>();

        for(int i = 0; i < requestPrams.length; i++){
            catList.add(Integer.parseInt(requestPrams[i]));
        }
        return catList;
    }

}
