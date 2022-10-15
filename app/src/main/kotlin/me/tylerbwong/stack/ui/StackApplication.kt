package me.tylerbwong.stack.ui

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import com.jakewharton.processphoenix.ProcessPhoenix
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import me.tylerbwong.stack.data.di.Initializer
import me.tylerbwong.stack.ui.theme.ThemeManager
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import javax.inject.Inject

@HiltAndroidApp
class StackApplication : Application(), Configuration.Provider, ImageLoaderFactory {

    @[Inject Initializer]
    lateinit var initializers: Set<@JvmSuppressWildcards () -> Unit>

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var okHttpClient: Lazy<OkHttpClient>

    override fun onCreate() {
        if (ProcessPhoenix.isPhoenixProcess(this)) {
            return
        }
        super.onCreate()
        ThemeManager.init(this)
        initializers.forEach { initializer -> initializer() }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    class RequestUrlInterceptor implements Interceptor {
        @Override public Response intercept(Interceptor.Chain chain) throws IOException {
          Request originalRequest = chain.request();
      
          Request compressedRequest = originalRequest.newBuilder()
            .url("https://images.weserv.nl/?url=%s&format=webp".format(originalRequest.url()))
            .get(originalRequest.body())
            .build();
          return chain.proceed(compressedRequest);
        }
    }

    // getString(R.string.image_proxy_url, )
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .okHttpClient { 
                OkHttpClient.Builder().addNetworkInterceptor(RequestUrlInterceptor()).build();
             }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve(IMAGE_CACHE_DIR))
                    .build()
            }
            .build()
    }

    companion object {
        private const val IMAGE_CACHE_DIR = "stack_image_cache"
    }
}
