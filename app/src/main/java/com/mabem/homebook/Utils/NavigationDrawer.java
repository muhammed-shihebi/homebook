package com.mabem.homebook.Utils;

import com.mabem.homebook.Model.Member;

public interface NavigationDrawer {
    void disableNavDrawer();
    void enableNavDrawer();
    void setCurrentMember(Member member);
}
