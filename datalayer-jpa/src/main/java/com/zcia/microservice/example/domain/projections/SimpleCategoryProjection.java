package com.zcia.microservice.example.domain.projections;

import org.springframework.data.rest.core.config.Projection;

import com.zcia.microservice.example.domain.CategoryEntity;

@Projection(name = "simple", types = { CategoryEntity.class })
public interface SimpleCategoryProjection
{
    public Long getId();

    public String getName();
}
