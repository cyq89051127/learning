package com.cyq.learning.udf;

import com.cyq.learning.udf.function.ToUpperCase;
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

import static com.cyq.learning.udf.util.BaseConstant.*;

public class ScalarFunctionTest {
    public static void main(String[] args) throws Exception {
        StreamTableEnvironment streamTableEnvironment = EnvironmentUtil.getStreamTableEnvironment();

        TableSchema tableSchema = new TableSchema(new String[]{"product_id","addr","price", "time"},
                new TypeInformation[]{Types.STRING, Types.STRING, Types.LONG, Types.SQL_TIMESTAMP});

        Map<String,String> kakfaProps = new HashMap<>();
        kakfaProps.put("bootstrap.servers",BROKER_SERVERS);

        FlinkKafkaConsumerBase flinkKafkaConsumerBase = SourceUtil.createKafkaSourceStream(TOPIC,kakfaProps,tableSchema);

        DataStream<Row> stream = streamTableEnvironment.execEnv().addSource(flinkKafkaConsumerBase);

        streamTableEnvironment.registerFunction("upper",new ToUpperCase());

        Table streamTable = streamTableEnvironment.fromDataStream(stream,"product_id,addr,price,rowtime.rowtime");

        streamTableEnvironment.registerTable("product",streamTable);

        streamTable.printSchema();
        Table result = streamTableEnvironment.sqlQuery("select product_id,addr,upper(addr) as upperaddr,price from product");

        final TableSchema tableSchemaResult = new TableSchema(new String[]{"product_id","addr","upperaddr","price"},
                new TypeInformation[]{Types.STRING, Types.STRING, Types.STRING, Types.LONG});
        final TypeInformation<Row> typeInfoResult = tableSchemaResult.toRowType();
        DataStream ds = streamTableEnvironment.toAppendStream(result, typeInfoResult);
        ds.print();
        streamTableEnvironment.execEnv().execute("Flink Scalar Function Test");



    }
}
