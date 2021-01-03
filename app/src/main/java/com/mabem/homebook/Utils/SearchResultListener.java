package com.mabem.homebook.Utils;

import com.mabem.homebook.Model.Home;

public interface SearchResultListener {
    public void onHomeSelected(String homeId);

    void onOkPressed();
}
