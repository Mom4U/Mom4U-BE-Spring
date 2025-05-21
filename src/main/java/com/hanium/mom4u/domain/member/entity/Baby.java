package com.hanium.mom4u.domain.member.entity;

import com.hanium.mom4u.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Table(name = "baby")
@Getter
public class Baby extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "baby_id")
    private Long id;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "pregnant_date")
    private LocalDate pregnantDate;

    @Column(name = "month_level")
    private int monthLevel;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "is_ended")
    private boolean isEnded;

    @Column(name = "gender")
    private String gender;

    @Column(name = "name")
    private String name;

    @Column(name = "birth_year")
    private int birthYear;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
