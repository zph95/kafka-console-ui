package com.xuxd.kafka.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xuxd.kafka.console.beans.ResponseData;
import com.xuxd.kafka.console.beans.dos.MinOffsetAlignmentDO;
import com.xuxd.kafka.console.dao.MinOffsetAlignmentMapper;
import com.xuxd.kafka.console.service.OperationService;
import java.util.Map;
import java.util.Properties;
import kafka.console.OperationConsole;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;

/**
 * kafka-console-ui.
 *
 * @author xuxd
 * @date 2021-10-24 23:12:54
 **/
@Service
public class OperationServiceImpl implements OperationService {

    private Gson gson = new Gson();

    @Autowired
    private OperationConsole operationConsole;

    private MinOffsetAlignmentMapper minOffsetAlignmentMapper;

    public OperationServiceImpl(ObjectProvider<MinOffsetAlignmentMapper> minOffsetAlignmentMapper) {
        this.minOffsetAlignmentMapper = minOffsetAlignmentMapper.getIfAvailable();
    }

    @Override public ResponseData syncConsumerOffset(String groupId, String topic, Properties thatProps) {

        Tuple2<Object, String> tuple2 = operationConsole.syncConsumerOffset(groupId, topic, thatProps);

        return (boolean) tuple2._1() ? ResponseData.create().success() : ResponseData.create().failed(tuple2._2());
    }

    @Override public ResponseData minOffsetAlignment(String groupId, String topic, Properties thatProps) {

        Tuple2<Map<TopicPartition, Object>, Map<TopicPartition, Object>> tuple2 = operationConsole.checkAndFetchMinOffset(groupId, topic, thatProps);
        Map<TopicPartition, Object> thisMinOffset = tuple2._1();
        Map<TopicPartition, Object> thatMinOffset = tuple2._2();

        JsonObject thisJson = new JsonObject(), thatJson = new JsonObject();
        thisMinOffset.forEach((k, v) -> {
            thisJson.addProperty(String.valueOf(k.partition()), v.toString());
        });
        thatMinOffset.forEach((k, v) -> {
            thatJson.addProperty(String.valueOf(k.partition()), v.toString());
        });

        MinOffsetAlignmentDO alignmentDO = new MinOffsetAlignmentDO();
        alignmentDO.setGroupId(groupId);
        alignmentDO.setTopic(topic);

        QueryWrapper<MinOffsetAlignmentDO> wrapper = new QueryWrapper<>();
        wrapper.eq("group_id", groupId);
        wrapper.eq("topic", topic);
        if (minOffsetAlignmentMapper.selectCount(wrapper) > 0) {
            minOffsetAlignmentMapper.delete(wrapper);
        }

        alignmentDO.setThisOffset(thisJson.toString());
        alignmentDO.setThatOffset(thatJson.toString());
        minOffsetAlignmentMapper.insert(alignmentDO);
        return ResponseData.create().success();
    }
}
