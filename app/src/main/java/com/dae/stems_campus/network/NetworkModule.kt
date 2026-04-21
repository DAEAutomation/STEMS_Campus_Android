package com.dae.stems_campus.network

import com.dae.stems_campus.viewmodel.TokenManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val DISCOVERY_BASE_URL = "https://stemscampus.dae.tw:8443/"
    private const val PLACEHOLDER_BASE_URL = "https://placeholder.local/"

    /**
     * 提供 Gson
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideResponseLoggingInterceptor(): ResponseLoggingInterceptor {
        return ResponseLoggingInterceptor()
    }

    /**
     * 提供 TokenInterceptor
     */
    @Provides
    @Singleton
    fun provideTokenInterceptor(tokenManager: TokenManager): TokenInterceptor {
        return TokenInterceptor(tokenManager)
    }

    /**
     * 提供 BaseUrlInterceptor
     */
    @Provides
    @Singleton
    fun provideBaseUrlInterceptor(holder: BaseUrlHolder): BaseUrlInterceptor {
        return BaseUrlInterceptor(holder)
    }

    /**
     * 業務用 OkHttpClient（含 BaseUrlInterceptor + TokenInterceptor）
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        baseUrlInterceptor: BaseUrlInterceptor,
        tokenInterceptor: TokenInterceptor,
        responseLoggingInterceptor: ResponseLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(baseUrlInterceptor)
            .addInterceptor(responseLoggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Discovery 用 OkHttpClient（不需要 token，也不需要 BaseUrlInterceptor）
     */
    @Provides
    @Singleton
    @Named("discovery")
    fun provideDiscoveryOkHttpClient(
        responseLoggingInterceptor: ResponseLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(responseLoggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 業務用 Retrofit（baseUrl 是 placeholder，會被 BaseUrlInterceptor 改寫）
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(PLACEHOLDER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Discovery 用 Retrofit（固定 URL）
     */
    @Provides
    @Singleton
    @Named("discovery")
    fun provideDiscoveryRetrofit(
        @Named("discovery") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DISCOVERY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * 提供 ApiService
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    /**
     * 提供 DiscoveryService
     */
    @Provides
    @Singleton
    fun provideDiscoveryService(@Named("discovery") retrofit: Retrofit): DiscoveryService {
        return retrofit.create(DiscoveryService::class.java)
    }
}
