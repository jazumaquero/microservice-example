package com.zcia.microservice.example.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.zcia.microservice.example.domain.ProductBuyEventEntity;
import com.zcia.microservice.example.domain.SubscriberBuyEventEntity;

@RepositoryRestResource(path = "subscribers_agg", collectionResourceRel = "subscribers_agg")
public interface SubscriberEventsRepository extends PagingAndSortingRepository<SubscriberBuyEventEntity, Long>
{
    Page<ProductBuyEventEntity> findByCategoryId(@Param("id") Long id, Pageable page);

    Page<ProductBuyEventEntity> findByCategoryName(@Param("name") String name, Pageable page);

    Page<ProductBuyEventEntity> findBySubscriberId(@Param("id") Long id, Pageable page);

    Page<ProductBuyEventEntity> findBySubscriberName(@Param("name") String name, Pageable page);

}
