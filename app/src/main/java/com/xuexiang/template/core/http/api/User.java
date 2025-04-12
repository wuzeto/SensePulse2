package com.xuexiang.template.core.http.api;

import static com.xuexiang.xutil.XUtil.runOnUiThread;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class User {


    private void sendRequestWithHttpURLConnection(){
        //开启线程发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("https://www.baidu.com");
                    connection =(HttpURLConnection)url.openConnection();
                    //通过此方法创建的HttpURLConnection对象，并没有真正执行连接操作，只是创建了一个新的实例，在正式连接前，往往还需要设置一些属性，如连接超时和请求方式等
                    connection.setRequestMethod("GET"); //设置请求方式
                    connection.setConnectTimeout(8000);//设置连接超时时间
                    connection.setReadTimeout(8000);//设置读取超时时间

                    connection.connect();//连接远程资源
                    Log.d("httpActivity","服务器的响应代码==》"+connection.getResponseCode());//可查看响应代码是否为200，是则请求成功，否则请求失败

                    InputStream input = connection.getInputStream();//获取到服务器返回的响应流
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));//构建BufferedReader用于读取响应流
                    StringBuilder sb = new StringBuilder();//构建StringBuilder对象，用来接收BufferedReader中的数据
                    String line = null;
                    while ((line=reader.readLine())!=null){
                        sb.append(line);
                    }
                    showResponse(sb.toString()); //调用showResponse方法，将StringBuilder中的内容显示到Activity中
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    connection.disconnect(); //关闭该http
                }
            }
        }).start();
    }

    private void showResponse(String sb){
        runOnUiThread(new Runnable() {  //Android不允许在子线程中更新ui，这里使用异步消息处理机制中的工作原理更新ui
            @Override
            public void run() {
//                responseText.setText(sb);
            }
        });
    }
}
