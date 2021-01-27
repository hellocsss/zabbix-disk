package com.zabbix.disk.Test;

import com.zabbix.disk.enums.TradeChannelEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.util.ArrayUtils;
import sun.security.util.ArrayUtil;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class EumnTest {
    private final Logger logger=LoggerFactory.getLogger(EumnTest.class);




    @Test
    public void getHello() throws Exception {
        TradeChannelEnum tradeChannelEnum = TradeChannelEnum.ALL_ENTRIES.get("20");

        String value = tradeChannelEnum.getValue();


        System.out.println(value);
    }

}
