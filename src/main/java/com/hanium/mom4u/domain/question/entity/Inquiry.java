package com.hanium.mom4u.domain.question.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inquiry")
public class Inquiry {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    @Column(name = "content")
    private String content;
}
