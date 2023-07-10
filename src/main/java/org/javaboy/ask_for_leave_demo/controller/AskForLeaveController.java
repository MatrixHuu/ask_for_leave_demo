package org.javaboy.ask_for_leave_demo.controller;

import org.javaboy.ask_for_leave_demo.model.AskForLeaveVO;
import org.javaboy.ask_for_leave_demo.model.RespBean;
import org.javaboy.ask_for_leave_demo.service.AskForLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xyma
 * @version 1.0
 * @data 2023/7/10 22:01
 */
@RestController
public class AskForLeaveController {
    @Autowired
    AskForLeaveService askForLeaveService;

    @PostMapping("/ask_for_leave")
    public RespBean askForLeave(@RequestBody AskForLeaveVO askForLeaveVO) {
        return askForLeaveService.askForLeave(askForLeaveVO);
    }
}
