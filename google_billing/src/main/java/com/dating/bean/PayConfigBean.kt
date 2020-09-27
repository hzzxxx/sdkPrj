package com.dating.bean

import com.dating.sdklib.bean.BaseData


class PayConfigBean : BaseData(){
    /**
     * ProductId : string
     * PriceUSD : 0
     * BaseCount : 0
     * ExtraCount : 0
     * ProductType : 0
     * BannerExtraCount : 0
     * OriPriceUSD : 0
     * IsBought : true
     */

    var productId: String = ""
    var priceUSD: Float = 0f
    var baseCount: Int = 0
    var extraCount: Int = 0
    var productType: Int = 0

    var bannerExtraCount: Int = 0
    var oriPriceUSD: Float = 0f

    var priceINR: Float = 0f

    var isBought: Boolean = false

    override fun toString(): String {
        return "PayConfigBean(productId=$productId, priceUSD=$priceUSD, baseCount=$baseCount, extraCount=$extraCount, productType=$productType, bannerExtraCount=$bannerExtraCount, oriPriceUSD=$oriPriceUSD, isBought=$isBought)"
    }

}