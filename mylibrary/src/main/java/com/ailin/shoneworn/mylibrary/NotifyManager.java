package com.ailin.shoneworn.mylibrary;

import android.content.Context;

import java.util.Observable;
/**
 * Created by shoneworn on 2017/11/15.
 * 被观察者管理器——用来对观察者发送消息
 */

public class NotifyManager extends Observable {

    private static Context mcontext;

    /**
     * 被观察的事件类型
     */
    public static final int TYPE_MAIN = -1;// 其他未知

    private static class NotifyManagerHolder{
        private static NotifyManager mNotifyManager = new NotifyManager();
    }

    /**
     * 获取通知管理器
     *
     * @return
     */
    public static NotifyManager getNotifyManager() {
        return NotifyManagerHolder.mNotifyManager;
    }
//    private static NotifyManager mNotifyManager;

//    /**
//     * 获取通知管理器
//     *
//     * @return
//     */
//    public static NotifyManager getNotifyManager() {
//        if (mNotifyManager == null) {
//            mNotifyManager = new NotifyManager();
//        }
//        return mNotifyManager;
//    }

    private NotifyManager() {

    }

    /**
     * 事件发生后通知监听者
     *
     * @param code :事件代码号 仅通知
     */
    public void notifyChange(int code) {
        NotifyMsgEntity msgEntity = new NotifyMsgEntity();
        msgEntity.setCode(code);
        notifyChange(msgEntity);
    }

    /**
     * 事件发生后通知监听者
     *
     * @param msgEntity 需要发送的消息数据
     */
    public void notifyChange(final NotifyMsgEntity msgEntity) {
        NotifyUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                setChanged();
                notifyObservers(msgEntity);
            }
        });
    }

}