package com.gabriel4k2.fluidsynthdemo.di

import com.gabriel4k2.fluidsynthdemo.data.InstrumentRepository
import com.gabriel4k2.fluidsynthdemo.domain.IInstrumentRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract
class ActivityModule {


    @Binds
    abstract fun bindInstrumentRepository(dispatchersProvider: InstrumentRepository):
            IInstrumentRepository

    companion object {
        @Provides
        fun provideMoshi(): Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()


    }

}