package me.tylerbwong.stack.play.logging.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import me.tylerbwong.stack.BuildConfig
import me.tylerbwong.stack.data.di.Initializer
import me.tylerbwong.stack.play.logging.CrashlyticsTree
import timber.log.Timber
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
class CrashlyticsModule {

    @[Provides Initializer IntoSet]
    fun provideCrashlyticsInitializer(crashlyticsTree: Provider<CrashlyticsTree>): () -> Unit = {
        if (!BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree())
        }
    }
}
