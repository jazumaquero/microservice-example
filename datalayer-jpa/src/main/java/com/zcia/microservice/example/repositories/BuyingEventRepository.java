package com.zcia.microservice.example.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.zcia.microservice.example.domain.BuyingEventEntity;
import com.zcia.microservice.example.domain.projections.SimpleBuyingEventProjection;

@RepositoryRestResource(path = "events", collectionResourceRel = "events", excerptProjection = SimpleBuyingEventProjection.class)
public interface BuyingEventRepository extends CrudRepository<BuyingEventEntity, Long>
{

}
