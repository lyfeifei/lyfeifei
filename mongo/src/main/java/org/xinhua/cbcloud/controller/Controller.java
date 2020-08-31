package org.xinhua.cbcloud.controller;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xinhua.cbcloud.pojo.User;

import java.util.List;

@RestController
public class Controller {

    private Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private MongoTemplate mongoTemplate;

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

    @GetMapping("/dataInfo")
    public List<JSONObject> dataInfo() {

        return null;
    }
}
