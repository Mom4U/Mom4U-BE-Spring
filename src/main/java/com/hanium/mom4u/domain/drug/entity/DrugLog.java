package com.hanium.mom4u.domain.drug.entity;

import com.hanium.mom4u.domain.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "drug_log")
public class DrugLog extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drug_log_id")
    private Long id;

    @Column(name = "query")
    private String query;

    @Column(name = "answer")
    private String answer;
}
