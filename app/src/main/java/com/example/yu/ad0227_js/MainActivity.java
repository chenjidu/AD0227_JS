package com.example.yu.ad0227_js;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button mbtn_use_js_null;
    private Button mbtn_use_js_cs;
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mbtn_use_js_null = (Button) findViewById(R.id.mbtn_use_js_null);
        mbtn_use_js_cs = (Button) findViewById(R.id.mbtn_use_js_cs);
        mWebView = (WebView) findViewById(R.id.mWebView);

        //允许WebView加载内容。
        mWebView.getSettings().setJavaScriptEnabled(true);
        //加载内容的地址信息(这里本人asset多加了个s 会保存)
        mWebView.loadUrl("file:///android_asset/web.html");
        //设置连接的接口
        mWebView.addJavascriptInterface(MainActivity.this,"android");


        /**
         * 个人发现:android调用javascript就是在<script></script>里面写function函数。
         * function函数中无参数的时候   换行符<br\> 和  内容    都在引号里面
         * function函数中有参数的时候   换行符<br\>在引号里面  参数就是变量名    它们需要一个括号括起来表示一个整体。
         */
        mbtn_use_js_null.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里安卓调用无参数的javascript方法。
                mWebView.loadUrl("javascript:javacalljs()");
            }
        });

        mbtn_use_js_cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里安卓调用有参数的javascript方法。参数需要单引号。这个参数的宽度大于屏幕的宽度可以自动左右滑动
                mWebView.loadUrl("javascript:javacalljswith(" + "'http://blog.csdn.net/Leejizhou'" +")");
                //假如传过去的参数没有单引号是会出现问题的。就是参数会传不过去。而且斜杠是右斜杠。
                //而且那边的参数是不会有类型的。细节。
            }
        });

    }


    /**
     * 细节:javascript中调用java代码都是需要声明 @JavascriptInterface而且更新UI的时候需要在主线程更新。
     *      同时发行在javascript中传送到java代码的参数是有类型的。而按钮点击事件中传送到javascript中的参数在<script></script>中是没有类型说明的。
     *
     */
    @JavascriptInterface
    public void startFunction(){
        Toast.makeText(MainActivity.this,"js调用java代码",Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public void startFunction(final String text){
        //细节:要在主线程更新UI.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this).setMessage(text).show();
            }
        });
    }
    @JavascriptInterface
    public void call(String num){
        if(num.isEmpty()){
            Toast.makeText(this,"phone_number_is_not_null",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + num));//Data：Data通常是URI格式定义的操作数据。例如：tel:// 。通过setData()方法设置。
        startActivity(intent);                  //在intent-filter中指定data属性的实际目的是：要求接收的Intent中的data必须符合intent-filter中指定的data属性，这样达到反向限定Intent的作用。
    }
    @JavascriptInterface
    public void sendMsg(String num,String msgContent){
        if(num.isEmpty() || msgContent.isEmpty()){
            Toast.makeText(this,"num_or_msgContent_is_not_null",Toast.LENGTH_SHORT).show();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> list = smsManager.divideMessage(msgContent);

        for(String text : list){
            smsManager.sendTextMessage(num,null,text,null,null);
        }
        Toast.makeText(this,"send_message_success",Toast.LENGTH_SHORT).show();
    }

//    (1)SmsManager manager = SmsManager.getDefault();   //获得默认的消息管理器
//    (2)ArrayList<String> list = manager.divideMessage(String txt);  //拆分长短信
//    (3)manager.sendTextMessage(String phone,null,String content,null,null);  //发送短信



}
