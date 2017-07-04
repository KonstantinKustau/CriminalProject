package com.example.ibanez_xiphos.criminalproject.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.ibanez_xiphos.criminalproject.R;
import com.example.ibanez_xiphos.criminalproject.fragment.RecordFragment;
import com.example.ibanez_xiphos.criminalproject.fragment.RecordListFragment;
import com.example.ibanez_xiphos.criminalproject.model.Record;
import com.example.ibanez_xiphos.criminalproject.model.RecordLab;

import java.util.List;
import java.util.UUID;

public class RecordPagerActivity extends AppCompatActivity {
    private static final String EXTRA_RECORD_ID =
            "com.example.ibanez_xiphos.criminalproject.record_id";
    private ViewPager mViewPager;
    private List<Record> mRecords;
    private boolean mSubtitleVisible;

    @Override
    public Intent getParentActivityIntent(){
        Intent intent = super.getParentActivityIntent();
        intent.putExtra(RecordListFragment.EXTRA_SUBTITLE_VISIBLE, mSubtitleVisible);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_pager);

        mSubtitleVisible = getIntent().getBooleanExtra(RecordListFragment.EXTRA_SUBTITLE_VISIBLE, false);
        UUID crimeId = (UUID)getIntent().getSerializableExtra(EXTRA_RECORD_ID);

        mViewPager = (ViewPager)findViewById(R.id.activity_record_pager_view_pager);
        mRecords = RecordLab.get(this).getRecords();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Record record = mRecords.get(position);
                return RecordFragment.newInstance(record.getId());
            }

            @Override
            public int getCount() {
                return mRecords.size();
            }
        });

        for (int i = 0; i < mRecords.size(); i++){
            if (mRecords.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext, RecordPagerActivity.class);
        intent.putExtra(EXTRA_RECORD_ID, crimeId);
        return intent;
    }
}
