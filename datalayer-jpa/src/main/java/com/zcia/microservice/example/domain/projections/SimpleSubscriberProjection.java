package com.zcia.microservice.example.domain.projections;

import org.springframework.data.rest.core.config.Projection;

import com.zcia.microservice.example.domain.SubscriberEntity;

@Projection(name = "simple", types = { SubscriberEntity.class })
public interface SimpleSubscriberProjection
{
    public Long getId();

    public String getName();

    public Float getEmail();
}
