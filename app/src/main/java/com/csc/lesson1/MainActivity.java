package com.csc.lesson1;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listView;
    private TextView textView, phoneNumber;
    private LinearLayout linearLayout;
    private ImageView imageView;
    private String url = "https://goo.gl/n43FX9";
    private boolean isLand = false;
    private Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.textViewName);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        imageView = (ImageView) findViewById(R.id.imageView);
        drawable = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        isLand = size.x > size.y;

        new SetImageTask().execute();
        textView.setText(getString(R.string.name));
        phoneNumber.setText("8-911-984-39-51");

        ArrayList<HashMap<String, String>> info = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(1994, 5, 15);
        java.text.DateFormat dateFormat = DateFormat.getDateFormat(this);
        String formattedDate = dateFormat.format(calendar.getTime());

        info.add(createField(getString(R.string.birthday), formattedDate));
        info.add(createField(R.string.profession, R.string.profession_value));
        info.add(createField(R.string.sex, R.string.sex_value));
        info.add(createField(R.string.language, R.string.language_value));
        info.add(createField(R.string.prog_language, R.string.prog_language_value));

        SimpleAdapter adapter = new SimpleAdapter(this, info, R.layout.list_item,
                new String[]{"Name", "Value"},
                new int[]{R.id.textViewName, R.id.textViewValue});

        listView.setAdapter(adapter);
    }

    @NonNull
    private HashMap<String, String> createField(@NonNull String name, @NonNull String value) {
        HashMap<String, String> map = new HashMap<>();
        map.put("Name", name + ":");
        map.put("Value", value);
        return map;
    }

    @NonNull
    private HashMap<String, String> createField(int name_id, int value_id) {
        String name = getString(name_id);
        String value = getString(value_id);
        return createField(name, value);
    }

    class SetImageTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (drawable == null) {
                    Log.d(TAG, "doInBackground: getDrawable from " + url);
                    drawable = getDrawableFromUrl(url); // TODO: cache it!
                }
            } catch (IOException e) {
                Log.e(TAG, "onCreate: Cannot set background: ", e);
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (drawable != null) {
                Log.d(TAG, "onPostExecute: setting");
                if (isLand) {
                    imageView.setBackground(drawable);
                } else {
                    linearLayout.setBackground(drawable);
                }
            } else {
                Log.e(TAG, "onPostExecute: drawable is null");
            }
        }

        @Nullable
        private Drawable getDrawableFromUrl(@NonNull String url) throws IOException {
            Bitmap bitmap;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            return new BitmapDrawable(getResources(), bitmap);
        }
    }
}
