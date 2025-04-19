package com.example.springdatajpa.domain;

public interface UsernameOnly {
    // 엔티티의 필드를 getter  형식으로 작성 시 - 해당 필드만 선택해서 조회한다.
    String getUsername();
}
