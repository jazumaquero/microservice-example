package com.zcia.microservice.example.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.zcia.microservice.example.domain.BuyingEventEntity;

@RepositoryRestResource(path = "events", collectionResourceRel = "events")
public interface BuyingEventRepository extends CrudRepository<BuyingEventEntity, Long>
{

}
