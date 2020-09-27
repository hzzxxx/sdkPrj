package com.video.baselibrary.ext

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dating.sdklib.R
import com.dating.sdklib.glide.GlideOption

fun ImageView.loadHead(
    url: String?,
    width: Int? = null,
    height: Int? = null,
    option: RequestOptions = GlideOption.normalHeadOption(width, height)
) {
    Glide.with(context)
        .asBitmap()
        .load(url)
        .apply(option)
        .into(this)
}


fun ImageView.loadPic(
    url: String?,
    width: Int? = null,
    height: Int? = null,
    option: RequestOptions = GlideOption.normalPicOption(width, height)
) {
    Glide.with(context)
        .asBitmap()
        .load(url)
        .apply(option)
        .into(this)

}

fun ImageView.loadPic1(
    url: String?,
    width: Int? = null,
    height: Int? = null,
    option: RequestOptions = GlideOption.normalPicOption(width, height, 10)
) {
    Glide.with(context)
        .asBitmap()
        .load(url)
        .apply(option)
        .into(this)
}

fun ImageView.loadPic(
    uri: Uri?,
    width: Int? = null,
    height: Int? = null,
    @DrawableRes holder: Int = R.drawable.default_bg,
    option: RequestOptions = GlideOption.normalPicOptionWidthHolder(width, height, holder)
) {
    Glide.with(context)
            .asBitmap()
            .load(uri)
            .apply(option)
            .into(this)

}

fun ImageView.loadGif(
    url: String?,
    width: Int? = null,
    height: Int? = null,
    option: RequestOptions = GlideOption.normalHeadOption(width, height)
) {
    Glide.with(context)
        .asGif()
        .load(url)
        .apply(option)
        .into(this)
}

fun ImageView.loadGif(
    url: Int?,
    width: Int? = null,
    height: Int? = null,
    option: RequestOptions = GlideOption.normalHeadOption(width, height)
) {
    Glide.with(context)
        .asGif()
        .load(url)
        .apply(option)
        .into(this)
}