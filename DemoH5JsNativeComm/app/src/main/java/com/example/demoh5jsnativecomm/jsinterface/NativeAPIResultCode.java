package com.example.demoh5jsnativecomm.jsinterface;

import androidx.annotation.IntDef;

@IntDef(value = {
        NativeAPIResultCode.SUCCESS,
        NativeAPIResultCode.REQUEST_PERMISSION
})
public @interface NativeAPIResultCode {
    int SUCCESS = 0;
    int REQUEST_BASE = -100;
    /**
     * 请求权限
     */
    int REQUEST_PERMISSION = REQUEST_BASE - 1;
    /**
     * 权限被拒绝。
     */
    int LOCATION_PERMISSION_DENNY = REQUEST_PERMISSION - 1;
    /**
     * 已经发起了位置请求，但位置还没有获取到，
     */
    int LOCATION_NOT_PREPARED = LOCATION_PERMISSION_DENNY - 1;
}
