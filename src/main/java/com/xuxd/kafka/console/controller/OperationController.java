package com.xuxd.kafka.console.controller;

import com.xuxd.kafka.console.beans.TopicPartition;
import com.xuxd.kafka.console.beans.annotation.RequiredAuthorize;
import com.xuxd.kafka.console.beans.dto.BrokerThrottleDTO;
import com.xuxd.kafka.console.beans.dto.ProposedAssignmentDTO;
import com.xuxd.kafka.console.beans.dto.ReplicationDTO;
import com.xuxd.kafka.console.beans.dto.SyncDataDTO;
import com.xuxd.kafka.console.service.OperationService;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * kafka-console-ui.
 *
 * @author xuxd
 * @date 2021-10-24 23:13:28
 **/
@RestController
@RequestMapping("/op")
public class OperationController {

    @Autowired
    private OperationService operationService;

    @PostMapping("/sync/consumer/offset")
    @RequiredAuthorize
    public Object syncConsumerOffset(@RequestBody SyncDataDTO dto) {
        dto.getProperties().put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, dto.getAddress());
        return operationService.syncConsumerOffset(dto.getGroupId(), dto.getTopic(), dto.getProperties());
    }

    @PostMapping("/sync/min/offset/alignment")
    @RequiredAuthorize
    public Object minOffsetAlignment(@RequestBody SyncDataDTO dto) {
        dto.getProperties().put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, dto.getAddress());
        return operationService.minOffsetAlignment(dto.getGroupId(), dto.getTopic(), dto.getProperties());
    }

    @GetMapping("/sync/alignment/list")
    public Object getAlignmentList() {
        return operationService.getAlignmentList();
    }

    @DeleteMapping("/sync/alignment")
    @RequiredAuthorize
    public Object deleteAlignment(@RequestParam Long id) {
        return operationService.deleteAlignmentById(id);
    }

    @PostMapping("/replication/preferred")
    public Object electPreferredLeader(@RequestBody ReplicationDTO dto) {
        return operationService.electPreferredLeader(dto.getTopic(), dto.getPartition());
    }

    @PostMapping("/broker/throttle")
    @RequiredAuthorize
    public Object configThrottle(@RequestBody BrokerThrottleDTO dto) {
        return operationService.configThrottle(dto.getBrokerList(), dto.getUnit().toKb(dto.getThrottle()));
    }

    @DeleteMapping("/broker/throttle")
    public Object removeThrottle(@RequestBody BrokerThrottleDTO dto) {
        return operationService.removeThrottle(dto.getBrokerList());
    }

    @GetMapping("/replication/reassignments")
    public Object currentReassignments() {
        return operationService.currentReassignments();
    }

    @DeleteMapping("/replication/reassignments")
    public Object cancelReassignment(@RequestBody TopicPartition partition) {
        return operationService.cancelReassignment(new org.apache.kafka.common.TopicPartition(partition.getTopic(), partition.getPartition()));
    }

    @PostMapping("/replication/reassignments/proposed")
    @RequiredAuthorize
    public Object proposedAssignments(@RequestBody ProposedAssignmentDTO dto) {
        return operationService.proposedAssignments(dto.getTopic(), dto.getBrokers());
    }
}
