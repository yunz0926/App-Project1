package com.example.project1_2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements TextWatcher {
    private final int PICK_IMAGE = 1111;
    private ImageAdapter imageAdapter;
    private RecyclerView rv;
    private LinearLayoutManager llm;
    private int i = 0;
    private EditText editText;
    private RvAdapter adapter;
    public static ArrayList<Item> itemList = new ArrayList<>();
    LinearLayout contact;
    ConstraintLayout gallery;
    ConstraintLayout weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab) ;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // TODO : process tab selection event.
                int pos = tab.getPosition();
                changeView(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        }) ;
        contact = (LinearLayout) findViewById(R.id.contact) ;
        gallery = (ConstraintLayout) findViewById(R.id.gallery) ;
        weather = (ConstraintLayout) findViewById(R.id.weather) ;


        //탭 1
        contact.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                hideKeyboard();
                return false;
            }
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, new LinearLayoutManager(this).getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));

        rv = (RecyclerView) findViewById(R.id.main_rv);
        rv.addItemDecoration(dividerItemDecoration);
        llm = new LinearLayoutManager(this);
        adapter = new RvAdapter(this, itemList);

        editText = (EditText)findViewById(R.id.edittext);
        editText.addTextChangedListener(this);

        Button add_Btn = (Button) findViewById(R.id.add_Btn);
        add_Btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);

        //탭 2
        bindGrid();

        //탭 3
        /*
        checkPermission();
        getWeatherData();*/
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        adapter.getFilter().filter(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    void hideKeyboard()
    {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void bindGrid(){
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int displayWidth = size.x;


        GridView gridView = (GridView) findViewById(R.id.grid_view);
        imageAdapter = new ImageAdapter(this, displayWidth);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                i.putExtra("id", position);
                startActivity(i);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                System.out.println("long click");
                PopupMenu popup = new PopupMenu(MainActivity.this, view);
                getMenuInflater().inflate(R.menu.list_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()){
                            case R.id.action_delete:
                                imageAdapter.deleteItem(position);
                                imageAdapter.notifyDataSetChanged();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });

                popup.show();
                return true;
            }
        });

        Button btn_insert = (Button)findViewById(R.id.btn_insert);
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*imageAdapter.addItem(R.drawable.image17);
                imageAdapter.notifyDataSetChanged();*/
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if(intent.resolveActivity(getApplicationContext().getPackageManager()) != null){
                    startActivityForResult(intent, PICK_IMAGE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == PICK_IMAGE){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = intent.getData();
                if (imageUri != null) {
                    imageAdapter.addItem((Object)imageUri);
                    imageAdapter.notifyDataSetChanged();

                }
            }
        }
    }
    public void checkPermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        final int PERMISSIONS_REQUEST_CODE = 100;
        String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


        //퍼미션 요청을 허용한 적 없는 경우
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {
            // 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
            Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
            ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);

        } else {
            // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
            // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
            ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }
    }

    public void getWeatherData(){
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        double longitude;
        double latitude;

        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            Location location = lm.getLastKnownLocation(locationProvider);

            longitude = location.getLongitude();
            latitude = location.getLatitude();

            TextView text1 = (TextView) findViewById(R.id.forecast1);
            text1.setText(Double.toString(longitude));
            TextView text2 = (TextView) findViewById(R.id.forecast2);
            text2.setText(Double.toString(latitude));
            TextView text3 = (TextView) findViewById(R.id.forecast3);


            String queryURL = "api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=260c05833f6d6608df17de1271ec4d50&cnt=5";
            text3.setText(queryURL);
            try{
                URL url = new URL(queryURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = new JSONObject(getStringFromInputStream(in));
            } catch(MalformedURLException e){
                System.err.println("Malformed URL");
                e.printStackTrace();
            } catch(JSONException e) {
                System.err.println("JSON parsing error");
                e.printStackTrace();
            } catch(IOException e){
                System.err.println("URL Connection failed");
                e.printStackTrace();
            }

        }
    }
    private static String getStringFromInputStream(InputStream is){
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try{
            br = new BufferedReader(new InputStreamReader(is));
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
    /*
    private WeatherData parseJSON(JSONObject json) throws JSONException {
        WeatherData w = new WeatherData();

    }*/
    public void changeView(int index) {
        switch (index) {
            case 0 :
                contact.setVisibility(View.VISIBLE) ;
                gallery.setVisibility(View.INVISIBLE) ;
                weather.setVisibility(View.INVISIBLE) ;
                break ;
            case 1 :
                contact.setVisibility(View.INVISIBLE) ;
                gallery.setVisibility(View.VISIBLE) ;
                weather.setVisibility(View.INVISIBLE) ;
                break ;
            case 2 :
                contact.setVisibility(View.INVISIBLE) ;
                gallery.setVisibility(View.INVISIBLE) ;
                weather.setVisibility(View.VISIBLE) ;
                break ;

        }
    }
}