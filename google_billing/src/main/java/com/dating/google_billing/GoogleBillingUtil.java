package com.dating.google_billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.dating.sdklib.util.LogUtilKt.log;

/**
 * 作者:天镜baobao
 * 时间:2019/1/5  15:16
 * 说明:允许使用，但请遵循Apache License 2.0
 * 使用：
 * CSDN:http://blog.csdn.net/u013640004/article/details/78257536
 * Copyright 2019 天镜baobao
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
public class GoogleBillingUtil {

    public static final String TAG = "GoogleBillingUtil--->";
    private static boolean IS_DEBUG = true;
    private static String[] inAppSKUS = new String[]{};//内购ID,必填，注意！如果用不着的请去掉多余的""
    private static String[] subsSKUS = new String[]{};//订阅ID,必填，注意！如果用不着的请去掉多余的""

    public static final String BILLING_TYPE_INAPP = BillingClient.SkuType.INAPP;//内购
    public static final String BILLING_TYPE_SUBS = BillingClient.SkuType.SUBS;//订阅

    private static BillingClient mBillingClient;
    private static BillingClient.Builder builder;
    private OnGoogleBillingListener onGoogleBillingListener;
    private MyPurchasesUpdatedListener purchasesUpdatedListener = new MyPurchasesUpdatedListener();

    private static boolean isAutoAcknowledgePurchase = true;

    private static final GoogleBillingUtil mGoogleBillingUtil = new GoogleBillingUtil();

    // 由于某种原因消耗不掉的商品，原因暂时不明
    private List<String> errorPurchaseSignature = new ArrayList<>();

    private GoogleBillingUtil() {

    }

    public boolean isConnect() {
        return mBillingClient != null && mBillingClient.isReady();
    }

    //region===================================初始化google应用内购买服务=================================

    /**
     * 设置skus
     *
     * @param inAppSKUS 内购id
     * @param subsSKUS  订阅id
     */
    public static void setSkus(@Nullable String[] inAppSKUS, @Nullable String[] subsSKUS) {
        if (inAppSKUS != null) {
            GoogleBillingUtil.inAppSKUS = Arrays.copyOf(inAppSKUS, inAppSKUS.length);
        }
        if (subsSKUS != null) {
            GoogleBillingUtil.subsSKUS = Arrays.copyOf(subsSKUS, subsSKUS.length);
        }
    }

    public void addErrorPurchase(String signature) {
        errorPurchaseSignature.add(signature);
    }

    private static <T> void copyToArray(T[] base, T[] target) {
        System.arraycopy(base, 0, target, 0, base.length);
    }

    public static GoogleBillingUtil getInstance() {
        return mGoogleBillingUtil;
    }

    /**
     * 开始建立内购连接
     *
     */
    public GoogleBillingUtil build(Context context) {
        if (mBillingClient == null) {
            synchronized (mGoogleBillingUtil) {
                if (mBillingClient == null) {
                    builder = BillingClient.newBuilder(context);
                    mBillingClient = builder.setListener(purchasesUpdatedListener)
                            .enablePendingPurchases()
                            .build();
                } else {
                    builder.setListener(purchasesUpdatedListener);
                }
            }
        } else {
            builder.setListener(purchasesUpdatedListener);
        }
        synchronized (mGoogleBillingUtil) {
            if (mGoogleBillingUtil.startConnection()) {
                mGoogleBillingUtil.queryInventoryInApp();
                mGoogleBillingUtil.queryInventorySubs();
                mGoogleBillingUtil.queryPurchasesInApp();
            }
        }
        return mGoogleBillingUtil;
    }

    private boolean startConnection() {
        if (mBillingClient == null) {
            loge("初始化失败:mBillingClient==null");
            return false;
        }
        if (!mBillingClient.isReady()) {
            loge("start----connect");
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    loge("--onBillingSetupFinished--");
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (onGoogleBillingListener != null) {
                            onGoogleBillingListener.onSetupSuccess();
                        }
                        queryInventoryInApp();
                        queryInventorySubs();
                        queryPurchasesInApp();
                    } else {
                        loge("初始化失败:onSetupFail:code=" + billingResult.getResponseCode() + ", msg = " + billingResult.getDebugMessage());
                        if (onGoogleBillingListener != null) {
                            onGoogleBillingListener.onFail(GoogleBillingListenerTag.SETUP, billingResult.getResponseCode(), null);
                        }
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    if (onGoogleBillingListener != null) {
                        onGoogleBillingListener.onBillingServiceDisconnected();
                    }
                    loge("初始化失败:onBillingServiceDisconnected");
                }
            });
            return false;
        } else {
            return true;
        }
    }

    //endregion

    //region===================================查询商品=================================

    /**
     * 查询内购商品信息
     */
    private void queryInventoryInApp() {
        queryInventory(BillingClient.SkuType.INAPP);
    }

    /**
     * 查询订阅商品信息
     */
    public void queryInventorySubs() {
        queryInventory(BillingClient.SkuType.SUBS);
    }

    private void queryInventory(final String skuType) {
        Runnable runnable = () -> {
            if (mBillingClient == null) {
                if (onGoogleBillingListener != null) {
                    onGoogleBillingListener.onError(GoogleBillingListenerTag.QUERY);
                }
                return;
            }
            ArrayList<String> skuList = new ArrayList<>();
            if (skuType.equals(BillingClient.SkuType.INAPP)) {
                Collections.addAll(skuList, inAppSKUS);
            } else if (skuType.equals(BillingClient.SkuType.SUBS)) {
                Collections.addAll(skuList, subsSKUS);
            }
            if (!skuList.isEmpty()) {
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(skuType);
                mBillingClient.querySkuDetailsAsync(params.build(), new MySkuDetailsResponseListener(skuType));
            }
        };
        executeServiceRequest(runnable);
    }

    //endregion

    //region===================================购买商品=================================

    /**
     * 发起内购
     *
     * @param skuId 内购商品id
     */
    public void purchaseInApp(Activity activity, String skuId) {
        purchase(activity, skuId, BillingClient.SkuType.INAPP);
    }

    /**
     * 发起订阅
     *
     * @param skuId 订阅商品id
     */
    public void purchaseSubs(Activity activity, String skuId) {
        purchase(activity, skuId, BillingClient.SkuType.SUBS);
    }

    private void purchase(Activity activity, final String skuId, final String skuType) {
        if (mBillingClient == null) {
            if (onGoogleBillingListener != null) {
                onGoogleBillingListener.onError(GoogleBillingListenerTag.PURCHASE);
            }
            return;
        }
        if (startConnection()) {
            builder.setListener(purchasesUpdatedListener);
            List<Purchase> purchases = queryPurchasesInApp();
            if (purchases == null || checkHasNoErrorPurchase(purchases)) {
                log(TAG, "未查到未消耗的商品。执行本次购买");
                List<String> skuList = new ArrayList<>();
                skuList.add(skuId);
                SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                        .setSkusList(skuList)
                        .setType(skuType)
                        .build();
                mBillingClient.querySkuDetailsAsync(skuDetailsParams, (responseCode, skuDetailsList) -> {
                    log(TAG, "querySkuDetailsAsync code = " + responseCode.getResponseCode());
                    if (skuDetailsList != null && !skuDetailsList.isEmpty()) {
                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetailsList.get(0))
                                .build();
                        mBillingClient.launchBillingFlow(activity, flowParams);
                    } else {
                        log(TAG, "skuDetailsList 为空");
                    }
                });
            }
        } else {
            if (onGoogleBillingListener != null) {
                onGoogleBillingListener.onError(GoogleBillingListenerTag.PURCHASE);
            }
        }
    }

    // 查询是否有错误的未处理订单，如果都是错误的订单，返回true
    private boolean checkHasNoErrorPurchase(List<Purchase> purchases) {
        for (int i = 0; i < purchases.size(); i++) {
            if (!errorPurchaseSignature.contains(purchases.get(i).getSignature())) {
                return false;
            }
        }
        return true;
    }


    //endregion

    //region===================================消耗商品=================================

    /**
     * 消耗商品
     *
     * @param purchaseToken {@link Purchase#getPurchaseToken()}
     */
    public void consumeAsync(String purchaseToken) {
        consumeAsync(purchaseToken, null);
    }

    private void consumeAsync(String purchaseToken, @Nullable String developerPayload) {
        if (mBillingClient == null) {
            return;
        }
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .setDeveloperPayload(developerPayload)
                .build();
        mBillingClient.consumeAsync(consumeParams, new MyConsumeResponseListener());
    }

    /**
     * 消耗内购商品-通过sku数组
     *
     * @param sku sku
     */
    public void consumeAsyncInApp(@NonNull String... sku) {
        if (mBillingClient == null) {
            return;
        }
        List<String> skuList = Arrays.asList(sku);
        consumeAsyncInApp(skuList, null);
    }

    /**
     * 消耗内购商品-通过sku数组
     *
     * @param skuList sku数组
     */
    public void consumeAsyncInApp(@NonNull List<String> skuList, @Nullable List<String> developerPayloadList) {
        if (mBillingClient == null) {
            return;
        }
        List<Purchase> purchaseList = queryPurchasesInApp();
        if (purchaseList != null) {
            for (Purchase purchase : purchaseList) {
                int index = skuList.indexOf(purchase.getSku());
                if (index != -1) {
                    if (developerPayloadList != null && index < developerPayloadList.size()) {
                        consumeAsync(purchase.getPurchaseToken(), developerPayloadList.get(index));
                    } else {
                        consumeAsync(purchase.getPurchaseToken(), null);
                    }
                }
            }
        }
    }

    //endregion

    //region===================================确认购买=================================

    /**
     * 确认购买
     *
     * @param purchaseToken token
     */
    public void acknowledgePurchase(String purchaseToken) {
        acknowledgePurchase(purchaseToken, null);
    }

    private void acknowledgePurchase(String purchaseToken, @Nullable String developerPayload) {
        if (mBillingClient == null) {
            return;
        }
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .setDeveloperPayload(developerPayload)
                .build();
        mBillingClient.acknowledgePurchase(params, new MyAcknowledgePurchaseResponseListener());
    }

    //endregion

    //region===================================本地订单查询=================================

    /**
     * 获取已经内购的商品
     *
     * @return 商品列表
     */
    public List<Purchase> queryPurchasesInApp() {
        return queryPurchases(BillingClient.SkuType.INAPP);
    }

    /**
     * 获取已经订阅的商品
     *
     * @return 商品列表
     */
    public List<Purchase> queryPurchasesSubs() {
        return queryPurchases(BillingClient.SkuType.SUBS);
    }

    private List<Purchase> queryPurchases(String skuType) {
        if (mBillingClient == null) {
            return null;
        }
        if (!mBillingClient.isReady()) {
            startConnection();
        } else {
            Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(skuType);
            if (purchasesResult != null) {
                if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    List<Purchase> purchaseList = purchasesResult.getPurchasesList();
                    log(TAG, "queryPurchases size ---- " + (purchaseList == null ? 0 : purchaseList.size()));
                    if (purchaseList != null && !purchaseList.isEmpty()) {
                        for (Purchase purchase : purchaseList) {
                            if (onGoogleBillingListener != null) {
                                boolean isSuccess = onGoogleBillingListener.onRecheck(skuType, purchase);//是否消耗或者确认
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                    if (skuType.equals(BillingClient.SkuType.INAPP)) {
                                        if (isSuccess) {
                                            consumeAsync(purchase.getPurchaseToken());
                                        } else if (isAutoAcknowledgePurchase) {
                                            if (!purchase.isAcknowledged()) {
                                                acknowledgePurchase(purchase.getPurchaseToken());
                                            }
                                        }
                                    } else if (skuType.equals(BillingClient.SkuType.SUBS)) {
                                        if (isAutoAcknowledgePurchase) {
                                            if (!purchase.isAcknowledged()) {
                                                acknowledgePurchase(purchase.getPurchaseToken());
                                            }
                                        }
                                    }
                                } else {
                                    loge("未支付的订单:" + purchase.getSku());
                                }
                            }
                        }
                    }
                    return purchaseList;
                }
            }
        }
        return null;
    }
    //endregion

    //region===================================在线订单查询=================================

    /**
     * 异步联网查询所有的内购历史-无论是过期的、取消、等等的订单
     *
     * @param listener 监听器
     * @return 返回false的时候说明网络出错
     */
    public boolean queryPurchaseHistoryAsyncInApp(PurchaseHistoryResponseListener listener) {
        if (isReady()) {
            mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, listener);
            return true;
        }
        return false;
    }

    /**
     * 异步联网查询所有的订阅历史-无论是过期的、取消、等等的订单
     *
     * @param listener 监听器
     * @return 返回false的时候说明网络出错
     */
    public boolean queryPurchaseHistoryAsyncSubs(PurchaseHistoryResponseListener listener) {
        if (isReady()) {
            mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, listener);
            return true;
        } else {
            return false;
        }
    }

    //endregion

    //region===================================工具集合=================================

    /**
     * 获取有效订阅的数量
     *
     * @return -1查询失败，0没有有效订阅，>0具有有效的订阅
     */
    public int getPurchasesSizeSubs() {
        List<Purchase> list = queryPurchasesSubs();
        if (list != null) {
            return list.size();
        }
        return -1;
    }

    /**
     * 通过sku获取订阅商品序号
     *
     * @param sku sku
     * @return 序号
     */
    public int getSubsPositionBySku(String sku) {
        return getPositionBySku(sku, BillingClient.SkuType.SUBS);
    }

    /**
     * 通过sku获取内购商品序号
     *
     * @param sku sku
     * @return 成功返回需要 失败返回-1
     */
    public int getInAppPositionBySku(String sku) {
        return getPositionBySku(sku, BillingClient.SkuType.INAPP);
    }

    private int getPositionBySku(String sku, String skuType) {

        if (skuType.equals(BillingClient.SkuType.INAPP)) {
            int i = 0;
            for (String s : inAppSKUS) {
                if (s.equals(sku)) {
                    return i;
                }
                i++;
            }
        } else if (skuType.equals(BillingClient.SkuType.SUBS)) {
            int i = 0;
            for (String s : subsSKUS) {
                if (s.equals(sku)) {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

    /**
     * 通过序号获取订阅sku
     *
     * @param position 序号
     * @return sku
     */
    public String getSubsSkuByPosition(int position) {
        if (position >= 0 && position < subsSKUS.length) {
            return subsSKUS[position];
        } else {
            return null;
        }
    }

    /**
     * 通过序号获取内购sku
     *
     * @param position 序号
     * @return sku
     */
    public String getInAppSkuByPosition(int position) {
        if (position >= 0 && position < inAppSKUS.length) {
            return inAppSKUS[position];
        } else {
            return null;
        }
    }

    /**
     * 通过sku获取商品类型(订阅获取内购)
     *
     * @param sku sku
     * @return inapp内购，subs订阅
     */
    public String getSkuType(String sku) {
        if (Arrays.asList(inAppSKUS).contains(sku)) {
            return BillingClient.SkuType.INAPP;
        } else if (Arrays.asList(subsSKUS).contains(sku)) {
            return BillingClient.SkuType.SUBS;
        }
        return null;
    }

    //endregion

    //region===================================其他方法=================================

    private void executeServiceRequest(final Runnable runnable) {
        if (startConnection()) {
            runnable.run();
        }
    }


    /**
     * google内购服务是否已经准备好
     *
     * @return boolean
     */
    public static boolean isReady() {
        return mBillingClient != null && mBillingClient.isReady();
    }

    /**
     * 设置是否自动确认购买
     *
     * @param isAutoAcknowledgePurchase boolean
     */
    public static void setIsAutoAcknowledgePurchase(boolean isAutoAcknowledgePurchase) {
        GoogleBillingUtil.isAutoAcknowledgePurchase = isAutoAcknowledgePurchase;
    }

    /**
     * 断开连接google服务
     * 注意！！！一般情况不建议调用该方法，让google保留连接是最好的选择。
     */
    public static void endConnection() {
        //注意！！！一般情况不建议调用该方法，让google保留连接是最好的选择。
        if (mBillingClient != null) {
            if (mBillingClient.isReady()) {
                mBillingClient.endConnection();
                mBillingClient = null;
            }
        }
    }

    //endregion

    public GoogleBillingUtil setOnGoogleBillingListener(OnGoogleBillingListener onGoogleBillingListener) {
        this.onGoogleBillingListener = onGoogleBillingListener;
        return this;
    }

    public void removeOnGoogleBillingListener() {
        this.onGoogleBillingListener = null;
    }

    /**
     * 清除内购监听器，防止内存泄漏-在Activity-onDestroy里面调用。
     * 需要确保onDestroy和build方法在同一个线程。
     */
    public void onDestroy() {
        if (builder != null) {
            builder.setListener(null);
        }
        removeOnGoogleBillingListener();
    }

    /**
     * Google购买商品回调接口(订阅和内购都走这个接口)
     */
    private class MyPurchasesUpdatedListener implements PurchasesUpdatedListener {

        @Override
        public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                for (Purchase purchase : list) {
                    if (onGoogleBillingListener != null) {
                        boolean isSuccess = onGoogleBillingListener.onPurchaseSuccess(purchase);//是否自动消耗
                        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                            //是当前页面，并且商品状态为支付成功，才会进行消耗与确认的操作
                            String skuType = getSkuType(purchase.getSku());
                            if (BillingClient.SkuType.INAPP.equals(skuType)) {
                                if (isSuccess) {
                                    //进行消耗
                                    consumeAsync(purchase.getPurchaseToken());
                                } else if (isAutoAcknowledgePurchase) {
                                    //进行确认购买
                                    if (!purchase.isAcknowledged()) {
                                        acknowledgePurchase(purchase.getPurchaseToken());
                                    }
                                }
                            } else if (BillingClient.SkuType.SUBS.equals(skuType)) {
                                //进行确认购买
                                if (isAutoAcknowledgePurchase) {
                                    if (!purchase.isAcknowledged()) {
                                        acknowledgePurchase(purchase.getPurchaseToken());
                                    }
                                }
                            }
                        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                            loge("待处理的订单:" + purchase.getSku());
                        }
                    }
                }
            } else {
                if (IS_DEBUG) {
                    loge("购买失败,responseCode:" + billingResult.getResponseCode() + ",msg:" + billingResult.getDebugMessage());
                }
                if (onGoogleBillingListener != null) {
                    onGoogleBillingListener.onFail(GoogleBillingListenerTag.PURCHASE, billingResult.getResponseCode(), null);
                }
            }
        }

    }

    /**
     * Google查询商品信息回调接口
     */
    private class MySkuDetailsResponseListener implements SkuDetailsResponseListener {
        private String skuType;

        public MySkuDetailsResponseListener(String skuType) {
            this.skuType = skuType;
        }

        @Override
        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                if (onGoogleBillingListener != null) {
                    onGoogleBillingListener.onQuerySuccess(skuType, list);
                }
            } else {
                if (onGoogleBillingListener != null) {
                    onGoogleBillingListener.onFail(GoogleBillingListenerTag.QUERY, billingResult.getResponseCode(), null);
                }
                if (IS_DEBUG) {
                    loge("查询失败,responseCode:" + billingResult.getResponseCode() + ",msg:" + billingResult.getDebugMessage());
                }
            }
        }

    }

    /**
     * Googlg消耗商品回调
     */
    private class MyConsumeResponseListener implements ConsumeResponseListener {

        @Override
        public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (onGoogleBillingListener != null) {
                    onGoogleBillingListener.onConsumeSuccess(purchaseToken);
                }
            } else {
                if (onGoogleBillingListener != null) {
                    onGoogleBillingListener.onFail(GoogleBillingListenerTag.COMSUME, billingResult.getResponseCode(), purchaseToken);
                }
                if (IS_DEBUG) {
                    loge("消耗失败,responseCode:" + billingResult.getResponseCode() + ",msg:" + billingResult.getDebugMessage());
                }
            }
        }
    }

    /**
     * Googlg消耗商品回调
     */
    private class MyAcknowledgePurchaseResponseListener implements AcknowledgePurchaseResponseListener {

        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (onGoogleBillingListener != null) {
                    onGoogleBillingListener.onAcknowledgePurchaseSuccess();
                }
            } else {
                if (onGoogleBillingListener != null) {
                    onGoogleBillingListener.onFail(GoogleBillingListenerTag.AcKnowledgePurchase, billingResult.getResponseCode(), null);
                }
                if (IS_DEBUG) {
                    loge("确认购买失败,responseCode:" + billingResult.getResponseCode() + ",msg:" + billingResult.getDebugMessage());
                }
            }
        }
    }

    public enum GoogleBillingListenerTag {

        QUERY("query"),
        PURCHASE("purchase"),
        SETUP("setup"),
        COMSUME("comsume"),
        AcKnowledgePurchase("AcKnowledgePurchase"),
        ;
        public String tag;

        GoogleBillingListenerTag(String tag) {
            this.tag = tag;
        }
    }

    private static void loge(String msg) {
        if (IS_DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void isDebug(boolean isDebug) {
        GoogleBillingUtil.IS_DEBUG = isDebug;
    }
}
