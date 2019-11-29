package com.cyq.learning.udf.function;

import org.apache.flink.table.functions.ScalarFunction;

public class ToUpperCase extends ScalarFunction {

    public String eval(String s) {

        return s.toUpperCase();
    }
}