package com.dating.google_billing;

import androidx.annotation.NonNull;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;

import java.util.List;

import static com.dating.sdklib.util.LogUtilKt.log;

/**
 * 作者:天镜baobao
 * 时间:2019/6/2  13:51
 * 说明:允许使用，但请遵循Apache License 2.0
 * 使用：
 * Copyright 2019 天镜baobao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class OnGoogleBillingListener {

    public static final String TAG = GoogleBillingUtil.TAG;

    /**
     * 查询成功
     * @param skuType 内购或者订阅
     * @param list 商品列表
     */
    public void onQuerySuccess(@NonNull String skuType, @NonNull List<SkuDetails> list){

    }

    /**
     * 购买成功
     * @param purchase 商品
     * @param isSelf 是否是当前页面的结果
     *
     * @return 是否消耗，只有当isSelf为true,并且支付状态为{@link Purchase.PurchaseState.PURCHASED}时，该值才会生效。
     */
    public boolean onPurchaseSuccess(@NonNull Purchase purchase){return true;}

    /**
     * 初始化成功
     * @param isSelf 是否是当前页面的结果
     */
    public void onSetupSuccess(){
        log(TAG, "内购服务初始化完成");
    }

    /**
     * 每次启动重新检查订单，返回有效的订单
     *
     * @param skuType 内购或者订阅
     * @param purchase    商品
     * @param isSelf  是否是当前页面的结果
     *
     * @return 是否自动消耗，只有当isSelf为true,并且支付状态为{@link Purchase.PurchaseState.PURCHASED}时，该值才会生效。
     */
    public boolean onRecheck(@NonNull String skuType, @NonNull Purchase purchase) {
        return false;
    }

    /**
     * 链接断开
     */
    @SuppressWarnings("WeakerAccess")
    public void onBillingServiceDisconnected(){ }

    /**
     * 消耗成功
     * @param purchaseToken token
     * @param isSelf 是否是当前页面的结果
     */
    public void onConsumeSuccess(@NonNull String purchaseToken){}


    /**
     * 确认购买成功
     * @param isSelf 是否是当前页面的结果
     */
    public void onAcknowledgePurchaseSuccess(){}

    /**
     * 失败回调
     * @param responseCode 返回码{https://developer.android.com/google/play/billing/billing_reference}
     * @param isSelf 是否是当前页面的结果
     * @param purchaseToken 当tag是consume时有效
     */
    public void onFail(@NonNull GoogleBillingUtil.GoogleBillingListenerTag tag, int responseCode, String purchaseToken){}

    /**
     * google组件初始化失败等等。
     * @param isSelf 是否是当前页面的结果
     */
    public void onError(@NonNull GoogleBillingUtil.GoogleBillingListenerTag tag){}

}
