package com.dating.google_billing

import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson

object PayLogUtil {
    const val TAG = "PayLogUtil"

    val logMap = HashMap<String, String>()
    var purchaseOrderList = ArrayList<String>()
    val gson = Gson()

    const val SP_NAME = "paylog"
    const val PURCHASE_OVER = "over"

    fun startApp() {
//        // 获取所有的订单列表
//        purchaseOrderList.clear()
//        val purchaseOrders = SPUtils.getInstance(SP_NAME).getString("purchaseOrders")
//        if (purchaseOrders != null) {
//            val split = purchaseOrders.split("|")
//            purchaseOrderList.addAll(split)
//        }
//        // 获取所有订单的日志
//        logMap.clear()
//        purchaseOrderList.forEach {
//            val value = SPUtils.getInstance(SP_NAME).getString(it)
//            if (value != null && value.isNotEmpty()) {
//                addLog(it, "${value}|打开应用,当前金币数${AccountManager.getCoin()}")
//                sendLog(it)
//            }
//        }
    }

    fun addLogJson(orgJson: String, content: String) {
//        try {
//            val orgJsonBean = gson.fromJson(orgJson, PayOrgJsonBean::class.java)
//            orgJsonBean?.run {
//                addLog(orderId, content)
//            }
//        } catch (e: Exception) {
//        }
    }

    fun addLog(orderId: String, content: String) {
//        var orderLog = logMap[orderId]
//        if (orderLog == null) {
//            orderLog = content
//        } else {
//            orderLog += "|$content"
//        }
//        logMap[orderId] = orderLog
//        if (purchaseOrderList.indexOf(orderId) == -1) {
//            purchaseOrderList.add(orderId)
//            savePayLog()
//        }
//        SPUtils.getInstance(SP_NAME).put(orderId, orderLog)
//
//        if (content.contains(PURCHASE_OVER)) {
//            sendLog(orderId)
//        }
    }

    fun savePayLog() {
        SPUtils.getInstance(SP_NAME).put("purchaseOrders", purchaseOrderList.joinToString("|"))
    }

    fun sendLogJson(orgJson: String) {
//        try {
//            val orgJsonBean = gson.fromJson(orgJson, PayOrgJsonBean::class.java)
//            orgJsonBean?.run {
//                sendLog(orderId)
//            }
//        } catch (e: Exception) {
//        }
    }

    fun sendLog(orderId: String) {
//        val orderLog = logMap[orderId]
//        orderLog?.run {
//            log(TAG, "orderId = $orderId, paylogs = $orderLog")
//            RetrofitHelper.getApiService()
//                .addGoogleRechargeVerifyRecord(BasicRequest.session, "", orderId, this)
//                .compose(RxUtil.rxSchedulerHelper())
//                .subscribe(object : ResponseObserver<String>() {
//                    override fun onSuccess(response: String?) {
//                        if (this@run.contains(PURCHASE_OVER)) {
//                            // 消耗完毕并且上传成功，删除该日志
//                            logMap.remove(orderId)
//                            SPUtils.getInstance(SP_NAME).remove(orderId)
//                            purchaseOrderList.remove(orderId)
//                            savePayLog()
//                        }
//                    }
//                })
//        }
    }
}