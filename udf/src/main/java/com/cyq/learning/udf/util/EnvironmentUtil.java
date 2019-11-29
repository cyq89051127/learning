package com.cyq.learning.udf.util;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;

public class EnvironmentUtil {

    public static StreamTableEnvironment getStreamTableEnvironment ()  {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);
        return StreamTableEnvironment.create(env);
    }
}
