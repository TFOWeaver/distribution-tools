## distribution-tools

分布式服务下的开源组件，其中包括http接口限流，服务接口限流，分布式锁（redis+zk），服务治理等组件



项目结构：

distribution-tools

|——distribution-tools-lock 分布式锁组件

## 使用

### maven: 需先编译安装到本地仓库或者本地私服。

```xml
<dependency>
    <groupId>org.tfoweaver</groupId>
    <artifactId>distribution-tools-lock</artifactId>
</dependency>
```



基于Redis Lua脚本实现可重入分布式锁
使用案例：
```java
@Autowired
private RedisDsLockTemplate redisDsLockTemplate;


@GetMapping("/get/{name}")
public String get(@PathVariable("name") String name) {

    //分布式锁的id，可以是订单id（唯一），获取锁超时时间500ms
    redisDsLockTemplate.execute("11111", 500, new Callback() {
        @Override
        public Object onGetLock() throws InterruptedException {
			//获取锁后要处理的业务逻辑
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
            return null;
        }
	//超时处理业务逻辑
        @Override
        public Object onTimeout() throws InterruptedException {
            log.info("current thread :{}, execute timeout....", Thread.currentThread().getName());
            return null;
        }
    });

    return "get";
}
```

技术有问题请咨询QQ：904391301

