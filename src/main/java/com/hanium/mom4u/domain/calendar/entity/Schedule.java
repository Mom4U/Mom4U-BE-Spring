package com.hanium.mom4u.domain.calendar.entity;

import com.hanium.mom4u.domain.calendar.common.Color;
import com.hanium.mom4u.domain.common.BaseEntity;
import com.hanium.mom4u.domain.member.entity.Member;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "calendar")
public class Schedule extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    private Color color;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "health_check")
    private String healthCheck;
}
