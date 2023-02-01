package com.zkthinke.modules.apsat.ship.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldUnitUtil {

    public static String removeUnit(String field){
        Pattern pattern = Pattern.compile("[\\-\\+]?[\\d.]+");
        if (field!=null){
            Matcher matcher = pattern.matcher(field);
            if (matcher.find()){
                return matcher.group();
            }
        }
        return field;
    }
}
