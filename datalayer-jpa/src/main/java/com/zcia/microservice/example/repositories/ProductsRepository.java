package com.zcia.microservice.example.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.zcia.microservice.example.domain.ProductEntity;
import com.zcia.microservice.example.domain.projections.SimpleProductProjection;

@RepositoryRestResource(path = "products", collectionResourceRel = "products", excerptProjection = SimpleProductProjection.class)
public interface ProductsRepository extends CrudRepository<ProductEntity, Long>
{

}
