package com.zcia.microservice.example.domain;

import java.io.Serializable;
import java.util.Objects;

public class SubscriberBuyEventId implements Serializable
{
    private static final long serialVersionUID = 6385954839406711179L;

    private SubscriberEntity subscriber;
    private CategoryEntity category;

    public SubscriberBuyEventId()
    {
        super();
    }

    public SubscriberEntity getSubscriber()
    {
        return subscriber;
    }

    public void setSubscriber(SubscriberEntity subscriber)
    {
        this.subscriber = subscriber;
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
        return Objects.hash(category.getId(), subscriber.getId());
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
        SubscriberBuyEventId other = (SubscriberBuyEventId) obj;
        if (category == null)
        {
            if (other.category != null)
                return false;
        }
        else if (!category.getId().equals(other.category.getId()))
            return false;
        if (subscriber == null)
        {
            if (other.subscriber != null)
                return false;
        }
        else if (!subscriber.getId().equals(other.subscriber.getId()))
            return false;
        return true;
    }

}
