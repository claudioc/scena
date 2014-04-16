package com.claudioc.scena;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends FragmentActivity {

    private Button button;
    private EditText urlField;
    private String url;
    private SharedPreferences dp;
    private SharedPreferences.Editor dpEd;
    private String urlList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "Customize");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        dp = this.getPreferences(Context.MODE_PRIVATE);
        dpEd = dp.edit();

        final ArrayList<String> urlList = new ArrayList<String>(Arrays.asList(dp.getString("urlList", "").split("\n")));
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, urlList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                textView.setTextColor(Color.BLUE);
                textView.setHeight(60);
                textView.setMinimumHeight(60);

                return view;
            }
        };

        // Refreshes the history

        final ListView lv = (ListView) findViewById(R.id.history);
        lv.setDivider(null);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
	            urlField = (EditText) findViewById(R.id.url);
	            urlField.setText(((TextView)view).getText(), TextView.BufferType.EDITABLE);
	            Toast.makeText(getApplicationContext(), "Value copied", Toast.LENGTH_SHORT).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
	            urlList.remove(position);
	            adapter.notifyDataSetChanged();
	            dpEd.putString("urlList", TextUtils.join("\n", urlList.toArray())).commit();
	            Toast.makeText(getApplicationContext(), "History item " + position + " removed", Toast.LENGTH_SHORT).show();
	            return true;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dp = this.getPreferences(Context.MODE_PRIVATE);
        dpEd = dp.edit();

        urlList = dp.getString("urlList", "");

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                urlField = (EditText) findViewById(R.id.url);
                url = urlField.getText().toString().trim();

                if (!Patterns.WEB_URL.matcher(url).matches()) {
                    new ProblemDialogFragment().show(getSupportFragmentManager(), "oops");
                    return;
                }

                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }

                if (!urlList.contains(url)) {
                    urlList = url + "\n" + urlList;
                    dpEd.putString("urlList", urlList).commit();
                }

                Intent intent = new Intent(context, BrowserActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }

        });
    }
}
