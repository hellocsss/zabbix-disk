import com.zabbix.disk.ZabbixDevApplication;
import com.zabbix.disk.config.ZabbixApiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * <p>标题: </p>
 * <p>描述: </p>
 * <p>版权: Copyright (c) 2020</p>
 * <p>公司: 智业软件股份有限公司</p>
 *
 * @version: 1.0
 * @author: jwh
 * @date 2020/6/2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ZabbixDevApplication.class)
@SpringBootConfiguration
public class TestZabbix {

    @Resource
    private ZabbixApiService zabbixApiService;

    @Test
    @ResponseBody
    public void testZabbix() throws Exception {
        String auth = zabbixApiService.loginZabbix();
        System.out.println(auth+"这是什么");





    }
}
