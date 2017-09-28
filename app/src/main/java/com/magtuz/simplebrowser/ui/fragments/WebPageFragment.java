package com.magtuz.simplebrowser.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.magtuz.simplebrowser.R;
import com.magtuz.simplebrowser.common.PageCache;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by magtuz on 9/28/2017.
 */

public class WebPageFragment extends Fragment {

    public static final String FILE_NAME = "file_name";

    @BindView(R.id.web_page)
    WebView webView;

    String fileName;

    public static WebPageFragment newInstance(Bundle data) {
        WebPageFragment fragment = new WebPageFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_web_page, container, false);
        ButterKnife.bind(this, root);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            appCompatActivity.getSupportActionBar().setHomeButtonEnabled(true);
        }

        if (getArguments() != null) {
            fileName = getArguments().getString(FILE_NAME);
        }
        PageCache cache = new PageCache(getActivity());
        cache.register(fileName, fileName.replace("/", ""), "text/html", "UTF-8", 60 * PageCache.ONE_DAY);
        WebResourceResponse response = cache.load(fileName);

        try {
            InputStream inputStream = response.getData();
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            webView.loadData(new String(buffer), "text/html", "UTF-8");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

}
