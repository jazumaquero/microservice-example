package com.zcia.microservice.example.domain.projections;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.zcia.microservice.example.domain.BuyingEventEntity;

@Projection(name = "simple", types = { BuyingEventEntity.class })
public interface SimpleBuyingEventProjection
{
    public Long getId();

    public Date getTstamp();

    public String getSubscriberId();

    public String getProductId();
}
