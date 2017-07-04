package com.example.ibanez_xiphos.criminalproject.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.ibanez_xiphos.criminalproject.PictureUtils;
import com.example.ibanez_xiphos.criminalproject.R;
import com.example.ibanez_xiphos.criminalproject.activity.RecordCameraActivity;
import com.example.ibanez_xiphos.criminalproject.model.Record;
import com.example.ibanez_xiphos.criminalproject.model.RecordLab;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class RecordFragment extends Fragment {
    private static final String ARG_RECORD_ID = "crime_id";
    private static final String DIALOG_DATE = "dialog_date";
    private static final String DIALOG_TIME = "dialog_time";
    private static final String DIALOG_PHOTO = "dialog_photo";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;
    private Record mRecord;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mContactButton;
    private ImageButton mCameraButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_RECORD_ID);
        mRecord = RecordLab.get(getActivity()).getRecord(crimeId);
        mPhotoFile = RecordLab.get(getActivity()).getPhotoFile(mRecord);
        setHasOptionsMenu(true);
        returnResult();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_record, menu);
    }

    @Override
    public void onPause(){
        super.onPause();
        RecordLab.get(getActivity()).updateRecord(mRecord);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_delete_record:
                RecordLab.get(getActivity()).removeRecord(mRecord);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_record, container, false);
        mTitleField = (EditText)v.findViewById(R.id.record_title);
        mTitleField.setText(mRecord.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecord.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDateButton = (Button)v.findViewById(R.id.record_date);
        mDateButton.setText(mRecord.getDateString());
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mRecord.getDate());
                dialog.setTargetFragment(RecordFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button)v.findViewById(R.id.record_time);
        mTimeButton.setText(mRecord.getTimeString());
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mRecord.getDate());
                dialog.setTargetFragment(RecordFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.record_solved);
        mSolvedCheckBox.setChecked(mRecord.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRecord.setSolved(isChecked);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mContactButton = (Button)v.findViewById(R.id.record_contact);
        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null){
            mContactButton.setEnabled(false);
        }

        if (mRecord.getContact() != null){
            mContactButton.setText(mRecord.getContact());
        } else {
            mContactButton.setText(getString(R.string.record_contact_text));
        }

        mReportButton = (Button)v.findViewById(R.id.record_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getRecordReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.record_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        mCameraButton = (ImageButton)v.findViewById(R.id.record_camera);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RecordCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        // if camera is not available, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            mCameraButton.setEnabled(false);
        }

        mPhotoView = (ImageView)v.findViewById(R.id.record_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoFile == null || !mPhotoFile.exists()){
                    return;
                } else {
                    PhotoViewFragment dialog = PhotoViewFragment.newInstance(mRecord.getId());
                    FragmentManager manager = getFragmentManager();
                    dialog.setTargetFragment(RecordFragment.this, REQUEST_PHOTO);
                    dialog.show(manager, DIALOG_PHOTO);
                }
            }
        });
        updatePhotoView();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        if (requestCode == REQUEST_DATE){
            mRecord.setDate((Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));
            mDateButton.setText(mRecord.getDateString());
        } else if (requestCode == REQUEST_TIME){
            mRecord.setDate((Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME));
            mTimeButton.setText(mRecord.getTimeString());
        } else if (requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};

            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            try{
                if (c.getCount() == 0){
                    return;
                }
                c.moveToFirst();
                String contact = c.getString(0);
                mRecord.setContact(contact);
                mContactButton.setText(contact);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }
    }

    public void returnResult(){
        Intent intent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, null);
    }

    public static RecordFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECORD_ID, crimeId);

        RecordFragment fragment = new RecordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String getRecordReport(){
        String solvedString = null;
        if (mRecord.isSolved()){
            solvedString = getString(R.string.record_report_solved);
        } else {
            solvedString = getString(R.string.record_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mRecord.getDate()).toString();

        String contact = mRecord.getContact();
        if (contact == null){
            contact = getString(R.string.record_report_no_contact);
        } else {
            contact = getString(R.string.record_report_contact, contact);
        }

        String report = getString(R.string.record_report, mRecord.getTitle(), dateString, solvedString, contact);
        return report;
    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
