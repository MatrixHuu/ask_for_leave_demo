package org.javaboy.ask_for_leave_demo.service;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.javaboy.ask_for_leave_demo.model.AskForLeaveVO;
import org.javaboy.ask_for_leave_demo.model.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.javaboy.ask_for_leave_demo.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xyma
 * @version 1.0
 * @data 2023/7/10 22:02
 */
@Service
public class AskForLeaveService {

    @Autowired
    RuntimeService runtimeService;

    @Transactional
    public RespBean askForLeave(AskForLeaveVO askForLeaveVO) {
        try {
            Map<String, Object> vars = new HashMap<>();
            vars.put("days", askForLeaveVO.getDays());
            vars.put("approveUser", askForLeaveVO.getApproveUser());
            vars.put("reason", askForLeaveVO.getReason());
            vars.put("startTime", askForLeaveVO.getStartTime());
            vars.put("endTime", askForLeaveVO.getEndTime());
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            vars.put("applicant", user.getUsername());
            ProcessInstance pi = runtimeService.startProcessInstanceByKey("askforleave_demo", vars);
            return RespBean.ok("请假申请已提交");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RespBean.error("请假申请提交失败");
    }
}
