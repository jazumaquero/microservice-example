package com.zcia.microservice.example.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "products", schema = "ec", uniqueConstraints = { @UniqueConstraint(name = "product_name_unique", columnNames = { "name" }) })
@SequenceGenerator(name = "product_seq", sequenceName = "ec.product_seq", initialValue = 1, allocationSize = 1)
public class ProductEntity implements Serializable
{
    private static final long serialVersionUID = 2894362722708300394L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    private Long id;

    @Column
    @NotNull
    private String name;

    @Column
    @NotNull
    private Float price;

    @ManyToMany(mappedBy = "products")
    private Set<CategoryEntity> categories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<BuyingEventEntity> events;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductBuyEventEntity> productEvents;

    public ProductEntity()
    {
        super();
        this.categories = new HashSet<>();
        this.events = new ArrayList<>();
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

    public Float getPrice()
    {
        return price;
    }

    public void setPrice(Float price)
    {
        this.price = price;
    }

    public Set<CategoryEntity> getCategories()
    {
        return categories;
    }

    public void setCategories(Set<CategoryEntity> categories)
    {
        this.categories = categories;
    }

    public List<BuyingEventEntity> getEvents()
    {
        return events;
    }

    public void setEvents(List<BuyingEventEntity> events)
    {
        this.events = events;
    }

    public List<ProductBuyEventEntity> getProductEvents()
    {
        return productEvents;
    }

    public void setProductEvents(List<ProductBuyEventEntity> productEvents)
    {
        this.productEvents = productEvents;
    }

}
