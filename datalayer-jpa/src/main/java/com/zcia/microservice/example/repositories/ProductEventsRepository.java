package com.zcia.microservice.example.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.zcia.microservice.example.domain.ProductBuyEventEntity;

@RepositoryRestResource(path = "products_agg", collectionResourceRel = "products_agg")
public interface ProductEventsRepository extends PagingAndSortingRepository<ProductBuyEventEntity, Long>
{
    Page<ProductBuyEventEntity> findByCategoryId(@Param("id") Long id, Pageable page);

    Page<ProductBuyEventEntity> findByCategoryName(@Param("name") String name, Pageable page);

    Page<ProductBuyEventEntity> findByProductId(@Param("id") Long id, Pageable page);

    Page<ProductBuyEventEntity> findByProductName(@Param("name") String name, Pageable page);

}
