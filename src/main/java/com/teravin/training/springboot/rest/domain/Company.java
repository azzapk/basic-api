package com.teravin.training.springboot.rest.domain;

import com.teravin.base.domain.Updatable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "trn_company")
public class Company extends Updatable {

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
            name = "delete_company"
    )
    private boolean deleteCompany;
    @Column(
            name = "inactive_company"
    )
    private boolean inactiveCompany;
}
