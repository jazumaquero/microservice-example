package com.zcia.microservice.example.domain;

import java.io.Serializable;
import java.util.Objects;

public class ProductBuyEventId implements Serializable
{
    private static final long serialVersionUID = -4341429537206893503L;

    private Long productId;
    private Long categoryId;

    public ProductBuyEventId()
    {
        super();
    }

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

    @Override
    public int hashCode()
    {
        return Objects.hash(categoryId, productId);
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean equals = this == obj;
        if (!equals && obj != null && obj instanceof ProductBuyEventId)
        {
            ProductBuyEventId other = (ProductBuyEventId) obj;
            equals = Objects.equals(categoryId, other.categoryId) && Objects.equals(productId, other.productId);
        }
        return equals;
    }

}
