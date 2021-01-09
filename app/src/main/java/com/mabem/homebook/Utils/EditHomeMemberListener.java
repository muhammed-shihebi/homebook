package com.mabem.homebook.Utils;

import com.mabem.homebook.Model.Member;

public interface EditHomeMemberListener {
    public void onDeleteClicked(Member member, int position);

    public void onAdminSwitchClicked(Member member, boolean warning); //return 0 if not agreed, 1 if agreed

    public void onDeletingLastAdmin();
}
