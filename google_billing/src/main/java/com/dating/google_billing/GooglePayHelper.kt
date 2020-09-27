package com.video.baselibrary.pay.googlebilling

import android.app.Activity
import android.os.Handler
import androidx.annotation.NonNull
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.blankj.utilcode.util.SPUtils
import com.dating.bean.PayConfigBean
import com.dating.google_billing.GoogleBillingUtil
import com.dating.google_billing.OnGoogleBillingListener
import com.dating.google_billing.PayLogUtil
import com.dating.sdklib.app.BaseApplication
import com.dating.sdklib.util.log

class GooglePayHelper {

    private val TAG = GoogleBillingUtil.TAG

    private val googleBillingUtil by lazy { GoogleBillingUtil.getInstance() }
    private var homeDiscount: PayConfigBean? = null
    private var rechargeDiscount: PayConfigBean? = null

    object Discount {
        var HOME_DISCOUNT_PRODUCT_ID = "google_gold_137_8"
        var RECHARGE_DISCOUNT_PRODUCT_ID = "google_gold_137_9"

        init {
//            if (BuildConfig.channel_id == 3) {
//                HOME_DISCOUNT_PRODUCT_ID = "google_gold_3_8"
//                RECHARGE_DISCOUNT_PRODUCT_ID = "google_gold_3_9"
//            }
        }
    }

    private object Sku {
        // higo 内购项
        var INAPP = arrayOf("google_gold_137_1", "google_gold_137_2", "google_gold_137_3", "google_gold_137_4", "google_gold_137_5", "google_gold_137_6", "google_gold_137_8", "google_gold_137_9")
        var SUB = arrayOf("google_vip_137_7")

        init {
//            if (BuildConfig.channel_id == 3) {// boiling 内购项
//                INAPP = arrayOf("google_gold_3_1", "google_gold_3_2", "google_gold_3_3", "google_gold_3_4", "google_gold_3_5", "google_gold_3_6", "google_gold_3_8", "google_gold_3_9")
//                SUB = arrayOf("google_vip_3_7")
//            }
        }
    }

    init {
        googleBillingUtil.setOnGoogleBillingListener(BillingListener()).build(BaseApplication.mInstance)
        GoogleBillingUtil.setIsAutoAcknowledgePurchase(false)

        GoogleBillingUtil.setSkus(Sku.INAPP, Sku.SUB)
    }

    companion object {
        const val SP_PURCHASE_TOKEN_HISTORY = "SP_PURCHASE_TOKEN_HISTORY"
        const val SP_PURCHASE_TOKEN_HISTORY_KEY = "SP_PURCHASE_TOKEN_HISTORY_KEY"

        fun getInstance(): GooglePayHelper = Helper.instance

        fun purchaseInApp(activity: Activity, skuId: String) {
            getInstance().googleBillingUtil.purchaseInApp(activity, skuId)
        }

        fun purchaseSub(activity: Activity, skuId: String) {
            getInstance().googleBillingUtil.purchaseSubs(activity, skuId)
        }
        /**
         * 购买首页0.99折扣产品
         */
        fun purchaseHomeDiscount(activity: Activity) {
            getInstance().purchaseHomeDiscount(activity)
        }
        /**
         * 购买4.99折扣产品
         */
        fun purchaseRechargeDiscount(activity: Activity) {
            getInstance().purchaseRechargeDiscount(activity)
        }

        fun onDestroy() {
            getInstance().onDestroy()
        }

        fun onResume() {
            getInstance().onResume()
        }
    }

    private object Helper {
        val instance = GooglePayHelper()
    }

    var currPurchase: Purchase? = null
    // 记录每个purchase验证的次数
    var purchaseMap = HashMap<String, Int>()

    inner class BillingListener: OnGoogleBillingListener() {
        override fun onRecheck(@NonNull skuType: String, @NonNull purchase: Purchase): Boolean { // 服务器验证
            log(GoogleBillingUtil.TAG, "查到未处理订单---" + purchase.sku + "  isAcknowledged=" + purchase.isAcknowledged + "    purchase state = " + purchase.purchaseState)
            log(GoogleBillingUtil.TAG, purchase.signature)
            log(GoogleBillingUtil.TAG, purchase.originalJson)
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                val tokenHistory = SPUtils.getInstance(SP_PURCHASE_TOKEN_HISTORY).getString(SP_PURCHASE_TOKEN_HISTORY_KEY)
                log(GoogleBillingUtil.TAG, "获取的历史token为：$tokenHistory")
                if (BillingClient.SkuType.INAPP == skuType || (!purchase.isAcknowledged && (tokenHistory.isNullOrEmpty() || !tokenHistory.contains(purchase.purchaseToken)))) {
//                    PayLogUtil.addLogJson(purchase.originalJson, "查到未消耗订单${purchase.sku}当前金币数${AccountManager.getCoin()}")
                    if (purchaseMap.containsKey(purchase.signature)) {
                        var checkCount = purchaseMap.get(purchase.signature)!!
                        if (checkCount < 3) {
                            PayLogUtil.addLogJson(purchase.originalJson, "验证次数$checkCount<3执行验证")
                            log(GoogleBillingUtil.TAG, "验证次数$checkCount<3执行验证")
                            checkCount++
                            purchaseMap.put(purchase.signature, checkCount)
                            checkPayResult(purchase)
                        } else {
                            log(GoogleBillingUtil.TAG, "验证次数$checkCount>3不执行验证")
                            PayLogUtil.addLogJson(purchase.originalJson, "验证次数$checkCount>3不执行验证")
                            googleBillingUtil.addErrorPurchase(purchase.signature)
                        }
                    } else {
                        log(GoogleBillingUtil.TAG, "第一次执行验证")
                        PayLogUtil.addLogJson(purchase.originalJson, "第一次执行验证")
                        purchaseMap.put(purchase.signature, 1)
                        checkPayResult(purchase)
                    }

                }
            }
            return false
        }

        override fun onPurchaseSuccess(@NonNull purchase: Purchase): Boolean { // 服务器验证
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                log(GoogleBillingUtil.TAG, "购买成功---" + purchase.sku)
                log(GoogleBillingUtil.TAG, purchase.signature)
                log(GoogleBillingUtil.TAG, purchase.originalJson)
//                PayLogUtil.addLogJson(purchase.originalJson, "购买成功${purchase.sku}当前金币数${AccountManager.getCoin()}")
                checkPayResult(purchase)
            }
            return false
        }

        override fun onConsumeSuccess(@NonNull purchaseToken: String) {
            log(GoogleBillingUtil.TAG, "消耗成功---$purchaseToken")
            currPurchase?.run {
                if (this.purchaseToken == purchaseToken) {
                    PayLogUtil.addLogJson(this.originalJson, "消耗成功${PayLogUtil.PURCHASE_OVER}")
                }
            }
        }

        override fun onAcknowledgePurchaseSuccess() {
            log(GoogleBillingUtil.TAG, "确认购买成功---")
        }

        override fun onFail(@NonNull tag: GoogleBillingUtil.GoogleBillingListenerTag, responseCode: Int, purchaseToken: String?) {
            log(TAG, "操作失败:tag=" + tag.tag.toString() + ",responseCode=" + responseCode)
            if (tag === GoogleBillingUtil.GoogleBillingListenerTag.COMSUME) {
                currPurchase?.run {
                    if (this.purchaseToken == purchaseToken) {
                        PayLogUtil.addLogJson(this.originalJson, "消耗失败code=$responseCode")
                    }
                }
            } else if (tag == GoogleBillingUtil.GoogleBillingListenerTag.PURCHASE) {// 主要发送给折扣项
//                EventBusHelper.post(MessageEvent(EventBusConst.EVENT_GOOGLE_PURCHASE_FAIL, EventBean(0)))
            }
        }

        override fun onError(@NonNull tag: GoogleBillingUtil.GoogleBillingListenerTag) {
            log(TAG, "操作失败:tag=" + tag.tag)
        }
    }

    private fun checkPayResult(purchase: Purchase) {
        currPurchase = purchase
//        BaseApplication.mInstance.startKtxActivity(ARouterPath.ActivityCheckGooglePay)
    }

    private fun purchaseHomeDiscount(activity: Activity) {
//        AccountManager.getInstance().globalParam?.ggpConfigs?.ggpConfig?.forEach {
//            if (it.productId == Discount.HOME_DISCOUNT_PRODUCT_ID) {
//                googleBillingUtil.purchaseInApp(activity, it.productId)
//                return
//            }
//        }
//        loge(TAG, "没有找到首页折扣产品配置")
    }

    private fun purchaseRechargeDiscount(activity: Activity) {
//        AccountManager.getInstance().globalParam?.ggpConfigs?.ggpConfig?.forEach {
//            if (it.productId == Discount.RECHARGE_DISCOUNT_PRODUCT_ID) {
//                googleBillingUtil.purchaseInApp(activity, it.productId)
//                return
//            }
//        }
//        loge(TAG, "没有找到充值折扣产品配置")
    }

    private var isFirstIn = true

    /**
     * 如果调用了onDestroy，那必须调用该方法
     */
    private fun onResume() {
        // 初始化billing库会执行查询，避免首次重复查询
        if (isFirstIn) {
            isFirstIn = false
            return
        }

        // 延时2秒，避免上一次消耗还未结束
        handler.removeMessages(Flag_recheck_purchase)
        handler.sendEmptyMessageDelayed(Flag_recheck_purchase, 5000)

        googleBillingUtil.setOnGoogleBillingListener(BillingListener())
    }

    private fun onDestroy() {
        googleBillingUtil.removeOnGoogleBillingListener()
        handler.removeMessages(Flag_recheck_purchase)
    }

    private val Flag_recheck_purchase = 8010
    private val handler = Handler(Handler.Callback {
        if(it.what == Flag_recheck_purchase) {
            log(TAG, "GooglePayHelper 执行轮询查询")
            googleBillingUtil.queryPurchasesInApp()
            googleBillingUtil.queryPurchasesSubs()
        }

        false
    })
}