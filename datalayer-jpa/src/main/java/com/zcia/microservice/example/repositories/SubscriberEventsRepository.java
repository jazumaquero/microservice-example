package com.zcia.microservice.example.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.zcia.microservice.example.domain.SubscriberBuyEventEntity;

@RepositoryRestResource(path = "subscribers_agg", collectionResourceRel = "subscribers_agg")
public interface SubscriberEventsRepository extends PagingAndSortingRepository<SubscriberBuyEventEntity, Long>
{

}
