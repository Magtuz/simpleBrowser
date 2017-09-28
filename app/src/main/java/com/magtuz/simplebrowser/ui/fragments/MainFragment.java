package com.magtuz.simplebrowser.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.magtuz.simplebrowser.Events.SwitchEvent;
import com.magtuz.simplebrowser.R;
import com.magtuz.simplebrowser.common.NetworkUtil;
import com.magtuz.simplebrowser.common.PageCache;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    @BindView(R.id.search_button)
    Button downloadButton;

    @BindView(R.id.text_input)
    EditText textInputView;


    public static MainFragment newInstance(Bundle data) {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(data);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @OnClick(R.id.search_button)
    public void searchClick() {
        if (!TextUtils.isEmpty(textInputView.getText().toString().trim())) {
            if (checkIsUrlValid(textInputView)) {
                PageCache pageCache = new PageCache(getActivity());
                String url = textInputView.getText().toString().trim();
                url = NetworkUtil.hasProtocol(url);
                pageCache.register(url, url, "text/html", "UTF-8", 60 * PageCache.ONE_DAY);
                WebResourceResponse response = pageCache.load(url);
                if (response != null) {
                    EventBus.getDefault().post(new SwitchEvent(url.replace("/", "")));
                } else if (!NetworkUtil.isOnline(getActivity())) {
                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.search_wrong_format), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.search_empty_text), Toast.LENGTH_LONG).show();
        }
    }

    boolean checkIsUrlValid(EditText view) {
        return Patterns.WEB_URL.matcher(view.getText().toString().toLowerCase()).matches();
    }

}
