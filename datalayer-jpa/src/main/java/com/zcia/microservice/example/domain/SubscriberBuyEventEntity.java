package com.zcia.microservice.example.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "subscribers_events", schema = "ec")
@IdClass(SubscriberBuyEventId.class)
public class SubscriberBuyEventEntity implements Serializable
{
    private static final long serialVersionUID = -1040037996244589620L;

    @Id
    @Column(name = "subscriberid")
    private Long subscriberId;

    @Id
    @Column(name = "categoryid")
    private Long categoryId;

    @Column(name = "subscriber_name")
    private String subscriberName;

    @Column(name = "subscriber_email")
    private String subscriberEmail;

    @Column(name = "category")
    private String categoryName;

    @Column
    private Long num;

    public SubscriberBuyEventEntity()
    {
        super();
    }

    public Long getSubscriberId()
    {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId)
    {
        this.subscriberId = subscriberId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public String getSubscriberName()
    {
        return subscriberName;
    }

    public void setSubscriberName(String subscriberName)
    {
        this.subscriberName = subscriberName;
    }

    public String getSubscriberEmail()
    {
        return subscriberEmail;
    }

    public void setSubscriberEmail(String subscriberEmail)
    {
        this.subscriberEmail = subscriberEmail;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public Long getNum()
    {
        return num;
    }

    public void setNum(Long num)
    {
        this.num = num;
    }

}
