package com.example.ibanez_xiphos.criminalproject.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ibanez_xiphos.criminalproject.R;
import com.example.ibanez_xiphos.criminalproject.activity.RecordPagerActivity;
import com.example.ibanez_xiphos.criminalproject.model.RecordLab;
import com.example.ibanez_xiphos.criminalproject.model.Record;

import java.util.List;

public class RecordListFragment extends Fragment {
    public static final String EXTRA_SUBTITLE_VISIBLE =
            "com.example.ibanez_xiphos.criminalproject.extra_subtitle_visible";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final int REQUEST_RECORD = 1;
    private RecyclerView mRecordRecyclerView;
    private LinearLayout mEmptyListView;
    private Button mAddNewButton;
    private RecordAdapter mAdapter;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getActivity().getIntent() != null)
        {
            mSubtitleVisible = getActivity().getIntent().getBooleanExtra(EXTRA_SUBTITLE_VISIBLE, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);
        mRecordRecyclerView = (RecyclerView)view.findViewById(R.id.record_recycler_view);
        mRecordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mEmptyListView = (LinearLayout)view.findViewById(R.id.empty_list_view);
        mAddNewButton = (Button)view.findViewById(R.id.fragment_empty_list_add_new);
        mAddNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRecord();
            }
        });
        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }

    private void updateUI(){
        if(RecordLab.get(getActivity()).getRecords().isEmpty()){
            mRecordRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyListView.setVisibility(View.VISIBLE);
        } else {
            mRecordRecyclerView.setVisibility(View.VISIBLE);
            mEmptyListView.setVisibility(View.INVISIBLE);
            RecordLab recordLab = RecordLab.get(getActivity());
            List<Record> records = recordLab.getRecords();

            if(mAdapter == null){
                mAdapter = new RecordAdapter(records);
                mRecordRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.mRecords = records;
                mAdapter.notifyDataSetChanged();
            }
        }
        updateSubtitle();
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_record_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_new_record:
                addNewRecord();
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_RECORD){
            if (resultCode == Activity.RESULT_OK){

            }
        }
    }

    private void addNewRecord(){
        Record record = new Record();
        RecordLab.get(getActivity()).addRecord(record);
        Intent intent = RecordPagerActivity.newIntent(getActivity(), record.getId());
        intent.putExtra(EXTRA_SUBTITLE_VISIBLE, mSubtitleVisible);
        startActivity(intent);
    }

    private void updateSubtitle(){
        RecordLab recordLab = RecordLab.get(getActivity());
        int recordCount = recordLab.getRecords().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, recordCount, recordCount);

        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class RecordHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public Record mRecord;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        public RecordHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView)itemView.findViewById(R.id.list_item_record_title_text_view);
            mDateTextView = (TextView)itemView.findViewById(R.id.list_item_record_date_text_view);
            mSolvedCheckBox = (CheckBox)itemView.findViewById(R.id.list_item_record_solved_check_box);
        }

        public void bindCrime(Record record){
            mRecord = record;
            mTitleTextView.setText(mRecord.getTitle());
            mDateTextView.setText(mRecord.getDateString());
            mSolvedCheckBox.setClickable(false);
            mSolvedCheckBox.setChecked(mRecord.isSolved());
        }

        @Override
        public void onClick(View v){
            Intent intent = RecordPagerActivity.newIntent(getActivity(), mRecord.getId());
            intent.putExtra(EXTRA_SUBTITLE_VISIBLE, mSubtitleVisible);
            startActivityForResult(intent, REQUEST_RECORD);
        }
    }

    private class RecordAdapter extends RecyclerView.Adapter<RecordHolder> {
        private List<Record> mRecords;

        public RecordAdapter(List<Record> records) {
            mRecords = records;
        }

        @Override
        public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_record, parent, false);
            return new RecordHolder(view);
        }

        @Override
        public void onBindViewHolder(RecordHolder holder, int position){
            Record record = mRecords.get(position);
            holder.bindCrime(record);
        }

        @Override
        public int getItemCount(){
            return mRecords.size();
        }
    }
}
