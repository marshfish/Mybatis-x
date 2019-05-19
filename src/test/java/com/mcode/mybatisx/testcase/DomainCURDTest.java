package com.mcode.mybatisx.testcase;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;
import com.mcode.mybatisx.MybatisXApplication;
import com.mcode.mybatisx.dal.entity.PagingDO;
import com.mcode.mybatisx.demo.User;
import com.mcode.mybatisx.demo.UserDao;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MybatisXApplication.class)
public class DomainCURDTest {
    @Resource
    private UserDao userDao;
    private static List<User> users;

    @Before
    public void setUsers() {
        //初始化测试数据
        Random random = new Random();
        AtomicLong count = new AtomicLong(1);
        AtomicLong num = new AtomicLong(1);
        users = Stream.generate(() -> {
            int ok = random.nextInt(10000);
            User user = new User();
            user.setAddress("莆田酒店");
            user.setAge(ok);
            user.setCreateTime(num.getAndAdd(2));
            user.setUpdateTime(System.currentTimeMillis());
            user.setEmail("hisdfas@163.com");
            user.setHobby("测试--" + count.getAndIncrement());
            user.setName("笑然白");
            user.setSex(1);
            user.setTime(System.currentTimeMillis());
            return user;
        }).limit(20).collect(Collectors.toList());
    }

    @Test
    @Ignore
    @Transactional
    @Rollback(false)
    public void initIt() {
        //建表
        userDao.executeSql(() -> "DROP TABLE IF EXISTS `sys_user`; \n");
        userDao.executeSql(() -> "  CREATE TABLE IF NOT EXISTS `sys_user` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(255) DEFAULT NULL,\n" +
                "  `age` int(11) DEFAULT NULL,\n" +
                "  `email` varchar(255) DEFAULT NULL,\n" +
                "  `time` bigint(20) DEFAULT NULL,\n" +
                "  `address` varchar(255) DEFAULT NULL,\n" +
                "  `hobby` varchar(255) DEFAULT NULL,\n" +
                "  `sex` int(255) DEFAULT NULL,\n" +
                "  `create_time` bigint(20) DEFAULT NULL,\n" +
                "  `update_time` bigint(20) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=608 DEFAULT CHARSET=utf8;");
        //重置自增
        userDao.executeSql(() -> "TRUNCATE TABLE `sys_user`");

        //保存测试数据
        int row = userDao.insertBatch(() -> users);
        Assert.isTrue(row == users.size());
        Assert.isTrue(users.size() == users.stream().map(User::getId).filter(Objects::nonNull).count());
    }

    @Test
    @Transactional
    @Rollback
    public void count() {
        User user1 = new User();
        user1.setName("笑然白");
        int count = userDao.count(user1);
        Assert.isTrue(count == users.size());
    }

    @Test
    @Transactional
    @Rollback
    public void countSupplier() {
        int count = userDao.count(user -> user.setName("笑然白"));
        Assert.isTrue(count == users.size());
    }


    @Test
    @Transactional
    @Rollback
    public void insertOne() {
        User user1 = new User();
        BeanUtil.copyProperties(users.get(0), user1);
        int insert = userDao.insert(user1);
        Assert.isTrue(insert == 1);

        int count = userDao.count(user -> user.setName("笑然白"));
        Assert.isTrue(count == users.size() + 1);
    }

    @Test
    @Transactional
    @Rollback
    public void insertOneSupplier() {
        int insert = userDao.insert(user -> BeanUtil.copyProperties(users.get(0), user));
        Assert.isTrue(insert == 1);

        int count = userDao.count(user -> user.setName("笑然白"));
        Assert.isTrue(count == users.size() + 1);
    }

    @Test
    @Transactional
    @Rollback
    public void insertBatch() {
        int row = userDao.insertBatch(users);
        Assert.isTrue(row == users.size());
        Assert.isTrue(users.size() == users.stream().map(User::getId).filter(Objects::nonNull).count());
    }

    @Test
    @Transactional
    @Rollback
    public void insertBatchSupplier() {
        int row = userDao.insertBatch(() -> users);
        Assert.isTrue(row == users.size());
        Assert.isTrue(users.size() == users.stream().map(User::getId).filter(Objects::nonNull).count());
    }

    @Test
    @Transactional
    @Rollback
    public void updateOne() {
        int next = new Random().nextInt(100000);
        //查询
        User user = userDao.selectOne(user1 -> user1.setName("笑然白"));
        user.setSex(next);
        //更新
        int update = userDao.update(user.getId(), user);
        Assert.isTrue(update == 1);
        //比较
        User user1 = userDao.selectById(user.getId());
        Assert.isTrue(user1.getSex() == next);
    }

    @Test
    @Transactional
    @Rollback
    public void updateOneSupplier() {
        int next = new Random().nextInt(100000);
        //查询
        User user = userDao.selectOne(user1 -> user1.setName("笑然白"));
        //更新
        int update = userDao.update(user.getId(), user12 -> user12.setSex(next));
        Assert.isTrue(update == 1);
        //比较
        User user1 = userDao.selectById(user.getId());
        Assert.isTrue(user1.getSex() == next);
    }

    @Test
    @Transactional
    @Rollback
    public void updateBatch() {
        int next = new Random().nextInt(100000);
        //查询
        List<User> user = userDao.select(user1 -> user1.setName("笑然白"));
        List<Long> ids = user.stream().map(User::getId).limit(5).collect(Collectors.toList());
        //更新
        User user2 = new User();
        user2.setSex(next);
        int update = userDao.updateBatch(ids, user2);
        Assert.isTrue(update == 5);
        //比较
        userDao.selectByIds(ids).stream().map(User::getSex).forEach(sex ->
                Assert.isTrue(sex == next)
        );
    }

    @Test
    @Transactional
    @Rollback
    public void updateBatchSupplier() {
        int next = new Random().nextInt(100000);
        //查询
        List<User> user = userDao.select(user1 -> user1.setName("笑然白"));
        List<Long> ids = user.stream().map(User::getId).limit(5).collect(Collectors.toList());
        //更新
        int update = userDao.updateBatch(ids, user2 -> user2.setSex(next));
        Assert.isTrue(update == 5);
        //比较
        userDao.selectByIds(ids).stream().map(User::getSex).forEach(sex ->
                Assert.isTrue(sex == next)
        );
    }

    @Test
    @Transactional
    @Rollback
    public void delete() {
        User user = userDao.selectOne(user1 -> user1.setName("笑然白"));
        User user2 = new User();
        user2.setId(user.getId());
        int i = userDao.delete(user2);
        Assert.isTrue(i == 1);
        User user1 = userDao.selectById(user.getId());
        Assert.isTrue(user1 == null);
    }

    @Test
    @Transactional
    @Rollback
    public void deleteById() {
        User user = userDao.selectOne(user1 -> user1.setName("笑然白"));
        int i = userDao.deleteById(user.getId());
        Assert.isTrue(i == 1);
        User user1 = userDao.selectById(user.getId());
        Assert.isTrue(user1 == null);
    }

    @Test
    @Transactional
    @Rollback
    public void deleteByIds() {
        List<User> user = userDao.select(user1 -> user1.setName("笑然白"));
        List<Long> ids = user.stream().map(User::getId).limit(5).collect(Collectors.toList());

        int i = userDao.deleteByIds(ids);
        Assert.isTrue(i == ids.size());

        List<User> users = userDao.selectByIds(ids);
        Assert.isTrue(users.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void select() {
        List<User> select = userDao.select(new User());
        Assert.isTrue(select.size() == users.size());
    }

    @Test
    @Transactional
    @Rollback
    public void selectSupplier() {
        List<User> select = userDao.select(user -> {
        });
        Assert.isTrue(select.size() == users.size());
    }

    @Test
    @Transactional
    @Rollback
    public void selectOne() {
        User user1 = new User();
        user1.setName("笑然白");
        User select = userDao.selectOne(user1);
        Assert.isTrue(select != null);
    }


    @Test
    @Transactional
    @Rollback
    public void selectOneSupplier() {
        User select = userDao.selectOne(user -> user.setName("笑然白"));
        Assert.isTrue(select != null);
    }

    @Test
    @Transactional
    @Rollback
    public void selectByIds() {
        List<User> user = userDao.select(user1 -> user1.setName("笑然白").setOrder("create_time desc"));
        List<Long> ids = user.stream().map(User::getId).limit(5).collect(Collectors.toList());
        List<User> users = userDao.selectByIds(ids);
        Assert.isTrue(users.size() == ids.size());
    }

    @Test
    @Transactional
    @Rollback
    public void selectById() {
        User user2 = userDao.selectOne(user1 -> user1.setName("笑然白"));

        User user = userDao.selectById(user2.getId());
        Assert.isTrue(user != null);
    }

    @Test
    @Transactional
    @Rollback
    public void selectByPaging() {
        PagingDO<User> pagingDO = userDao.selectPaging(new User());
        Assert.isTrue(pagingDO.getTotal() == users.size());
        Assert.isTrue(pagingDO.getCurrentPage() == 1);
        Assert.isTrue(pagingDO.getPageTotal() == 2);
        Assert.isTrue(pagingDO.getTotal() == 20);
        Assert.isTrue(pagingDO.getList().size() == 10);
    }

    @Test
    @Transactional
    @Rollback
    public void selectByPagingSupplier() {
        PagingDO<User> pagingDO = userDao.selectPaging(user -> {
        });
        Assert.isTrue(pagingDO.getTotal() == users.size());
        Assert.isTrue(pagingDO.getCurrentPage() == 1);
        Assert.isTrue(pagingDO.getPageTotal() == 2);
        Assert.isTrue(pagingDO.getTotal() == 20);
        Assert.isTrue(pagingDO.getList().size() == 10);
    }

    @Test
    @Transactional
    @Rollback
    public void selectIn() {
        List<Object> integers = Lists.newArrayList(1, 3, 5, 7, 9);
        List<User> users = userDao.selectIn("create_time", integers);
        Assert.isTrue(users.size() == integers.size());
    }
}
