package org.javaboy.ask_for_leave_demo.task;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author xyma
 * @version 1.0
 * @data 2023/7/10 19:51
 */
public class RejectServiceTask implements JavaDelegate {
    private static final Logger logger = LoggerFactory.getLogger(RejectServiceTask.class);

    @Override
    public void execute(DelegateExecution execution) {
        Map<String, Object> variables = execution.getVariables();
        Object days = variables.get("days");
        Object name = variables.get("name");
        logger.info("{} 请假 {} 天的申请没有通过审批", name, days);
    }
}
