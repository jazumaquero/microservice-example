package com.zcia.microservice.example.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.zcia.microservice.example.domain.CategoryEntity;
import com.zcia.microservice.example.domain.projections.SimpleCategoryProjection;

@RepositoryRestResource(path = "categories", collectionResourceRel = "categories", excerptProjection = SimpleCategoryProjection.class)
public interface CatetoriesRepository extends CrudRepository<CategoryEntity, Long>
{

}
