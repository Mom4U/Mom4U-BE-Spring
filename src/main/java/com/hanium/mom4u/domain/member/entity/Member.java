package com.hanium.mom4u.domain.member.entity;

import com.hanium.mom4u.domain.calendar.entity.Schedule;
import com.hanium.mom4u.domain.common.BaseEntity;
import com.hanium.mom4u.domain.family.entity.Family;
import com.hanium.mom4u.domain.member.common.Role;
import com.hanium.mom4u.domain.member.common.SocialType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "member")
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "gender")
    private String gender;

    @Column(name = "is_pregnant")
    private boolean isPregnant;

    @Column(name = "is_inactive")
    private boolean isInactive;

    @Column(name = "inactive_date")
    private LocalDate inactiveDate;

    @Column(name = "pregnant_week")
    private int pregnantWeek;

    @Column(name = "img_url")
    private String imgUrl;

    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    @OneToMany(mappedBy = "member")
    private List<Baby> babyList;

    @OneToMany(mappedBy = "member")
    private List<Schedule> scheduleList;
}
