package com.dating.sdklib.glide

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class MyAppGlideModule: AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val memoryCacheSize = maxMemory / 8
        builder.setMemoryCache(LruResourceCache(memoryCacheSize.toLong()))

        val cacheDir = context.cacheDir
        if (cacheDir != null) {
            val diskCacheSize = 1024 * 1024 * 512
            builder.setDiskCache(
                DiskLruCacheFactory(
                    cacheDir.absolutePath,
                    "Images",
                    diskCacheSize.toLong()
                )
            )
        }

        //设置Bitmap的缓存池
//        builder.setBitmapPool(new LruBitmapPool(30));

        //设置内存缓存
//        builder.setMemoryCache(new LruResourceCache(30));
//
//        //设置磁盘缓存
//        builder.setDiskCache(new InternalCacheDiskCacheFactory(context));

        //设置读取不在缓存中资源的线程
//        builder.setResizeExecutor(GlideExecutor.newSourceExecutor());
//
//        //设置读取磁盘缓存中资源的线程
//        builder.setDiskCacheExecutor(GlideExecutor.newDiskCacheExecutor());
//
//        //设置日志级别
//        builder.setLogLevel(Log.VERBOSE);

        //设置全局选项
        val requestOptions = RequestOptions()
        requestOptions.format(DecodeFormat.PREFER_RGB_565)
        builder.setDefaultRequestOptions(requestOptions)
    }
}