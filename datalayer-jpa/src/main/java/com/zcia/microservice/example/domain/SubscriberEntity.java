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
@Table(name = "subscribers", schema = "ec", uniqueConstraints = { @UniqueConstraint(name = "subscriber_email_unique", columnNames = { "email" }) })
@SequenceGenerator(name = "subscriber_seq", sequenceName = "ec.subscriber_seq", initialValue = 1, allocationSize = 1)
public class SubscriberEntity implements Serializable
{
    private static final long serialVersionUID = 4351507993719944582L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscriber_seq")
    private Long id;

    @Column
    @NotNull
    private String name;

    @Column
    @NotNull
    private String email;

    @ManyToMany(mappedBy = "subscribers")
    private Set<CategoryEntity> categories;

    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL)
    private List<BuyingEventEntity> events;

    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL)
    private List<SubscriberBuyEventEntity> subscriberEvents;

    public SubscriberEntity()
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

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
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

    public List<SubscriberBuyEventEntity> getSubscriberEvents()
    {
        return subscriberEvents;
    }

    public void setSubscriberEvents(List<SubscriberBuyEventEntity> subscriberEvents)
    {
        this.subscriberEvents = subscriberEvents;
    }

}
