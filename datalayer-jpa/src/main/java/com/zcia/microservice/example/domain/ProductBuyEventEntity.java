package com.zcia.microservice.example.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "product_events", schema = "ec")
@IdClass(ProductBuyEventId.class)
public class ProductBuyEventEntity implements Serializable
{
    private static final long serialVersionUID = -5097063682625903856L;

    @Id
    @ManyToOne
    @JoinColumn(name = "productid")
    private ProductEntity product;

    @Id
    @ManyToOne
    @JoinColumn(name = "categoryid")
    private CategoryEntity category;

    @Column
    private Long num;

    public ProductBuyEventEntity()
    {
        super();
    }

    public ProductEntity getProduct()
    {
        return product;
    }

    public void setProduct(ProductEntity product)
    {
        this.product = product;
    }

    public CategoryEntity getCategory()
    {
        return category;
    }

    public void setCategory(CategoryEntity category)
    {
        this.category = category;
    }

    public Long getNum()
    {
        return num;
    }

    public void setNum(Long num)
    {
        this.num = num;
    }

}
