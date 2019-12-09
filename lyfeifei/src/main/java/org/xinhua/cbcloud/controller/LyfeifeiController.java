package org.xinhua.cbcloud.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LyfeifeiController {

    private Logger logger = LoggerFactory.getLogger(LyfeifeiController.class);

    @GetMapping("/publish")
    public String publish() {

        logger.error("异常日志收集");
        logger.error("异常日志收集");
        logger.error("异常日志收集");
        logger.error("异常日志收集");
        logger.error("异常日志收集");
        logger.error("异常日志收集");

        return null;
    }
}
