package com.teravin.training.springboot.rest.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "trn_company_city")
public class CompanyCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    private Company company;

    @ManyToOne
    private City city;
}
