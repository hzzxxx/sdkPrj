package com.dating.baselib.net;

public class RetrofitHelper {
    private static ApiService mIdeaApiService;

    public static ApiService getApiService() {
        if (mIdeaApiService == null)
            mIdeaApiService = IdeaApi.getApiService(ApiService.class, NetConfig.INSTANCE.getBaseUrl());
        return mIdeaApiService;
    }
}
