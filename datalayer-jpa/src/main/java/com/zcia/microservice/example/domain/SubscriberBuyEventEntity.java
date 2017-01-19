package com.zcia.microservice.example.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "subscribers_events", schema = "ec")
@IdClass(SubscriberBuyEventId.class)
public class SubscriberBuyEventEntity implements Serializable
{
    private static final long serialVersionUID = -1040037996244589620L;

    @Id
    @ManyToOne
    @JoinColumn(name = "subscriberid")
    private SubscriberEntity subscriber;

    @Id
    @ManyToOne
    @JoinColumn(name = "categoryid")
    private CategoryEntity category;

    @Column
    private Long num;

    public SubscriberBuyEventEntity()
    {
        super();
    }

    public SubscriberEntity getSubscriber()
    {
        return subscriber;
    }

    public void setSubscriber(SubscriberEntity subscriber)
    {
        this.subscriber = subscriber;
    }

    public CategoryEntity getCategory()
    {
        return category;
    }

    public void setCategory(CategoryEntity category)
    {
        this.category = category;
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
