package com.omsworld.familycare.Diary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.MyServiceListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by omprakash.m on 1/22/2018.
 */

public class EditDiary_fr extends Fragment implements View.OnClickListener {

    static CommonFunctions cmf;
    String USER_ID;
    EditText mTitleText;
    EditText mBodyText;
    TextView mDateText;

    String id = "";
    ImageView IV_save, IV_delete;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.edit_diary_fr, container, false);
        cmf = new CommonFunctions(getActivity());
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        USER_ID = cmf.myPreference.getString(getActivity(), cmf.gc.USER_ID);

        mTitleText = (EditText) rootView.findViewById(R.id.title);
        mBodyText = (EditText) rootView.findViewById(R.id.body);
        mDateText = (TextView) rootView.findViewById(R.id.notelist_date);
        IV_save = (ImageView) rootView.findViewById(R.id.IV_save);
        IV_delete = (ImageView) rootView.findViewById(R.id.IV_delete);

        IV_save.setOnClickListener(this);
        IV_delete.setOnClickListener(this);

        cmf.bdl = this.getArguments();
        if (cmf.bdl != null) {
            // pageType = cmf.bdl.getString(cmf.gc.fromPage);
            id = cmf.bdl.getString("id");
            String note = cmf.bdl.getString("note");
            String subject = cmf.bdl.getString("subject");
            String added_date = cmf.bdl.getString("added_date");
            mTitleText.setText(subject);
            mBodyText.setText(note);
            mDateText.setText(added_date);
        } else {
            long msTime = System.currentTimeMillis();
            Date curDateTime = new Date(msTime);

            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
            String curDate = formatter.format(curDateTime);
            mDateText.setText("" + curDate);
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.IV_save:
                //save
                saveState();
                break;
            case R.id.IV_delete:
                //delete
                deleteNote();
                break;
        }
    }


    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if (id.equals("")) {
            //create note
            cteateNote(title, body);
        } else {
            //update note
            updateNote(title, body);
        }
    }


    private void cteateNote(String title, String body) {
        new CallWebService(getActivity(), cmf.urlList.add_diary, cmf.add_diary(USER_ID, title, body), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                Log.d("--------add_diary--->" + string, "");
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("status");
                    // String Message = jsonObject.optString("Message");
                    if (Status.equals("1")) {
                        cmf.back_fragment();
                    } else {
                        //  Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
            }
        });

    }

    private void updateNote(String title, String body) {
        new CallWebService(getActivity(), cmf.urlList.edit_diary, cmf.edit_diary(USER_ID, id, title, body), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                Log.d("--------add_diary--->" + string, "");
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("status");
                    // String Message = jsonObject.optString("Message");
                    if (Status.equals("1")) {
                        cmf.back_fragment();
                    } else {
                        //  Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
            }
        });
    }

    private void deleteNote() {
        new CallWebService(getActivity(), cmf.urlList.delete_diary, cmf.delete_diary(USER_ID, id), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                Log.d("--------add_diary--->" + string, "");
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("status");
                    // String Message = jsonObject.optString("Message");
                    if (Status.equals("1")) {
                        cmf.back_fragment();
                    } else {
                        //  Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
            }
        });
    }


}