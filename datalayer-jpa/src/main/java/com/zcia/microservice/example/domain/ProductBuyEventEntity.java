package com.zcia.microservice.example.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "product_events", schema = "ec")
@IdClass(ProductBuyEventId.class)
public class ProductBuyEventEntity implements Serializable
{
    private static final long serialVersionUID = -5097063682625903856L;

    @Id
    @Column(name = "productid")
    private Long productId;

    @Id
    @Column(name = "categoryid")
    private Long categoryId;

    @Column(name = "product")
    private String productName;

    @Column(name = "category")
    private String categoryName;

    @Column
    private Long num;

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
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
