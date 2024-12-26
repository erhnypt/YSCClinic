package com.yscclinic.entity;

import com.yscclinic.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;
}
