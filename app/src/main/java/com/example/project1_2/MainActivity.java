package com.example.project1_2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements TextWatcher {
    private final int PICK_IMAGE = 1111;
    private ImageAdapter imageAdapter;
    private RecyclerView rv;
    private LinearLayoutManager llm;
    private int i = 0;
    private EditText editText;
    private RvAdapter adapter;
    public static ArrayList<Item> itemList = new ArrayList<>();
    public static ArrayList<WeatherData> weatherList;
    private boolean GETDATA = false;
    private int current_temp = -5;
    private int three_temp = -7;
    private int six_temp = -7;
    private int nine_temp = -5;
    private int twelve_temp = 0;
    private int fifteen_temp = 4;
    private int eighteen_temp = 1;
    private int twenty_two_temp = -4;
    private int temp = current_temp;
    private int current_time = 23;
    private int time = 0;


    LinearLayout contact;
    ConstraintLayout gallery;
    ConstraintLayout weather;
    LinearLayout current;
    LinearLayout three;
    LinearLayout six;

    private TextView current_time_text, three_time_text, six_time_text;
    private ImageView current_cap, three_cap, six_cap;
    private ImageView current_left_glove, current_right_glove;
    private ImageView three_left_glove, three_right_glove;
    private ImageView six_left_glove, six_right_glove;
    private ImageView current_cloth, three_cloth, six_cloth;
    private TextView current_description;
    private ImageView current_weathericon;

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
        current = (LinearLayout) findViewById(R.id.current) ;
        three = (LinearLayout) findViewById(R.id.three) ;
        six = (LinearLayout) findViewById(R.id.six) ;


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

        checkPermission();
        WeatherActivity weatherActivity = new WeatherActivity();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        new Thread(){
            public void run(){
                weatherList = weatherActivity.getWeatherData(MainActivity.this, lm);
                GETDATA = true;

            }
        }.start();

        current_time_text = findViewById(R.id.current_time);
        three_time_text = findViewById(R.id.three_time);
        six_time_text = findViewById(R.id.six_time);

        current_cap = (ImageView) findViewById(R.id.current_cap);
        three_cap = (ImageView) findViewById(R.id.three_cap);
        six_cap = (ImageView) findViewById(R.id.six_cap);

        current_left_glove = (ImageView) findViewById(R.id.current_left_glove);
        current_right_glove = (ImageView) findViewById(R.id.current_right_glove);
        three_left_glove = (ImageView) findViewById(R.id.three_left_glove);
        three_right_glove = (ImageView) findViewById(R.id.three_right_glove);
        six_left_glove = (ImageView) findViewById(R.id.six_left_glove);
        six_right_glove = (ImageView) findViewById(R.id.six_right_glove);

        current_cloth = (ImageView) findViewById(R.id.current_cloth);
        three_cloth = (ImageView) findViewById(R.id.three_cloth);
        six_cloth = (ImageView) findViewById(R.id.six_cloth);

        /*
        current_weathericon = (ImageView) findViewById(R.id.current_weathericon);

         */
        current_description = (TextView) findViewById(R.id.current_description);


        current_time_text.setText(""+current_time);

        while(!GETDATA){

        };

        if(weatherList.get(0).getWeather() == "Rain"){
            current_description.setText("해당 시간대의 기온은 " + weatherList.get(0).getTemp() + "도입니다.\n비가 올 확률이 높으니 우산을 꼭 챙기세요! ");
        }
        else {
            current_description.setText("해당 시간대의 기온은 " + weatherList.get(0).getTemp() + "도입니다.\n비가 올 확률이 높으니 우산을 꼭 챙기세요! ");
        }

        String resName = "@drawable/w" + weatherList.get(0).getIconUrl();
        System.out.println("resName: " + resName);
        int resId = getResources().getIdentifier(resName, "drawable", this.getPackageName());
        /*current_weathericon.setImageResource(resId);*/

        if (temp < -5) {
            current_cap.setVisibility(View.VISIBLE);
            current_left_glove.setVisibility(View.VISIBLE);
            current_right_glove.setVisibility(View.VISIBLE);
            current_cloth.setVisibility(View.VISIBLE);

            current_cap.setImageResource(R.drawable.cap);
            current_left_glove.setImageResource(R.drawable.left_glove);
            current_right_glove.setImageResource(R.drawable.right_glove);
            current_cloth.setImageResource(R.drawable.paka);
        }
        else if (temp >= -5 && temp < 0) {
            current_cap.setVisibility(View.VISIBLE);
            current_left_glove.setVisibility(View.INVISIBLE);
            current_right_glove.setVisibility(View.INVISIBLE);
            current_cloth.setVisibility(View.VISIBLE);

            current_cap.setImageResource(R.drawable.white);
            current_cloth.setImageResource(R.drawable.paka);
        }
        else if (temp > 0 && temp < 4) {
            ;
        }

        TabLayout weather_tabLayout = (TabLayout) findViewById(R.id.weather_tab);
        weather_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // TODO : process tab selection event.
                int pos = tab.getPosition();
                changeView2(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        });
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

    public void changeView2(int index) {
        switch (index) {
            case 0 :
                temp = current_temp;
                time = current_time;
                current_time_text.setText(""+time);
                current.setVisibility(View.VISIBLE);
                three.setVisibility(View.INVISIBLE) ;
                six.setVisibility(View.INVISIBLE) ;
                if (temp < -5) {
                    current_cap.setVisibility(View.VISIBLE);
                    current_left_glove.setVisibility(View.VISIBLE);
                    current_right_glove.setVisibility(View.VISIBLE);
                    current_cloth.setVisibility(View.VISIBLE);

                    current_cap.setImageResource(R.drawable.cap);
                    current_left_glove.setImageResource(R.drawable.left_glove);
                    current_right_glove.setImageResource(R.drawable.right_glove);
                    current_cloth.setImageResource(R.drawable.paka);
                }
                else if (temp > -5 && temp < 0) {
                    current_cap.setVisibility(View.INVISIBLE);
                    current_left_glove.setVisibility(View.INVISIBLE);
                    current_right_glove.setVisibility(View.INVISIBLE);
                    current_cloth.setVisibility(View.VISIBLE);

                    current_cloth.setImageResource(R.drawable.paka);
                }
                else if (temp > 0 && temp < 4) {
                    current_left_glove.setVisibility(View.INVISIBLE);
                    current_right_glove.setVisibility(View.INVISIBLE);
                }
                break ;
            case 1 :
                temp = three_temp;
                time = current_time+3;
                if (time > 24) time -= 24;
                current.setVisibility(View.INVISIBLE);
                three.setVisibility(View.VISIBLE) ;
                six.setVisibility(View.INVISIBLE) ;
                three_time_text.setText(""+time);
                if (temp < -5) {
                    three_cap.setVisibility(View.VISIBLE);
                    three_left_glove.setVisibility(View.VISIBLE);
                    three_right_glove.setVisibility(View.VISIBLE);
                    three_cloth.setVisibility(View.VISIBLE);

                    three_cap.setImageResource(R.drawable.cap);
                    three_left_glove.setImageResource(R.drawable.left_glove);
                    three_right_glove.setImageResource(R.drawable.right_glove);
                    three_cloth.setImageResource(R.drawable.paka);                }
                else if (temp >= -5 && temp < 0) {
                    three_left_glove.setVisibility(View.INVISIBLE);
                    three_right_glove.setVisibility(View.INVISIBLE);
                    three_cloth.setVisibility(View.VISIBLE);
                    three_cloth.setImageResource(R.drawable.paka);
                }
                else if (temp >= 0 && temp < 4) {
                    three_left_glove.setVisibility(View.INVISIBLE);
                    three_right_glove.setVisibility(View.INVISIBLE);
                }
                break ;
            case 2:
                temp = six_temp;
                time = current_time+6;
                if (time > 24) time -= 24;
                current.setVisibility(View.INVISIBLE);
                three.setVisibility(View.INVISIBLE) ;
                six.setVisibility(View.VISIBLE) ;
                six_time_text.setText(""+time);
                if (temp < -5) {
                    six_cap.setVisibility(View.VISIBLE);
                    six_left_glove.setVisibility(View.VISIBLE);
                    six_right_glove.setVisibility(View.VISIBLE);
                    six_cloth.setVisibility(View.VISIBLE);

                    six_cap.setImageResource(R.drawable.cap);
                    six_left_glove.setImageResource(R.drawable.left_glove);
                    six_right_glove.setImageResource(R.drawable.right_glove);
                    six_cloth.setImageResource(R.drawable.paka);
                }
                else if (temp >= -5 && temp < 0) {
                    six_left_glove.setVisibility(View.INVISIBLE);
                    six_right_glove.setVisibility(View.INVISIBLE);
                    six_cloth.setVisibility(View.VISIBLE);
                    six_cloth.setImageResource(R.drawable.paka);
                }
                else if (temp >= 0 && temp < 4) {
                    six_left_glove.setVisibility(View.INVISIBLE);
                    six_right_glove.setVisibility(View.INVISIBLE);
                }
                break ;
        }
    }
}