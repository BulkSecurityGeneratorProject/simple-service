package com.pure.service.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import io.github.jhipster.service.filter.InstantFilter;




/**
 * Criteria class for the CustomerCousultantAssignHistory entity. This class is used in CustomerCousultantAssignHistoryResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /customer-cousultant-assign-histories?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CustomerCousultantAssignHistoryCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter olderFollowerLogin;

    private StringFilter olderFollowerName;

    private StringFilter newFollowerLogin;

    private StringFilter newFollowerName;

    private StringFilter comments;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter customerId;

    public CustomerCousultantAssignHistoryCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getOlderFollowerLogin() {
        return olderFollowerLogin;
    }

    public void setOlderFollowerLogin(StringFilter olderFollowerLogin) {
        this.olderFollowerLogin = olderFollowerLogin;
    }

    public StringFilter getOlderFollowerName() {
        return olderFollowerName;
    }

    public void setOlderFollowerName(StringFilter olderFollowerName) {
        this.olderFollowerName = olderFollowerName;
    }

    public StringFilter getNewFollowerLogin() {
        return newFollowerLogin;
    }

    public void setNewFollowerLogin(StringFilter newFollowerLogin) {
        this.newFollowerLogin = newFollowerLogin;
    }

    public StringFilter getNewFollowerName() {
        return newFollowerName;
    }

    public void setNewFollowerName(StringFilter newFollowerName) {
        this.newFollowerName = newFollowerName;
    }

    public StringFilter getComments() {
        return comments;
    }

    public void setComments(StringFilter comments) {
        this.comments = comments;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public StringFilter getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(StringFilter lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "CustomerCousultantAssignHistoryCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (olderFollowerLogin != null ? "olderFollowerLogin=" + olderFollowerLogin + ", " : "") +
                (olderFollowerName != null ? "olderFollowerName=" + olderFollowerName + ", " : "") +
                (newFollowerLogin != null ? "newFollowerLogin=" + newFollowerLogin + ", " : "") +
                (newFollowerName != null ? "newFollowerName=" + newFollowerName + ", " : "") +
                (comments != null ? "comments=" + comments + ", " : "") +
                (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
                (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
                (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
                (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
            "}";
    }

}
