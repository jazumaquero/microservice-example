/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2017 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.zcia.microservice.example.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.zcia.microservice.example.domain.ProductEntity;

@RepositoryRestResource
public interface ProductsRepository extends PagingAndSortingRepository<ProductEntity, Long>
{

}
