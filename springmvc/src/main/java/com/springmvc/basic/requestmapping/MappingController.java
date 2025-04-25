package com.springmvc.basic.requestmapping;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@RestController
public class MappingController {

    private Logger log = LoggerFactory.getLogger(getClass());
    /**
     * 기본 요청
     * 둘 다 허용 /hello-basic
     * HTTP 메서드 모두 허용
     */

    @RequestMapping("/hello-basic")
    public String helloBasic() {
        log.info("helloBasic");
        return "ok";
    }


    @RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)
    public String mappingGetV1() {
        log.info("mappingGetV1");
        return "ok";
    }


    // 축약버전
    @GetMapping(value = "/mapping-get-v2")
    public String mappingGetV2() {
        log.info("mappingGetV2");
        return "ok";
    }

    // pathVariable 사용 - pathParameter

    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String data) {
        log.info("mappingPath userId = {}", data);
        return "ok";
    }

    @GetMapping("/mapping/users/{userId}/orders/{orderId}")
    public String mappingPath(@PathVariable("userId") String userId , @PathVariable Long orderId) {
        log.info("mappingPath useId = {}, orderId = {}",userId,orderId);
        return "ok";
    }

    /**
     * Content Type 헤더 기반 추가 매핑 Media Type
     * 일치 하지 않는다면 415 에러를 반환한다.
     * consumes = "application/json"
     *  consumes = "application/*"
     */


    @PostMapping(value = "/mapping-consume", consumes = "application/json")
    public String mappingConsumes() {
        log.info("mappingConsumes");
        return "ok";
    }






}
