package com.magtuz.simplebrowser.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.magtuz.simplebrowser.Events.SwitchEvent;
import com.magtuz.simplebrowser.R;
import com.magtuz.simplebrowser.ui.fragments.MainFragment;
import com.magtuz.simplebrowser.ui.fragments.WebPageFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.magtuz.simplebrowser.ui.fragments.WebPageFragment.FILE_NAME;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        startFragment();
    }

    private void startFragment() {
        Bundle data = new Bundle();
        Fragment f = MainFragment.newInstance(data);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, f).commit();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SwitchEvent event) {
        switchFragments(event.fileName);
    }

    private void switchFragments(String fileName) {
        Bundle data = new Bundle();
        data.putString(FILE_NAME, fileName);
        Fragment fragment = WebPageFragment.newInstance(data);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().addToBackStack(null);
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
                onBackPressed();
                return true;
        }
        return false;
    }
}
