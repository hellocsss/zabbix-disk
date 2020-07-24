package com.zabbix.disk.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;

@Controller
public class file {

    @RequestMapping("/file")
    public void file(@RequestParam("filedd") MultipartFile files){

        System.out.println(files.getName());
        HashMap<String, Object> paramMap = new HashMap<>();


//文件上传只需将参数中的键指定（默认file），值设为文件对象即可，对于使用者来说，文件上传与普通表单提交并无区别
        paramMap.put("file",files);
        String result= HttpUtil.post("localhost:8898/files", paramMap);


    }
}
