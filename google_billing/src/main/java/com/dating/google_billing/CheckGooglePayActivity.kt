package com.video.baselibrary.pay.googlebilling

import android.os.Bundle
import android.view.View
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.blankj.utilcode.util.SPUtils
import com.dating.google_billing.GoogleBillingUtil
import com.dating.google_billing.PayLogUtil
import com.dating.google_billing.R
import com.dating.sdklib.base.view.BaseActivity
import com.dating.sdklib.util.log

class CheckGooglePayActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_check_googl_pay_yq

    override fun initView() {
        startLoading(false)
    }

    override fun initData(bundle: Bundle?) {
        GooglePayHelper.getInstance().currPurchase?.run {
            checkResult(this)
            return
        }
        finish()
    }

    override fun onBackPressed() {

    }

    private fun checkResult(purchase: Purchase) {
        PayLogUtil.addLogJson(purchase.originalJson, "开始服务器验证")
        log(className, "checkResult-----purchase = " + purchase.sku)
//        RetrofitHelper.getApiService()
//            .payWidthGoogle(BasicRequest.session, purchase.originalJson, purchase.signature, 1)
//            .compose(RxUtil.rxSchedulerHelper())
//            .subscribe(object : ResponseObserver<AddDiamondMsgBean>() {
//                override fun onSuccess(response: AddDiamondMsgBean?) {
//                    PayLogUtil.addLogJson(purchase.originalJson, "验证成功code==0")
//                    response?.run {
//                        EventStatistics.onGoogleInAppPurchase(InitialApplication.instance, priceUSD)
//
//                        if (isSVip) {
//                            ToastUtils.show(getString(R.string.buy_svip_success))
//                            AccountManager.setSVip(true)
//                            AccountManager.increaseCoins(leftDiamond)
//                            EventBusHelper.post(MessageEvent(EventBusConst.EVENT_SUB_SVIP))
//                        } else {
//                            ToastUtils.show(getString(R.string.buy_coins_success, leftDiamond))
//                            log(GoogleBillingUtil.TAG, "google 购买金币验证成功")
//                            AccountManager.increaseCoins(leftDiamond)
//                            Upurchase(productId, 1, priceUSD.toString())
//                        }
//                        if (this.productId == GooglePayHelper.Discount.RECHARGE_DISCOUNT_PRODUCT_ID) {
//                            hasBuyDiscount = true
//                        }
//                        EventBusHelper.post(MessageEvent(EventBusConst.EVENT_GOOGLE_PURCHASE_SUCCESS, EventBean(0, this)))
//                    }
//
//                    consumePurchase(purchase)
//                    finish()
//                }
//
//                override fun onCodeFail(errorCode: Int) {
//                    super.onCodeFail(errorCode)
//                    PayLogUtil.addLogJson(purchase.originalJson, "验证失败code==$errorCode")
//                    // 处理session失效等问题
//                    if (errorCode != CodeDefines.RetCode_SessionInvalid && errorCode != CodeDefines.RetCode_SessionNotFound && errorCode != CodeDefines.RetCode_SessionExpired) {
//                        consumePurchase(purchase)
//                    } else {
//                        PayLogUtil.sendLogJson(purchase.originalJson)
//                    }
//                    finish()
//                }
//
//                override fun onFail(message: String?) {
//                    super.onFail(message)
//                    PayLogUtil.addLogJson(purchase.originalJson, "验证http请求失败")
//                    PayLogUtil.sendLogJson(purchase.originalJson)
//                    finish()
//                }
//            })
    }

    private fun consumePurchase(purchase: Purchase) {
        if (BillingClient.SkuType.INAPP == GoogleBillingUtil.getInstance().getSkuType(purchase.sku)) {
//            PayLogUtil.addLogJson(purchase.originalJson, "开始消耗商品,当前金币数${AccountManager.getCoin()}")
            PayLogUtil.sendLogJson(purchase.originalJson)
            log(GoogleBillingUtil.TAG, "验证商品回调code=" + 0 + ", 开始消耗---")
            GoogleBillingUtil.getInstance().consumeAsync(purchase.purchaseToken)
        } else {
            log(GoogleBillingUtil.TAG, "验证订阅回调code=" + 0 + ", 开始消耗---")
            // 增加一个消耗的历史列表，每个订阅只消耗一次，避免有的手机出现无法消耗
            var tokenHistory = SPUtils.getInstance(GooglePayHelper.SP_PURCHASE_TOKEN_HISTORY).getString(GooglePayHelper.SP_PURCHASE_TOKEN_HISTORY_KEY)
            log(GoogleBillingUtil.TAG, "消耗订阅，获取的历史token为：$tokenHistory")
            if (tokenHistory.isNullOrEmpty()) {
                tokenHistory = purchase.purchaseToken
            } else {
                tokenHistory += "split${purchase.purchaseToken}"
            }
            SPUtils.getInstance(GooglePayHelper.SP_PURCHASE_TOKEN_HISTORY).put(GooglePayHelper.SP_PURCHASE_TOKEN_HISTORY_KEY, tokenHistory)
            GoogleBillingUtil.getInstance().acknowledgePurchase(purchase.purchaseToken)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        endLoading()
    }
}
