package org.tfoweaver.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.tfoweaver.api.Callback;
import org.tfoweaver.redis.RedisDsLockTemplate;

import java.util.Objects;

/**
 * @Description:
 * @title: TestController
 * @Author Star_Chen
 * @Date: 2022/7/14 8:46
 * @Version 1.0
 */
@RestController
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    RedisDsLockTemplate redisDsLockTemplate;

    @Autowired
    private TestDao testDao;

    @GetMapping("/get/{name}")
    public String get(@PathVariable("name") String name) {

        redisDsLockTemplate.execute("11111", 500, new Callback() {
            @Override
            public Object onGetLock() throws InterruptedException {

                redisDsLockTemplate.execute("11111", 500, new Callback() {
                    @Override
                    public Object onGetLock() throws InterruptedException {
                        Users user = testDao.getUserById(name);
                        if (Objects.isNull(user)) {
                            Users users = new Users();
                            users.setName(name);
                            users.setAddress("nicaicai");
                            users.setOrderId(11);
                            testDao.insert(users);
                            log.info("current thread :{}, execute getLock....", Thread.currentThread().getName());
                        }
                        return null;
                    }

                    @Override
                    public Object onTimeout() throws InterruptedException {
                        return null;
                    }
                });
                Thread.sleep(1000);
                Users user = testDao.getUserById(name);
                if (Objects.isNull(user)) {
                    Users users = new Users();
                    users.setName(name);
                    users.setAddress("nicaicai");
                    users.setOrderId(11);
                    testDao.insert(users);
                    log.info("current thread :{}, execute getLock....", Thread.currentThread().getName());
                }
                return null;
            }

            @Override
            public Object onTimeout() throws InterruptedException {
                log.info("current thread :{}, execute timeout....", Thread.currentThread().getName());
                return null;
            }
        });

        return "get";
    }

    @GetMapping("/gets/{name}")
    public String gets(@PathVariable("name") String name) {


        Users user = testDao.getUserById(name);
        if (Objects.isNull(user)) {
            Users users = new Users();
            users.setName(name);
            users.setAddress("nicaicai");
            users.setOrderId(11);
            testDao.insert(users);
            log.info("current thread :{}, execute getLock....", Thread.currentThread().getName());
        }

        return "get";
    }

}
