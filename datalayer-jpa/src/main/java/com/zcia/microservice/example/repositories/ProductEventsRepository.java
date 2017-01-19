package com.zcia.microservice.example.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.zcia.microservice.example.domain.ProductBuyEventEntity;

@RepositoryRestResource(path = "products_agg", collectionResourceRel = "products_agg")
public interface ProductEventsRepository extends PagingAndSortingRepository<ProductBuyEventEntity, Long>
{

}
