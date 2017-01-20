package com.zcia.microservice.example.domain;

import java.io.Serializable;
import java.util.Objects;

public class SubscriberBuyEventId implements Serializable
{
    private static final long serialVersionUID = 6385954839406711179L;

    private Long subscriberId;
    private Long categoryId;

    public SubscriberBuyEventId()
    {
        super();
    }

    public Long getSubscriberId()
    {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId)
    {
        this.subscriberId = subscriberId;
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
        return Objects.hash(categoryId, subscriberId);
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean equals = this == obj;
        if (!equals && obj != null && obj instanceof SubscriberBuyEventId)
        {
            SubscriberBuyEventId other = (SubscriberBuyEventId) obj;
            equals = Objects.equals(categoryId, other.categoryId) && Objects.equals(subscriberId, other.subscriberId);
        }
        return equals;
    }

}
