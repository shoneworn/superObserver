package com.ailin.shoneworn.observer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.ailin.shoneworn.mylibrary.NotifyManager;
import com.ailin.shoneworn.mylibrary.NotifyMsgEntity;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends Activity implements Observer {
//在需要的地方去实现observer接口。
        ImageView img ;
    private Button btn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //这里添加观察者 表示，我时刻关注着你。你有啥事，通知我一声。
        NotifyManager.getNotifyManager().addObserver(this);
//        startThread();
        img = (ImageView)findViewById(R.id.img);

        btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img.setImageBitmap(Utils.takeScreenShot(MainActivity.this));
            }
        });
    }

    //这里模拟在其他界面或者子线程中的被观察者。被观察者这个时候开始，要发消息了。code 为TYPE_MAIN ，可以自定义。往下看
    private void startThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NotifyMsgEntity msgEntity = new NotifyMsgEntity();
                msgEntity.setCode(NotifyManager.TYPE_MAIN);
                msgEntity.setData(1);
                NotifyManager.getNotifyManager().notifyChange(msgEntity);
            }
        }).start();
    }

    //observer的方法，用于处理被观察者发来的通知。
    @Override
    public void update(Observable o, Object data) {

        if (data == null || !(data instanceof NotifyMsgEntity)) {
            return;
        }
        NotifyMsgEntity entity= (NotifyMsgEntity) data;
        int code = (int)entity.getData();
        int type =entity.getCode();
        if(NotifyManager.TYPE_MAIN==type){
            Toast.makeText(this, "code="+code, Toast.LENGTH_SHORT).show();
        }
    }


}