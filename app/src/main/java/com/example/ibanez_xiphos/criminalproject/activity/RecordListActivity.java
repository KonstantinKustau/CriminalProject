package com.example.ibanez_xiphos.criminalproject.activity;

import android.support.v4.app.Fragment;

import com.example.ibanez_xiphos.criminalproject.fragment.RecordListFragment;
import com.example.ibanez_xiphos.criminalproject.fragment.SingleFragmentActivity;

public class RecordListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new RecordListFragment();
    }
}
