package org.xinhua.cbcloud.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DataController {

    private Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping("/dataInfo")
    public ModelAndView dataInfo(String docId) {
        return new ModelAndView("redirect:/index.jsp");
    }
}
