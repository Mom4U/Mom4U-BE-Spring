package com.hanium.mom4u.domain.family.entity;

import com.hanium.mom4u.domain.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "daily_question")
public class DailyQuestion extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_question_id")
    private Long id;

    @Column(name = "question")
    private String question;

    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(name = "writer")
    private String writer;

    @Column(name = "answer")
    private String answer;
}
