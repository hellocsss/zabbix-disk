package com.zabbix.disk.Test;

import java.io.*;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class SocketClient {
    private static Logger logger = Logger.getLogger(SocketClient.class
            .getName());
    private static final int MAX_TIMEOUT = 10;

    private SocketClient() {
    }

    /**
     * 向服务端发送消息
     *
     * @param host
     *            主机Host或IP
     * @param port
     *            端口
     * @param timeout
     *            超时,单位秒
     * @param content
     *            发送内容
     */
    public static void send(String host, int port, int timeout, String content) {
        Socket s = null;
        PrintWriter out = null;
        try {
            s = new Socket(host, port);
            s
                    .setSoTimeout((timeout > MAX_TIMEOUT ? MAX_TIMEOUT
                            : timeout) * 1000);
            out = new PrintWriter(s.getOutputStream());
            out.write(content);
            out.flush();
        } catch (Exception e) {
          //  logger.error(e);
        } finally {
            if (s != null){


                try {
                    s.close();
                } catch (IOException e) {
                }   }


            if (out != null)  {

                out.close();
            s = null;
            out = null;    }
        }
    }

    /**
     * 向SocketServer发送通信指令并获取回复数据
     *
     * @param host 主机名称或IP
     * @param port 端口
     * @param timeout 超时时间(秒)
     * @param content 指令内容
     * @return
     */
    public static String sendAndGetReply(String host, int port, int timeout,
                                         String content) {
        String encode = "utf-8";
        Socket s = null;
        BufferedReader in = null;
        PrintWriter out = null;
        String line = null;
        try {
            content = URLEncoder.encode(content, encode);
            s = new Socket(host, port);
            s
                    .setSoTimeout((timeout > MAX_TIMEOUT ? MAX_TIMEOUT
                            : timeout) * 1000);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s
                    .getOutputStream())), true);
            out.println(content);
            line = in.readLine();
        } catch (Exception e) {
          //  logger.error(e);
        } finally {
            if (s != null){
                try {
                    s.close();
                } catch (IOException e) {
                }}
            if (out != null){
                out.close();}
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                }}
            s = null;
            out = null;
            in = null;
        }
        try {
            line = URLEncoder.encode(line, encode);
        } catch (UnsupportedEncodingException e) {
          //  logger.error(e);
        }
        return line;
    }

    /**
     * 向SocketServer发送通信指令,无同步回复消息
     *
     * @param host 主机名称或IP
     * @param port 端口
     * @param timeout 超时时间(秒)
     * @param content 指令内容
     * @return
     */
    public static void sendAndNoReply(String host, int port, int timeout,
                                      String content) {
        String encode = "utf-8";
        Socket s = null;
        PrintWriter out = null;
        try {
            content = URLEncoder.encode(content, encode);
            s = new Socket(host, port);
            s
                    .setSoTimeout((timeout > MAX_TIMEOUT ? MAX_TIMEOUT
                            : timeout) * 1000);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s
                    .getOutputStream())), true);
            out.println(content);
        } catch (Exception e) {
            //logger.error(e);
        } finally {
            if (s != null){
                try {
                    s.close();
                } catch (IOException e) {
                }}
            if (out != null){
                out.close();
            s = null;
            out = null;}
        }
    }
}
