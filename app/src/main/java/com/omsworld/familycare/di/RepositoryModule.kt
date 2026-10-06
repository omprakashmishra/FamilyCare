package com.omsworld.familycare.di

import com.omsworld.familycare.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Optional module — repositories use @Inject constructor and are
 * automatically provided by Hilt. This module exists to explicitly
 * document the dependency graph and can be extended with @Binds
 * if you later introduce repository interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // Placeholder — Hilt provides @Singleton repositories automatically
    // via constructor injection. No @Provides needed unless you bind
    // to an interface.
    //
    // Example for future use:
    //
    // @Provides
    // @Singleton
    // fun provideAuthRepository(
    //     api: ApiService,
    //     prefs: MySharedPreference
    // ): AuthRepository = AuthRepository(api, prefs)
}