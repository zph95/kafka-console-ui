package com.xuxd.kafka.console.service;

import com.xuxd.kafka.console.beans.ResponseData;
import java.util.Properties;

/**
 * kafka-console-ui.
 *
 * @author xuxd
 * @date 2021-10-24 23:12:43
 **/
public interface OperationService {

    ResponseData syncConsumerOffset(String groupId, String topic, Properties thatProps);

    ResponseData minOffsetAlignment(String groupId, String topic, Properties thatProps);
}
