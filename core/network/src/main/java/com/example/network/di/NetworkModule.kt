package com.example.network.di

import com.example.data.repository.SessionRepository
import com.example.network.retrofit.RetrofitClient
import com.example.network.retrofit.auth.AuthApi
import com.example.network.retrofit.profile.ProfileApi
import com.example.network.retrofit.sync.SyncApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideRetrofitClient(sessionRepository: SessionRepository): Retrofit =
        RetrofitClient.init(sessionRepository)

    @Provides
    fun provideSyncApi(retrofit: Retrofit): SyncApi {
        return retrofit.create(SyncApi::class.java)
    }

    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    fun provideProfileApi(retrofit: Retrofit): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }

}