package com.zcia.microservice.example.domain.projections;

import org.springframework.data.rest.core.config.Projection;

import com.zcia.microservice.example.domain.ProductEntity;

@Projection(name = "simple", types = { ProductEntity.class })
public interface SimpleProductProjection
{
    public Long getId();

    public String getName();

    public Float getPrice();
}
