package com.zcia.microservice.example.domain;

import java.io.Serializable;
import java.util.Objects;

public class ProductBuyEventId implements Serializable
{
    private static final long serialVersionUID = -4341429537206893503L;

    private ProductEntity product;
    private CategoryEntity category;

    public ProductBuyEventId()
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

    @Override
    public int hashCode()
    {
        return Objects.hash(category.getId(), product.getId());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProductBuyEventId other = (ProductBuyEventId) obj;
        if (category == null)
        {
            if (other.category != null)
                return false;
        }
        else if (!category.getId().equals(other.category.getId()))
            return false;
        if (product == null)
        {
            if (other.product != null)
                return false;
        }
        else if (!product.getId().equals(other.product.getId()))
            return false;
        return true;
    }

}
