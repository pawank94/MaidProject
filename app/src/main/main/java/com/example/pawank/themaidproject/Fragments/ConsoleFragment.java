package com.example.pawank.themaidproject.Fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.pawank.themaidproject.MainActivity;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.utils.ConsoleCommands;
import com.example.pawank.themaidproject.utils.MiscUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class ConsoleFragment extends Fragment {

    private ProgressBar parentProgressBar;
    private ImageButton console_enter;
    private EditText console_command;
    private Handler mHandler;
    private RandomAccessFile raf;
    private String FILE_NAME = "console_log.log";
    private String log_content;
    private ScrollView console_scroll_view;
    private TextView console_log_container;

    public static ConsoleFragment newInstance() {
        ConsoleFragment fragment = new ConsoleFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_console, container, false);
        console_command = (EditText)v.findViewById(R.id.console_command);
        console_log_container = (TextView) v.findViewById(R.id.console_log_container);
        parentProgressBar = (ProgressBar) ((MainActivity)getContext()).findViewById(R.id.progressBarFragmentContainer);
        console_enter = (ImageButton) v.findViewById(R.id.console_enter);
        console_scroll_view = (ScrollView) v.findViewById(R.id.console_scroll_view);
        mHandler = new Handler(Looper.getMainLooper());
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(parentProgressBar.getVisibility()==View.VISIBLE)
                                parentProgressBar.setVisibility(View.GONE);
                            log_content = MiscUtils.getConsoleLogs(getActivity());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                console_log_container.setText(Html.fromHtml(log_content,Html.FROM_HTML_MODE_LEGACY));
                            }
                            else{
                                console_log_container.setText(Html.fromHtml(log_content));
                            }
                            console_scroll_view.scrollTo(0,console_log_container.getBottom());
                            console_enter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ConsoleCommands.execCommand(getActivity(),console_command.getText().toString());
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        console_enter.requestFocus();
                    }
                },50);
            }
        }).run();
    }
}
