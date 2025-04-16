package com.example.nplusone.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 통합 테스트 예시.
 *
 * - @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) :
 *   실제 서버 포트를 랜덤으로 할당하여 전체 컨텍스트 구동.
 * - @AutoConfigureMockMvc : MockMvc 주입 받음
 * - @ActiveProfiles("test") : test 프로파일을 쓴다면 지정
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @LocalServerPort
    private int port;

    // if when then
    @Test
    @DisplayName("N+1 발생 데모 요청")
    void testNPlusOne() throws Exception {
        mockMvc.perform(get("/api/members/n-plus-one"))
                .andExpect(status().isOk())
                .andExpect(content().string("N+1 Demo - Check console"));
    }

    @Test
    @DisplayName("Fetch Join 예시")
    void testFetchJoin() throws Exception {
        mockMvc.perform(get("/api/members/fetch-join"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    }

    @Test
    @DisplayName("DTO 조회 예시 - JSON 응답 검사")
    void testDto() throws Exception {
        mockMvc.perform(get("/api/members/dto"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // 응답 바디(JSON) 안에 [ { "memberId":1, "memberName":"xxx", ... }, ... ] 형태로 DTO 리스트가 내려온다고 가정.
        // 실제로는 jsonPath 등을 써서 assert 가능.
    }

    @Test
    @DisplayName("JDBC Template 예시 - JSON 응답 검사")
    void testJdbc() throws Exception {
        mockMvc.perform(get("/api/members/jdbc"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // 응답이 List<Map<String, Object>> 형태이므로, 대략 [{ "MEMBER_ID": 1, "MEMBER_NAME": "xxx", ...}, ...]
    }

}
