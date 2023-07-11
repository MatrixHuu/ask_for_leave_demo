package org.javaboy.ask_for_leave_demo.service;

import org.apache.commons.io.FileUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.javaboy.ask_for_leave_demo.config.FlowableConfig;
import org.javaboy.ask_for_leave_demo.mapper.UserMapper;
import org.javaboy.ask_for_leave_demo.model.AskForLeaveVO;
import org.javaboy.ask_for_leave_demo.model.RespBean;
import org.javaboy.ask_for_leave_demo.model.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.javaboy.ask_for_leave_demo.model.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author xyma
 * @version 1.0
 * @data 2023/7/10 22:02
 */
@Service
public class AskForLeaveService {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    TaskService taskService;

    @Autowired
    HistoryService historyService;

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

    public List<User> getAllUsers() {
        return userMapper.getAllUsersExcludeCurrent(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public List<AskForLeaveVO> getUnapproveProcess() {
        List<AskForLeaveVO> list = new ArrayList<>();
        List<Execution> executions = runtimeService.createExecutionQuery().startedBy(SecurityContextHolder.getContext().getAuthentication().getName()).list();
        for (Execution execution : executions) {
            String executionId = execution.getId();
            Integer days = (Integer) runtimeService.getVariable(executionId, "days");
            String approveUser = (String) runtimeService.getVariable(executionId, "approveUser");
            String reason = (String) runtimeService.getVariable(executionId, "reason");
            Date endTime = (Date) runtimeService.getVariable(executionId, "endTime");
            Date startTime = (Date) runtimeService.getVariable(executionId, "startTime");
            list.add(new AskForLeaveVO(days, reason, startTime, endTime, approveUser, execution.getProcessInstanceId()));
        }
        return list;
    }

    public void processImage(String processId, HttpServletResponse response) throws IOException {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey("askforleave_demo").latestVersion().singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pd.getId());
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessEngineConfiguration processEngineConfig = processEngine.getProcessEngineConfiguration();

        List<String> highLightActivities = new ArrayList<>();
        List<String> highLightFlows = new ArrayList<>();
        List<HistoricActivityInstance> instances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).list();
        for (HistoricActivityInstance ai : instances) {
            if (ai.getActivityType().equals("sequenceFlow")) {
                highLightFlows.add(ai.getActivityId());
            } else {
                highLightActivities.add(ai.getActivityId());
            }
        }
        InputStream is = generator.generateDiagram(bpmnModel, "PNG", highLightActivities, highLightFlows,
                processEngineConfig.getActivityFontName(),
                processEngineConfig.getLabelFontName(), processEngineConfig.getAnnotationFontName(),
                processEngineConfig.getClassLoader(), 1.0, true);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        FileCopyUtils.copy(is, response.getOutputStream());
    }

//    public void processImage2(String processId, HttpServletResponse response) throws IOException {
//        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey("askforleave_demo").latestVersion().singleResult();
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(pd.getId());
//        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
//        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        ProcessEngineConfiguration processEngineConfig = processEngine.getProcessEngineConfiguration();
//
//        List<String> highLightActivities = new ArrayList<>();
//        List<String> highLightFlows = new ArrayList<>();
//        List<ActivityInstance> instances = runtimeService.createActivityInstanceQuery().processInstanceId(processId).list();
//        for (ActivityInstance ai : instances) {
//            if (ai.getActivityType().equals("sequenceFlow")) {
//                highLightFlows.add(ai.getActivityId());
//            } else {
//                highLightActivities.add(ai.getActivityId());
//            }
//        }
//        InputStream is = generator.generateDiagram(bpmnModel, "PNG", highLightActivities, highLightFlows,
//                processEngineConfig.getActivityFontName(),
//                processEngineConfig.getLabelFontName(), processEngineConfig.getAnnotationFontName(),
//                processEngineConfig.getClassLoader(), 1.0, true);
//        response.setContentType(MediaType.IMAGE_PNG_VALUE);
//        FileCopyUtils.copy(is, response.getOutputStream());
//    }

    public List<TaskVO> getCurrentAllTask() {
        List<TaskVO> taskVOS = new ArrayList<>();
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Task> list = taskService.createTaskQuery().taskAssignee(currentUserName).list();
        for (Task task : list) {
            Integer days = (Integer) taskService.getVariable(task.getId(), "days");
            String reason = (String) taskService.getVariable(task.getId(), "reason");
            String applicant = (String) taskService.getVariable(task.getId(), "applicant");
            Date startTime = (Date) taskService.getVariable(task.getId(), "startTime");
            Date endTime = (Date) taskService.getVariable(task.getId(), "endTime");
            TaskVO taskVO = new TaskVO(days, reason, startTime, endTime, currentUserName, task.getId(), null, applicant);
            taskVOS.add(taskVO);
        }
        return taskVOS;
    }

    public RespBean approve(TaskVO taskVO) {
        try {
            Map<String, Object> vars = new HashMap<>();
            vars.put("approval", taskVO.getApproval());
            taskService.complete(taskVO.getTaskId(), vars);
            return RespBean.ok("审批成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RespBean.error("审批失败");
    }

    public List<AskForLeaveVO> getHistoryProcess() {
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().startedBy(SecurityContextHolder.getContext().getAuthentication().getName()).finished().list();
        List<AskForLeaveVO> askForLeaveVOS = new ArrayList<>();
        for (HistoricProcessInstance hpi : list) {
            List<HistoricVariableInstance> variableInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(hpi.getId()).list();
            AskForLeaveVO askForLeaveVO = new AskForLeaveVO();
            askForLeaveVO.setProcessId(hpi.getId());
            for (HistoricVariableInstance hvi : variableInstanceList) {
                String variableName = hvi.getVariableName();
                Object value = hvi.getValue();
                if ("days".equals(variableName)) {
                    askForLeaveVO.setDays(((Integer) value));
                } else if ("reason".equals(variableName)) {
                    askForLeaveVO.setReason(((String) value));
                } else if ("startTime".equals(variableName)) {
                    askForLeaveVO.setStartTime(((Date) value));
                } else if ("endTime".equals(variableName)) {
                    askForLeaveVO.setEndTime(((Date) value));
                } else if ("approveUser".equals(variableName)) {
                    askForLeaveVO.setApproveUser(((String) value));
                } else if ("approval".equals(variableName)) {
                    askForLeaveVO.setApproval(((Boolean) value));
                }
            }
            askForLeaveVOS.add(askForLeaveVO);
        }
        return askForLeaveVOS;
    }
}
