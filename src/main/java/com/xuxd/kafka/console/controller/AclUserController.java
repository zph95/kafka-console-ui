package com.xuxd.kafka.console.controller;

import com.xuxd.kafka.console.beans.AclUser;
import com.xuxd.kafka.console.beans.annotation.RequiredAuthorize;
import com.xuxd.kafka.console.service.AclService;
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
 * @date 2021-08-28 21:13:05
 **/
@RestController
@RequestMapping("/user")
public class AclUserController {

    @Autowired
    private AclService aclService;

    @GetMapping
    public Object getUserList() {
        return aclService.getUserList();
    }

    @PostMapping
    @RequiredAuthorize
    public Object addOrUpdateUser(@RequestBody AclUser user) {
        return aclService.addOrUpdateUser(user.getUsername(), user.getPassword());
    }

    @DeleteMapping
    @RequiredAuthorize
    public Object deleteUser(@RequestBody AclUser user) {
        return aclService.deleteUser(user.getUsername());
    }


    @DeleteMapping("/auth")
    @RequiredAuthorize
    public Object deleteUserAndAuth(@RequestBody AclUser user) {
        return aclService.deleteUserAndAuth(user.getUsername());
    }

    @GetMapping("/detail")
    public Object getUserDetail(@RequestParam String username) {
        return aclService.getUserDetail(username);
    }
}
