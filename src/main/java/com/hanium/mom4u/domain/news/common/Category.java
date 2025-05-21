package com.hanium.mom4u.domain.news.common;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public enum Category {
    HEALTH("건강"),
    FINANCIAL("지원금"),
    PREGNANCY_PLANNING("임신준비"),
    CHILD("출산/육아"),
    EDUCATION("교육프로그램"),
    EMOTIONAL("정서");

    private String displayName;
}
