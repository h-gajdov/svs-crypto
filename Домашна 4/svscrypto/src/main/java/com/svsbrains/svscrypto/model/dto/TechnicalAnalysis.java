package com.svsbrains.svscrypto.model.dto;

import java.util.List;

public class TechnicalAnalysis {
    private List<String> day1;
    private List<String> week1;
    private List<String> month1;

    // Getters and Setters
    public List<String> getDay1() { return day1; }
    public void setDay1(List<String> day1) { this.day1 = day1; }

    public List<String> getWeek1() { return week1; }
    public void setWeek1(List<String> week1) { this.week1 = week1; }

    public List<String> getMonth1() { return month1; }
    public void setMonth1(List<String> month1) { this.month1 = month1; }
}
