package com.pure.service.service.dto.request;

import java.io.Serializable;

/**
 *
 */
public class ReportElement implements Serializable {

    private Long userId;
    private String userName;
    private Integer ageTooSmallCount = 0;
    private Integer errorInformation = 0;
    private Integer noWillingCount = 0;
    private Integer consideringCount = 0;
    private Integer scheduledCount = 0;
    private Integer dealedCount = 0; //已成交数量
    private Integer visitedCount = 0; //已成交数量
    private Integer newCreatedCount = 0;
    private Integer totalCount = 0;
    private String finishRate = "0";

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAgeTooSmallCount() {
        return ageTooSmallCount;
    }

    public void setAgeTooSmallCount(Integer ageTooSmallCount) {
        this.ageTooSmallCount = ageTooSmallCount;
    }

    public Integer getErrorInformation() {
        return errorInformation;
    }

    public void setErrorInformation(Integer errorInformation) {
        this.errorInformation = errorInformation;
    }

    public Integer getNoWillingCount() {
        return noWillingCount;
    }

    public void setNoWillingCount(Integer noWillingCount) {
        this.noWillingCount = noWillingCount;
    }

    public Integer getConsideringCount() {
        return consideringCount;
    }

    public void setConsideringCount(Integer consideringCount) {
        this.consideringCount = consideringCount;
    }

    public Integer getScheduledCount() {
        return scheduledCount;
    }

    public void setScheduledCount(Integer scheduledCount) {
        this.scheduledCount = scheduledCount;
    }

    public Integer getDealedCount() {
        return dealedCount;
    }

    public void setDealedCount(Integer dealedCount) {
        this.dealedCount = dealedCount;
    }

    public Integer getNewCreatedCount() {
        return newCreatedCount;
    }

    public void setNewCreatedCount(Integer newCreatedCount) {
        this.newCreatedCount = newCreatedCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getFinishRate() {
        return finishRate;
    }

    public void setFinishRate(String finishRate) {
        this.finishRate = finishRate;
    }

    public Integer getVisitedCount() {
        return visitedCount;
    }

    public void setVisitedCount(Integer visitedCount) {
        this.visitedCount = visitedCount;
    }
}
