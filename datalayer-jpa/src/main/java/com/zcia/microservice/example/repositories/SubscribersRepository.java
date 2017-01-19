package com.zcia.microservice.example.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.zcia.microservice.example.domain.SubscriberEntity;
import com.zcia.microservice.example.domain.projections.SimpleSubscriberProjection;

@RepositoryRestResource(path = "subscribers", collectionResourceRel = "subscribers", excerptProjection = SimpleSubscriberProjection.class)
public interface SubscribersRepository extends CrudRepository<SubscriberEntity, Long>
{

}
