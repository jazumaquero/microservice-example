package com.zcia.microservice.example.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "buy_event", schema = "ec")
@SequenceGenerator(name = "buy_event_seq", sequenceName = "ec.buy_event_seq", initialValue = 1, allocationSize = 1)
public class BuyingEventEntity implements Serializable
{
    private static final long serialVersionUID = -1250157316381845398L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "buy_event_seq")
    private Long id;

    @Column
    private Date tstamp;

    @Column(name = "productid")
    @NotNull
    private Long productId;

    @Column(name = "subscriberid")
    @NotNull
    private Long subscriberId;

    public BuyingEventEntity()
    {
        super();
        this.tstamp = new Date();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Date getTstamp()
    {
        return tstamp;
    }

    public void setTstamp(Date tstamp)
    {
        this.tstamp = tstamp;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Long getSubscriberId()
    {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId)
    {
        this.subscriberId = subscriberId;
    }

}
