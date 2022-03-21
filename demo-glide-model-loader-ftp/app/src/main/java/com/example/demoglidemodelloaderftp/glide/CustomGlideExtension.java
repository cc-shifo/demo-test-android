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

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;

@GlideExtension
public class CustomGlideExtension {

    private CustomGlideExtension() {
    }

    @GlideOption
    public static BaseRequestOptions<?> cacheOption(BaseRequestOptions<?> options) {
        return options.skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.DATA);
    }
}
