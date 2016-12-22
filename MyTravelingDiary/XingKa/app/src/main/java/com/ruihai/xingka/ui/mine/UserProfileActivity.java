package com.ruihai.xingka.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.fragment.OfficalAccountFragment;
import com.ruihai.xingka.ui.mine.fragment.UserProfileFragment;

public class UserProfileActivity extends BaseActivity {

    public static void launch(Context from, String userAccount, int type, int isOffical) {
        Intent intent = new Intent(from, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra("userAccount", userAccount);
        intent.putExtra("type", type);
        intent.putExtra("isOffical", isOffical);
        from.startActivity(intent);
    }

    private String mUserAccount;
    private int mType;
    private int isOffical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mType = getIntent().getIntExtra("type", 1);
        isOffical = getIntent().getIntExtra("isOffical", 0);

        if (savedInstanceState == null && getIntent() != null) {
            mUserAccount = getIntent().getStringExtra("userAccount");
        } else {
            if (savedInstanceState != null) {
                mUserAccount = savedInstanceState.getString("userAccount");
            }
        }
        if (isOffical == 1) {
            OfficalAccountFragment fragment = OfficalAccountFragment.newInstance(mUserAccount, 0, mType);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();

        } else {
            UserProfileFragment fragment = UserProfileFragment.newInstance(mUserAccount);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();

        }



    }
}
