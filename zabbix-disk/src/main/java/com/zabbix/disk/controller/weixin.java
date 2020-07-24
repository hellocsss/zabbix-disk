package com.zabbix.disk.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.zabbix.disk.config.ClientRequest;
import com.zabbix.disk.entity.weixinmessage.Template;
import com.zabbix.disk.entity.weixinmessage.TemplateMessage;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Controller
public class weixin {

    //开发者提供
    private static String appid="appid";
    @Resource
    private AccessRequest accessRequest;

    //开发者提供
    private static String secret ="secret";
    /*获取access_token*/
    /*https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET*/
    @RequestMapping("/weixin")
    public void Message() throws IOException {
        String url="https://api.weixin.qq.com/cgi-bin/token?" +
                "grant_type=client_credential&appid=APPID&secret=APPSECRET";
        //1.就可以获取access_token
 /*       String result1= HttpUtil.get(url);*/





        String access_token2 = accessRequest.AccessToken();

        /*这是获取到的access_token  access_token2 -> access_token 修改*/
        String access_token ="access_token";

        //2.然后在进行post发送模板消息
        //开发者提供
        String template_id="template_id";
        //开发者提供  用户id 想那个用户发送消息
        String touser="open_id";
        /*这里的date需要和模板的数据对应*/
        Template template=new Template();
        template.setTemplate_id(template_id);
        template.setTouser(touser);

        List<TemplateMessage> messages=new ArrayList<>();
        TemplateMessage templateMessage=new TemplateMessage();
        templateMessage.setName("nihao ");
        templateMessage.setValue("wode ");
        templateMessage.setColor("hongse");
        messages.add(templateMessage);
        template.setData(messages);

        String s = JSONUtil.toJsonStr(template);

        /*然后在发送post请求*/
        String urlpost="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+access_token;
        ClientRequest.sendPostJsonStr(urlpost,s);

    }





}
