package com.cyq.learning.udf;

import com.cyq.learning.udf.function.SplitFun;
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

import static com.cyq.learning.udf.util.BaseConstant.BROKER_SERVERS;
import static com.cyq.learning.udf.util.BaseConstant.TOPIC;

public class TableFunctionTest {
    public static void main(String[] args) throws Exception {
        StreamTableEnvironment streamTableEnvironment = EnvironmentUtil.getStreamTableEnvironment();

        TableSchema tableSchema = new TableSchema(new String[]{"product_id", "addr", "price", "time"},
                new TypeInformation[]{Types.STRING, Types.STRING, Types.LONG, Types.SQL_TIMESTAMP});

        Map<String, String> kakfaProps = new HashMap<>();
        kakfaProps.put("bootstrap.servers", BROKER_SERVERS);

        FlinkKafkaConsumerBase flinkKafkaConsumerBase = SourceUtil.createKafkaSourceStream(TOPIC, kakfaProps, tableSchema);

        DataStream<Row> stream = streamTableEnvironment.execEnv().addSource(flinkKafkaConsumerBase);

        streamTableEnvironment.registerFunction("split", new SplitFun("@"));

        Table streamTable = streamTableEnvironment.fromDataStream(stream, "product_id,addr,price,rowtime.rowtime");

        streamTableEnvironment.registerTable("product", streamTable);

        //        Table result = sourceTable.joinLateral("split(addr) as (addr, length)").select("product_id,addr,length");
//        Table result = sourceTable.leftOuterJoinLateral("split(addr) as (addr, length)").select("product_id,addr,length");
//        Table result = tableEnv.sqlQuery("SELECT product_id, addr, length FROM product, LATERAL TABLE(split(addr)) as T(addr, length)");
        Table result = streamTableEnvironment.sqlQuery("SELECT product_id, address, length FROM product LEFT JOIN LATERAL TABLE(split(addr)) as TT(address, length) ON TRUE");



        final TableSchema tableSchemaResult = new TableSchema(new String[]{"product_id", "addr", "length"},
                new TypeInformation[]{Types.STRING, Types.STRING, Types.INT});
        final TypeInformation<Row> typeInfoResult = tableSchemaResult.toRowType();
        DataStream ds = streamTableEnvironment.toAppendStream(result, typeInfoResult);
        ds.print();
        streamTableEnvironment.execEnv().execute("Flink Table Function Test");


    }
}
