package com.milan_projects.roma.things_maker;


import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by Roma on 19.12.2017.
 */

public class Decoder {
    public static String toEng(String s) {
        String res="";
        for(int i=0;i<s.length();i++) {
            res=res+(int)(s.charAt(i))+" ";
        }
        return res;
    }
    public static String toUTF(String s) {
        String res="";
        String[] mas = s.split(" ");
        for(int i=0;i<mas.length;i++) {
            res=res+(char)Integer.parseInt(mas[i]);
        }
        return res;
    }

}

