package com.cyq.learning.udf;

import com.cyq.learning.udf.function.SplitFun;
import com.cyq.learning.udf.function.WeightedAvg;
import com.cyq.learning.udf.util.EnvironmentUtil;
import com.cyq.learning.udf.util.SourceUtil;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.HashMap;
import java.util.Map;

import static com.cyq.learning.udf.util.BaseConstant.BROKER_SERVERS;
import static com.cyq.learning.udf.util.BaseConstant.TOPIC;

public class AggFunctionTest {
    public static void main(String[] args) throws Exception {
        StreamTableEnvironment streamTableEnvironment = EnvironmentUtil.getStreamTableEnvironment();

        TableSchema tableSchema = new TableSchema(new String[]{"product_id", "number", "price", "time"},
                new TypeInformation[]{Types.STRING, Types.LONG, Types.INT, Types.SQL_TIMESTAMP});

        Map<String, String> kakfaProps = new HashMap<>();
        kakfaProps.put("bootstrap.servers", BROKER_SERVERS);

        FlinkKafkaConsumerBase flinkKafkaConsumerBase = SourceUtil.createKafkaSourceStream(TOPIC, kakfaProps, tableSchema);

        DataStream<Row> stream = streamTableEnvironment.execEnv().addSource(flinkKafkaConsumerBase);

        streamTableEnvironment.registerFunction("wAvg", new WeightedAvg());

        Table streamTable = streamTableEnvironment.fromDataStream(stream, "product_id,number,price,rowtime.rowtime");

        streamTableEnvironment.registerTable("product", streamTable);
        Table result = streamTableEnvironment.sqlQuery("SELECT product_id, wAvg(number,price) as avgPrice FROM product group by product_id");


        final TableSchema tableSchemaResult = new TableSchema(new String[]{"product_id", "avg"},
                new TypeInformation[]{Types.STRING, Types.LONG});
        final TypeInformation<Row> typeInfoResult = tableSchemaResult.toRowType();

        // Here a toRestarctStream is need, Because the Talbe result is not an append table,
        DataStream ds = streamTableEnvironment.toRetractStream(result, typeInfoResult);
        ds.print();
        streamTableEnvironment.execEnv().execute("Flink Agg Function Test");


    }
}
