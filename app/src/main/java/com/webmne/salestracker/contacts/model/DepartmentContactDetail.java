package com.webmne.salestracker.contacts.model;

/**
 * Created by vatsaldesai on 02-09-2016.
 */
public class DepartmentContactDetail {

    String deptId,deptName;

    public DepartmentContactDetail(String deptId, String deptName) {
        this.deptId = deptId;
        this.deptName = deptName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

/*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DepartmentContactDetail)) return false;

        DepartmentContactDetail that = (DepartmentContactDetail) o;

        if (getDeptId() != null ? !getDeptId().equals(that.getDeptId()) : that.getDeptId() != null)
            return false;
        return getDeptName() != null ? getDeptName().equals(that.getDeptName()) : that.getDeptName() == null;

    }

    @Override
    public int hashCode() {
        int result = getDeptId() != null ? getDeptId().hashCode() : 0;
        result = 31 * result + (getDeptName() != null ? getDeptName().hashCode() : 0);
        return result;
    }
*/
}
