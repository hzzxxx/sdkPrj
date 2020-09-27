package com.dating.im_library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient

class ChatActivity : AppCompatActivity() {
    private lateinit var activity:AppCompatActivity;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        activity=this

        // 初始化. 建议在 Application 中进行初始化.
        var appKey ="x18ywvqfx59lc"// "x4vkb1qpxgjhk"
        RongIM.init(activity, appKey)


        var token = "用户Token";
        RongIM.connect(token, object : RongIMClient.ConnectCallback() {
            override fun onSuccess(p0: String?) {
                //连接成功
            }

            override fun onDatabaseOpened(code: RongIMClient.DatabaseOpenStatus?) {
                //消息数据库打开，可以进入到主页面
            }

            override fun onError(errorCode: RongIMClient.ConnectionErrorCode?) {
                if(errorCode?.equals(RongIMClient.ConnectionErrorCode.RC_CONN_TOKEN_INCORRECT)!!) {
                    //从 APP 服务获取新 token，并重连
                } else {
                    //无法连接 IM 服务器，请根据相应的错误码作出对应处理
                }
            }

        })
    }
}