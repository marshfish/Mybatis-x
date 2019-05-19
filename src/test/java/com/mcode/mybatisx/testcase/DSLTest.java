package com.mcode.mybatisx.testcase;

import cn.hutool.core.lang.Assert;
import com.mcode.mybatisx.demo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DSLTest {
    @Test
    @Transactional
    @Rollback
    public void test1() {
    }
}
