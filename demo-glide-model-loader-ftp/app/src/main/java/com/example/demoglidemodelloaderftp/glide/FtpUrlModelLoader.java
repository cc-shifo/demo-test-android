package com.example.demoglidemodelloaderftp.glide;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

/**
 * An {@link ModelLoader} for translating {@link
 * FtpUrl} (ftp URLS) into {@link InputStream} data.
 */
// Public API.
@SuppressWarnings("WeakerAccess")
public class FtpUrlModelLoader implements ModelLoader<FtpUrl, InputStream> {
    /**
     * An integer option that is used to determine the maximum connect and read timeout durations (in
     * milliseconds) for network connections.
     *
     * <p>Defaults to 2500ms.
     */
    public static final Option<Integer> TIMEOUT =
            Option.memory("HomeHttpGlideUrlLoader.Timeout", 2500);

    @Nullable private final ModelCache<FtpUrl, FtpUrl> modelCache;
    // @Nullable private final CustomModelCache<FtpUrl, FtpUrl> modelCache;

    public FtpUrlModelLoader() {
        this(null);
    }

    public FtpUrlModelLoader(@Nullable ModelCache<FtpUrl, FtpUrl> modelCache) {
        this.modelCache = modelCache;
    }

    @Override
    public LoadData<InputStream> buildLoadData(
            @NonNull FtpUrl model, int width, int height, @NonNull Options options) {
        // GlideUrls memoize parsed URLs so caching them saves a few object instantiations and time
        // spent parsing urls.


        // As APP store host uses dynamic link for all images, the modelCache is useless.
        /*GlideUrl url = model;
        if (modelCache != null) {
            url = modelCache.get(model, 0, 0);
            if (url == null) {
                modelCache.put(model, 0, 0, model);
                url = model;
            }
        }
        int timeout = options.get(TIMEOUT);
        return new LoadData<>(url, new HttpUrlFetcher(url, timeout));
        */
        int timeout = options.get(TIMEOUT);
        return new LoadData<>(model, new FtpUrlFetcher(model, timeout));
    }

    @Override
    public boolean handles(@NonNull FtpUrl model) {
        return true;
    }

    /** The default factory for {@link FtpUrlModelLoader}s. */
    public static class Factory implements ModelLoaderFactory<FtpUrl, InputStream> {
        private final ModelCache<FtpUrl, FtpUrl> modelCache = new ModelCache<>(500);

        @NonNull
        @Override
        public ModelLoader<FtpUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new FtpUrlModelLoader(modelCache);
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }
}

