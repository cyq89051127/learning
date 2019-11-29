package com.cyq.learning.udf.util;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.formats.csv.CsvRowDeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.types.Row;

import java.util.Map;
import java.util.Properties;

public class SourceUtil {

    public static FlinkKafkaConsumerBase<Row> createKafkaSourceStream(String topic,
                                                                      Map<String,String> kafkaProps,
                                                                      TableSchema tableSchema) {

         TypeInformation<Row> typeInfo = tableSchema.toRowType();
         CsvRowDeserializationSchema.Builder deserSchemaBuilder = new CsvRowDeserializationSchema.Builder(typeInfo).setFieldDelimiter(',');

        final Properties properties = new Properties();
        kafkaProps.forEach((key,value) -> properties.setProperty(key,value));

        FlinkKafkaConsumer010<Row> myConsumer = new FlinkKafkaConsumer010<>(
                topic,
                deserSchemaBuilder.build(),
                properties);

        myConsumer.setStartFromLatest();
        return myConsumer;
    }
}
