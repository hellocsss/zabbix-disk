package com.zabbix.disk.Test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@SpringBootTest
@RunWith(SpringRunner.class)

public class socketTest {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket= new ServerSocket(22);
        Socket accept = serverSocket.accept();
        accept.connect(new InetSocketAddress(502));
       // accept.getInetAddress().getHostFromNameService("qw",true);

        //clienttest clienttest = new clienttest();
        Socket socket = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        //客户端socket指定服务器的地址和端口号
        socket = new Socket("10.1.192.182", 5025);
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream())));
        String sendinfo ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>…";
        byte bb[] = sendinfo.getBytes("UTF-8");

       //  String  sb= clienttest.getHeadStr(bb.length); //红色方法为实现
       String  sb= getHeadStr(bb.length); //红色方法为实现

        //先告诉服务端即将要发送的字符长度，20位
        pw.print(sb.toString());
        //然后发送xml
        pw.print(sendinfo);
        pw.flush();
        //接收服务端返回的xml
        String str = br.readLine();


    }
    public static String getHeadStr(int len) {
        String strLen = String.valueOf(len);
        String strRet = strLen;
        for (int i =0;i<20 - strLen.length();i++) {
            strRet += '\0' ;
        }
        return strRet;
    }
/*
     public static void main(String[] args) throws Exception {
        String xml = "<xml>\r\n" +
                        "<name>张山</name>\r\n" +
                        "<amt>100000</amt>\r\n" +
                        "<time>20171011091230</time>\r\n" +
                        "<type>支出</type>\r\n" +
                        "<opt>信用卡还款</opt>\r\n" +
                        "<phone>18940916007</phone>\r\n" +
                        "</xml>";
        Socket client = new Socket("127.0.0.1", 3456);
        OutputStream out = client.getOutputStream();

        byte[] b = xml.getBytes("UTF-8");

      //  out.write(int2Bytes8(b.length));
        out.write(b);
        out.close();
        client.close();

    }*/
  //https://www.cnblogs.com/startnow/p/7676521.html
    /**
     * @Title: int2Bytes8
     * @Description: 数字[2] 变成八个字节的 ['0' '0' '0' '0' '0' '0' '0' '2']
     * @param: @param num
     * @param: @return
     * @return: byte[]
     */
    /**  public static byte[] int2Bytes8(int num) {
        StringBuffer sb = new StringBuffer(String.valueOf(num));
        int length = 8 - sb.length();
        for (int i = 0; i < length; i++) {
            sb.insert(0, '0');
        }
        return sb.toString().getBytes();
    }*/

}
