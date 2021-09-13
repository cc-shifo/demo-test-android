package com.whty.smartpos.typaysdk.inter;

import com.whty.smartpos.typaysdk.model.ResponseParams;

public interface ITYPayManagerListener {
    public void onResult(ResponseParams respParams);
}
