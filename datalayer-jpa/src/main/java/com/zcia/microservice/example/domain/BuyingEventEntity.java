package com.zcia.microservice.example.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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

    @ManyToOne
    @JoinColumn(name = "productid")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "subscriberid")
    private SubscriberEntity subscriber;

    public BuyingEventEntity()
    {
        super();
    }

    public Date getTstamp()
    {
        return tstamp;
    }

    public void setTstamp(Date tstamp)
    {
        this.tstamp = tstamp;
    }

    public ProductEntity getProduct()
    {
        return product;
    }

    public void setProduct(ProductEntity product)
    {
        this.product = product;
    }

    public SubscriberEntity getSubscriber()
    {
        return subscriber;
    }

    public void setSubscriber(SubscriberEntity subscriber)
    {
        this.subscriber = subscriber;
    }

}
