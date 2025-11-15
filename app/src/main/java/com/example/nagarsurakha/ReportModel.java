package com.example.nagarsurakha;

public class ReportModel {

    private String report_id;
    private String report_time;
    private String status;
    private String department;

    // ✅ Default constructor
    public ReportModel() {}

    // ✅ Full constructor
    public ReportModel(String report_id, String report_time, String status, String department) {
        this.report_id = report_id;
        this.report_time = report_time;
        this.status = status;
        this.department = department;
    }

    // ✅ Getters
    public String getReport_id() { return report_id; }
    public String getReport_time() { return report_time; }
    public String getStatus() { return status; }
    public String getDepartment() { return department; }

    // ✅ Setters
    public void setReport_id(String report_id) { this.report_id = report_id; }
    public void setReport_time(String report_time) { this.report_time = report_time; }
    public void setStatus(String status) { this.status = status; }
    public void setDepartment(String department) { this.department = department; }
}
