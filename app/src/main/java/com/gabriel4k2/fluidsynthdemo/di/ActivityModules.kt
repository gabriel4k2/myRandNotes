package com.gabriel4k2.fluidsynthdemo.di

import android.content.Context
import com.gabriel4k2.fluidsynthdemo.data.InstrumentRepository
import com.gabriel4k2.fluidsynthdemo.data.SettingsStorage
import com.gabriel4k2.fluidsynthdemo.domain.IInstrumentRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract
class ActivityModule {


    @Binds
    abstract fun bindInstrumentRepository(dispatchersProvider: InstrumentRepository):
            IInstrumentRepository


    companion object {
        @Provides
        @Singleton
        fun provideMoshi(): Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

        @Provides
        @Singleton
        fun provideSettingsStorage(@ApplicationContext context: Context, moshi: Moshi): SettingsStorage = SettingsStorage(context,moshi)



    }

}