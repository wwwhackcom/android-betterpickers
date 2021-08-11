package com.codetroopers.betterpickers.sample.activity.timepicker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.codetroopers.betterpickers.sample.R;
import com.codetroopers.betterpickers.sample.activity.BaseSampleActivity;
import com.codetroopers.betterpickers.sample.fragment.SampleTimeFragment;

/**
 * User: derek Date: 3/17/13 Time: 3:59 PM
 */
public class SampleTimeUsingFragment extends BaseSampleActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);

        Fragment fragment = new SampleTimeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.frame, fragment);

        transaction.commit();
    }
}
