package com.omsworld.familycare.di

import com.omsworld.familycare.BuildConfig
import com.omsworld.familycare.data.remote.SupabaseApiService
import com.omsworld.familycare.data.remote.SupabaseInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    @Named("supabase")
    fun provideSupabaseOkHttp(
        logging: HttpLoggingInterceptor,
        supabaseInterceptor: SupabaseInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(supabaseInterceptor)
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    @Provides
    @Singleton
    @Named("supabase")
    fun provideSupabaseRetrofit(
        @Named("supabase") client: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl("${BuildConfig.SUPABASE_URL}/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    @Named("supabase")
    fun provideSupabaseApiService(
        @Named("supabase") retrofit: Retrofit
    ): SupabaseApiService = retrofit.create(SupabaseApiService::class.java)
}