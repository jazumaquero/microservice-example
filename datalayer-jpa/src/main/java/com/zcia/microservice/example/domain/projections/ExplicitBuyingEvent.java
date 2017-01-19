package com.zcia.microservice.example.domain.projections;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.zcia.microservice.example.domain.BuyingEventEntity;

@Projection(name = "explicit", types = { BuyingEventEntity.class })
public interface ExplicitBuyingEvent
{
    public Long getId();

    public Date getTstamp();

    @Value("#{subscriber.id}")
    public String getSubscriberId();

    @Value("#{subscriber.name}")
    public String getSubscriberName();

    @Value("#{subscriber.email}")
    public String getSubscriberEmail();

    @Value("#{product.id}")
    public String getProductId();

    @Value("#{product.id}")
    public String getProductName();

    @Value("#{product.price}")
    public String getProductPrice();
}
