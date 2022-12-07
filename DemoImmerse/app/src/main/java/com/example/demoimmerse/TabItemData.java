/*
 * = COPYRIGHT
 *          xxxx
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                    Author                  Action
 * 2022-11-09              LiuJian                 Create
 */

package com.example.demoimmerse;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class TabItemData {
    /**
     * 图标资源
     */
    private Drawable mIcon;

    public TabItemData(@NonNull Drawable icon) {
        mIcon = icon;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }
}
