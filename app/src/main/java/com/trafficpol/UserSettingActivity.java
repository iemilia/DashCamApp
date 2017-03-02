package com.trafficpol;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * Created by Emilia on 4/27/2016.
 */

public class UserSettingActivity extends PreferenceActivity {
    final ParseApplication globalVariable= CardViewUnreportedActivity.globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        setTitle("       Settings");

    }

    public class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            Preference button = (Preference)findPreference(getString(R.string.logout));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //code for what you want it to do
                    String olduser=ParseUser.getCurrentUser().toString();
                    ParseUser.logOut();
                    Intent intent = new Intent(getActivity(),
                            MainActivity.class);
                    globalVariable.setUserName("null");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    //finish();
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactionbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.title:
                Toast.makeText(this, "Driving mode", Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(getApplicationContext(),ADrivingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case android.R.id.home:
                Toast.makeText(this, "Driving mode", Toast.LENGTH_SHORT).show();
                 intent= new Intent(getApplicationContext(),ADrivingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.events:
                Toast.makeText(this, "Item 1:Events selected", Toast.LENGTH_SHORT).show();
                intent= new Intent(getApplicationContext(),CardViewUnreportedActivity.class);
                startActivity(intent);
                break;
            case R.id.reported:
                Toast.makeText(this, "Item 2: Reported selected", Toast.LENGTH_SHORT).show();
                intent= new Intent(getApplicationContext(),CardViewReportedActivity.class);
                startActivity(intent);
                break;
            case R.id.all_videos:
                Toast.makeText(this, "Item 3: All Videos selected", Toast.LENGTH_SHORT).show();
                intent= new Intent(getApplicationContext(),CardViewAllVideos.class);
                startActivity(intent);
                break;
            case R.id.settings:
                Toast.makeText(this, "You are here: Settings", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (globalVariable.getUserName().equals("null")) {
            finish();
        }
    }
}