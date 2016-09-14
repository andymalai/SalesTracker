package com.webmne.salestracker.actionlog.model;

/**
 * Created by sagartahelyani on 22-08-2016.
 */
public class ActionLogModel {

    /**
     * Id : #AMG_130
     * AgentName : Sagar1
     * Description : manoj update thay gyo
     * CreatedDatetime : 2016/09/14 01:09:32
     * Status : R
     * DepartmentName : Hub Operations
     * Sla : 0
     * Reopen : false
     * UpdatedDatetime : 2016/09/14 02:51:35
     * AttachmentPath : sites/default/files/userfile
     * Attachment : add_staff_1.png
     * RemarkCount : 4
     */

    private String Id;
    private String AgentName;
    private String Description;
    private String CreatedDatetime;
    private String Status;
    private String DepartmentName;
    private int Sla;
    private String Reopen;
    private String UpdatedDatetime;
    private String AttachmentPath;
    private String Attachment;
    private String RemarkCount;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getAgentName() {
        return AgentName;
    }

    public void setAgentName(String AgentName) {
        this.AgentName = AgentName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getCreatedDatetime() {
        return CreatedDatetime;
    }

    public void setCreatedDatetime(String CreatedDatetime) {
        this.CreatedDatetime = CreatedDatetime;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String DepartmentName) {
        this.DepartmentName = DepartmentName;
    }

    public int getSla() {
        return Sla;
    }

    public void setSla(int Sla) {
        this.Sla = Sla;
    }

    public String getReopen() {
        return Reopen;
    }

    public void setReopen(String Reopen) {
        this.Reopen = Reopen;
    }

    public String getUpdatedDatetime() {
        return UpdatedDatetime;
    }

    public void setUpdatedDatetime(String UpdatedDatetime) {
        this.UpdatedDatetime = UpdatedDatetime;
    }

    public String getAttachmentPath() {
        return AttachmentPath;
    }

    public void setAttachmentPath(String AttachmentPath) {
        this.AttachmentPath = AttachmentPath;
    }

    public String getAttachment() {
        return Attachment;
    }

    public void setAttachment(String Attachment) {
        this.Attachment = Attachment;
    }

    public String getRemarkCount() {
        return RemarkCount;
    }

    public void setRemarkCount(String RemarkCount) {
        this.RemarkCount = RemarkCount;
    }
}
