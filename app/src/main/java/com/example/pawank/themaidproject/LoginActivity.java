package com.example.pawank.themaidproject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.pawank.themaidproject.utils.Callback;
import com.example.pawank.themaidproject.Managers.ComManager;
import com.example.pawank.themaidproject.utils.MiscUtils;
import com.example.pawank.themaidproject.Managers.SQLManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity{
    private SQLManager sqlManager;
    private int mRotationStatus;
    private static Context context;
    private EditText mUsername,mPassword;
    private String userName,password;
    private Button loginButton;
    private ComManager comManager;
    private TextWatcher textWatcher;
    private ProgressBar mProgressBar;
    private View view;
    private Callback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        sqlManager=new SQLManager(getApplicationContext());
        context=getApplicationContext();
        comManager = new ComManager(getApplicationContext());
        mRotationStatus = getResources().getConfiguration().orientation;
        /** orientation code**/
        if(mRotationStatus== Configuration.ORIENTATION_PORTRAIT) {
            mUsername = (EditText)findViewById(R.id.editText3);
            mPassword = (EditText)findViewById(R.id.editText4);
            loginButton = (Button)findViewById(R.id.button2);
            mProgressBar = (ProgressBar)findViewById(R.id.progressBar2);
            view =findViewById(R.id.main_layout1);
        }
        else {
            mUsername = (EditText)findViewById(R.id.editText);
            mPassword = (EditText)findViewById(R.id.editText2);
            loginButton = (Button)findViewById(R.id.button);
            mProgressBar = (ProgressBar)findViewById(R.id.progressBar3);
            view =findViewById(R.id.main_layout2);
        }
        /** orientation code end**/
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(mUsername.getText().toString().equals("") || mPassword.getText().toString().equals(""))
                {
                    //MiscUtils.logD("loginActivity",(mPassword.getText().toString().equals(""))+"");
                    loginButton.setEnabled(false);
                }
                else{
                    loginButton.setEnabled(true);
                }
            }
        };
        mUsername.addTextChangedListener(textWatcher);
        mPassword.addTextChangedListener(textWatcher);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ComManager.isNetConnected(LoginActivity.this))
                {
                    Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),"No Internet Connection",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                userName=mUsername.getText().toString();
                password=mPassword.getText().toString();
                Callback callback = new Callback() {
                    @Override
                    public void onSuccess(Object obj) {
                        JSONObject jObj = (JSONObject)obj;
                        MiscUtils.logD("LoginActivity",jObj.toString());
                        try {
                            LoginActivity.this.onDataReceived(jObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Object obj) throws JSONException {
                        JSONObject jObj = (JSONObject)obj;
                        Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),"Server Error Occured",Snackbar.LENGTH_SHORT).show();
                        loginButton.setText("SIGN IN");
                        loginButton.setEnabled(true);
                        mProgressBar.setVisibility(View.GONE);
                    }
                };
                comManager.postLogin(getBaseContext(),userName,password,callback);
                loginButton.setText("Working..");
                loginButton.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onDataReceived(JSONObject jObj) throws JSONException {
        //update SQLManager
        String checksum=jObj.getJSONObject("DATA").getString("CHECKSUM");
        String userId=jObj.getString("USERID");
        sqlManager.updateCredentialTable(userName,checksum,userId);
        Intent in=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(in);
        finish();
    }
}
