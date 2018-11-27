package com.example.demo.util;

import java.util.HashMap;

public class ResultMap extends HashMap<String, Object> {

    public static ResultMap success(Object obj){
       ResultMap result = new ResultMap();
       result.put("status", 1);
       result.put("result", obj);
       return result;
    }

    public static ResultMap success(){
        ResultMap result =  new ResultMap();
        result.put("status", 1);
        return result;
    }

    public static ResultMap error(String msg){
        ResultMap result = new ResultMap();
        result.put("status", 0);
        result.put("error", msg);
        return result;

    }

}
