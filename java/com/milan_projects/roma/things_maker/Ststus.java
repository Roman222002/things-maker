package com.milan_projects.roma.things_maker;

/**
 * Created by Roma on 27.12.2017.
 */

public class Ststus {
    public static String toStat(int k){
        String res="";
        if(k==0){
            res= "Виконується...";
        }
        if(k==1){
            res="Виканано";
        }
        if(k==2){
            res="Провалено";
        }
        if(k==4){
            res="В пошуці виконавця";
        }
        return res;
    }
}
