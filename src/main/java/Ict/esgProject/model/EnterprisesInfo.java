package Ict.esgProject.model;

import lombok.Data;

/*
    기업 관리자 INNER JOIN 기업
 */
@Data
public class EnterprisesInfo {
    //Enterprises_Mrg
    private String entMrgEmail;
    private String entMrgPw;
    //개인정보를 저장해서 확인하는 방식 X -> 항상 DB 에서 조회해서 확인할 것! -> 테이블 별로 VO 를 만드는것이 아니라 사용 용도에 따라 만들어야함.
    private String entMrgName;
    private String entMrgMobile;
    private String entMrgSns;
    private int entIdx;
    //Enterprises
    private String entName;
    private String entCat;
    private String entBossName;
    private String entDetailsCat;
    private String entMajorProd;
    private String entMajorClnt;
    private int entCert;
    private String entRegNo;
}
