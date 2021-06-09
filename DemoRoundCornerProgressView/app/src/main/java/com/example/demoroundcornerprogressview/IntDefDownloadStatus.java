/*
 * = COPYRIGHT
 *          // description
 *     Copyright (C) 2021-05-28  XXXX rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                    Author                    Action
 * 2021-05-28             Liu Jian                   Create
 */

package com.example.demoroundcornerprogressview;

import androidx.annotation.IntDef;

// success, failed, suspend, waiting, in progress
@IntDef({
        IntDefDownloadStatus.SUCCESS, 
        IntDefDownloadStatus.FAILED,
        IntDefDownloadStatus.STOPPED, IntDefDownloadStatus.READY,
        IntDefDownloadStatus.DOWNLOADING
})
public @interface IntDefDownloadStatus {
    int INIT = -1;// show "download"
    int SUCCESS = 0;// show "install"
    int FAILED = 1; // show "failed"
    int STOPPED = 2; // show "download", but with the composite background color of the
    // reached and unreached.
    int READY = 3; // show "waiting"
    int DOWNLOADING = 4; // show "progress"
}
