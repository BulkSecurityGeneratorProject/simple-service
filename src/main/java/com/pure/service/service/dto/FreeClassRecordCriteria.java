package com.pure.service.service.dto;

import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.InstantFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import java.io.Serializable;


/**
 * Criteria class for the FreeClassRecord entity. This class is used in FreeClassRecordResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /free-class-records?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FreeClassRecordCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter personName;

    private StringFilter contactPhoneNumber;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter marketChannelCategoryId;

    public FreeClassRecordCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getPersonName() {
        return personName;
    }

    public void setPersonName(StringFilter personName) {
        this.personName = personName;
    }

    public StringFilter getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(StringFilter contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
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

    public LongFilter getMarketChannelCategoryId() {
        return marketChannelCategoryId;
    }

    public void setMarketChannelCategoryId(LongFilter marketChannelCategoryId) {
        this.marketChannelCategoryId = marketChannelCategoryId;
    }

    @Override
    public String toString() {
        return "FreeClassRecordCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (personName != null ? "personName=" + personName + ", " : "") +
                (contactPhoneNumber != null ? "contactPhoneNumber=" + contactPhoneNumber + ", " : "") +
                (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
                (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
                (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
                (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
                (marketChannelCategoryId != null ? "marketChannelCategoryId=" + marketChannelCategoryId + ", " : "") +
            "}";
    }

}
