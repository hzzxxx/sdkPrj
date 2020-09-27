package com.dating.sdklib.glide

import androidx.annotation.DrawableRes
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.dating.sdklib.R
import com.dating.sdklib.ext.toPx

object GlideOption {

    fun normalHeadOption(width: Int?, height: Int?): RequestOptions {
        return RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.default_head)
            .error(R.drawable.default_head)
            .apply {
                width?.run {
                    height?.run {
                        override(width.toPx(), height.toPx())
                    }
                }
            }
    }

    fun normalPicOption(width: Int?, height: Int?): RequestOptions {
        return RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.default_bg)
            .error(R.drawable.default_bg)
            .apply {
                width?.run {
                    height?.run {
                        override(width.toPx(), height.toPx())
                    }
                }
            }
    }

    fun normalPicOptionWidthHolder(width: Int?, height: Int?, @DrawableRes holder: Int = R.drawable.default_bg): RequestOptions {
        return RequestOptions()
                .centerCrop()
                .placeholder(holder)
                .error(holder)
                .apply {
                    width?.run {
                        height?.run {
                            override(width.toPx(), height.toPx())
                        }
                    }
                }
    }

    fun normalfixXYPicOption(width: Int?, height: Int?): RequestOptions {
        return RequestOptions()
           // .placeholder(R.drawable.default_bg)
            .error(R.drawable.default_bg)
            .apply {
                width?.run {
                    height?.run {
                        override(width.toPx(), height.toPx())
                    }
                }
            }
    }

    fun normalPicOption(width: Int?, height: Int?, radius: Int = 0): RequestOptions {
        return if (radius>0) RequestOptions.bitmapTransform(RoundedCorners(radius)) else RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.default_bg)
            .error(R.drawable.default_bg)
            .apply {
                width?.run {
                    height?.run {
                        override(width.toPx(), height.toPx())
                    }
                }
            }
    }
}