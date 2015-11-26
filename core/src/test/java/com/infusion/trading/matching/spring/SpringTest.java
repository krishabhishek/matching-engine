package com.infusion.trading.matching.spring;

import com.infusion.trading.matching.test.common.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
/**
 * Created by pchurchward on 2015-11-26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*Cucumber.xml")
public class SpringTest{

    @Autowired
    private TestHelper testHelper;

    @Test
    public void test() {
        assertNotNull(testHelper);
    }
}
