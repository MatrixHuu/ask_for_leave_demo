package org.javaboy.ask_for_leave_demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * @author xyma
 * @version 1.0
 * @data 2023/7/10 21:56
 */
public class AskForLeaveVO {
    private Integer days;
    private String reason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Shanghai")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Shanghai")
    private Date endTime;

    private String approveUser;
    private Boolean approval;
    private String processId;

    public AskForLeaveVO(Integer days, String reason, Date startTime, Date endTime, String approveUser, Boolean approval, String processId) {
        this.days = days;
        this.reason = reason;
        this.startTime = startTime;
        this.endTime = endTime;
        this.approveUser = approveUser;
        this.approval = approval;
        this.processId = processId;
    }

    public Boolean getApproval() {
        return approval;
    }

    public void setApproval(Boolean approval) {
        this.approval = approval;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public AskForLeaveVO(Integer days, String reason, Date startTime, Date endTime, String approveUser, String processId) {
        this.days = days;
        this.reason = reason;
        this.startTime = startTime;
        this.endTime = endTime;
        this.approveUser = approveUser;
        this.processId = processId;
    }

    public AskForLeaveVO() {
    }

    public String getApproveUser() {
        return approveUser;
    }

    public void setApproveUser(String approveUser) {
        this.approveUser = approveUser;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
