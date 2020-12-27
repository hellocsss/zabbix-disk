package com.zabbix.disk.Test;

import com.zabbix.disk.enums.TradeChannelEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest

public class EumnTest {





    @Test
    public void getHello() throws Exception {
        TradeChannelEnum tradeChannelEnum = TradeChannelEnum.ALL_ENTRIES.get("20");

        String value = tradeChannelEnum.getValue();

        System.out.println(value);
    }

}
