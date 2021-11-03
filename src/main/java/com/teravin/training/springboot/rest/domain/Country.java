package com.teravin.training.springboot.rest.domain;

import com.teravin.base.domain.Maintenance;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "trn_country")
public class Country extends Maintenance {
}