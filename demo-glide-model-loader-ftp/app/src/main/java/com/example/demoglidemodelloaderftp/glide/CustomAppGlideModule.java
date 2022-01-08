/*
 * = COPYRIGHT
 *
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20220106 	         LiuJian                  Create
 */

package com.example.demoglidemodelloaderftp.glide;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.module.AppGlideModule;
import com.rgspace.cocontroller.BuildConfig;

import java.io.InputStream;

import timber.log.Timber;

@GlideModule
public class CustomAppGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        String cacheDir = context.getCacheDir().getPath();
        final GlideExecutor.UncaughtThrowableStrategy myUncaughtThrowableStrategy =
                new GlideExecutor.UncaughtThrowableStrategy() {
                    @Override
                    public void handle(Throwable t) {
                        Timber.e(t);
                    }
                };
        builder.setDiskCacheExecutor(GlideExecutor.newDiskCacheBuilder()
                .setUncaughtThrowableStrategy(myUncaughtThrowableStrategy).build());
        builder.setSourceExecutor(GlideExecutor.newSourceBuilder().build());
        // builder.setDiskCacheExecutor(newDiskCacheExecutor(myUncaughtThrowableStrategy));
        // builder.setResizeExecutor(newSourceExecutor(myUncaughtThrowableStrategy));
        builder.setDiskCache(new DiskLruCacheFactory(cacheDir, "apkIcons",
                1024 * 1024 * 1024));
        if (BuildConfig.DEBUG) {
            builder.setLogLevel(Log.VERBOSE);
        } else {
            builder.setLogLevel(Log.WARN);
        }
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.replace(FtpUrl.class, InputStream.class,
                new FtpUrlModelLoader.Factory());
        super.registerComponents(context, glide, registry);
    }
}
