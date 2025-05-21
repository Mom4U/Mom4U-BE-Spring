package com.hanium.mom4u.domain.news.entity;

import com.hanium.mom4u.domain.news.common.Category;
import jakarta.persistence.*;

@Entity
@Table(name = "news")
public class News {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "img_url")
    private String imgUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;
}
