package org.xinhua.cbcloud.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;
import org.xinhua.cbcloud.pojo.User;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MongoTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testAdd() {
        User user = new User();
        user.setName("admin");
        user.setAddress("bj");
        mongoTemplate.save(user, "user");
    }

    @Test
    public void testFindOne() {
        Query query = new Query();
        Criteria criteria = Criteria.where("name").is("admin");
        criteria.and("address").is("bj");
        query.addCriteria(criteria);
        User user = mongoTemplate.findOne(query, User.class,"user");
        System.out.println(user);
    }
}
