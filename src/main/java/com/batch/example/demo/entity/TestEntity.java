package com.batch.example.demo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "test")
@Getter
@Setter
@NoArgsConstructor
public class TestEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
}
