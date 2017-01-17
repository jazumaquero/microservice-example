/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2017 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.zcia.microservice.example.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "categories", schema = "ec", uniqueConstraints = { @UniqueConstraint(name = "category_name_unique", columnNames = { "name" }) })
@SequenceGenerator(name = "category_seq", sequenceName = "category_seq", schema = "ec", initialValue = 1, allocationSize = 1)
public class CategoryEntity implements Serializable
{
    private static final long serialVersionUID = 5164540774408244410L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    private Long id;

    @Column
    @NotNull
    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<ProductEntity> products;

    @ManyToMany(mappedBy = "categories")
    private Set<SubscriberEntity> subscribers;

    public CategoryEntity()
    {
        super();
        this.products = new HashSet<>();
        this.subscribers = new HashSet<>();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Set<ProductEntity> getProducts()
    {
        return products;
    }

    public void setProducts(Set<ProductEntity> products)
    {
        this.products = products;
    }

    public Set<SubscriberEntity> getSubscribers()
    {
        return subscribers;
    }

    public void setSubscribers(Set<SubscriberEntity> subscribers)
    {
        this.subscribers = subscribers;
    }

}
