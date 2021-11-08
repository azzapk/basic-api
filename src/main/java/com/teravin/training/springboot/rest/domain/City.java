package com.teravin.training.springboot.rest.domain;

import com.teravin.base.domain.Updatable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "trn_city")
public class City extends Updatable {

    @Id
    @Column(
            name = "code",
            length = 40,
            nullable = false
    )
    private String code;

    @Column(
            name = "name",
            length = 100,
            nullable = false
    )
    private String name;

    @Column(
            name = "description",
            length = 500
    )
    private String description;

    @Column(
            name = "idx",
            length = 3,
            nullable = false
    )
    private int index = 999;

    @Column(
            name = "delete_city"
    )
    private boolean deleteCity;
    @Column(
            name = "inactive_city"
    )
    private boolean inactiveCity;

    @ManyToOne
    private Country country;
}
