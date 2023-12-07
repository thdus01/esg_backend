package Ict.esgProject.model;


import lombok.AllArgsConstructor;
import lombok.Data;

//P,E,S,G 를 묶는 추상클래스.
@Data
@AllArgsConstructor
public class EvalCat {

    private String catType; // db에 없는 칼럼. 해당 타입에 따라 테이블이 결정.
    private int evalResultIdx;
    private int qusNo;
    private int ansNo;

}
