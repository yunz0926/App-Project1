package com.example.project1_2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.appcompat.app.ActionBar;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements TextWatcher {
    private UserDatabaseHelper userDatabaseHelper;
    public static final String TABLE_NAME = "user";
    SQLiteDatabase database;

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
    private int temp;
    private int time = 0;
    private int current_time;
    private String weatherMain;

    private String date_format;
    private String time_format;
    private String date;

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");


    public static Context MainActivity_context;

    LinearLayout contact;
    ConstraintLayout gallery;
    ConstraintLayout weather;
    LinearLayout weather_content;

    private TextView time_text, description;
    private ImageView cap, left_glove, right_glove, cloth;
    private ImageView current_weathericon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        MainActivity_context = getApplicationContext();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
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
        });
        contact = (LinearLayout) findViewById(R.id.contact);
        gallery = (ConstraintLayout) findViewById(R.id.gallery);
        weather = (ConstraintLayout) findViewById(R.id.weather);
        weather_content = (LinearLayout) findViewById(R.id.weather_content);


        //탭 1
        contact.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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

        editText = (EditText) findViewById(R.id.edittext);
        editText.addTextChangedListener(this);

        Button add_Btn = (Button) findViewById(R.id.add_Btn);
        add_Btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        userDatabaseHelper = UserDatabaseHelper.getInstance(this);
        database = userDatabaseHelper.getWritableDatabase();

        itemList.clear();
        selectData(TABLE_NAME);
        adapter.notifyDataSetChanged();

        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);

        //탭 2
        bindGrid();

        //탭 3

        checkPermission();
        WeatherActivity weatherActivity = new WeatherActivity();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Thread mThread = new Thread() {
            public void run() {
                weatherList = weatherActivity.getWeatherData(MainActivity.this, lm);
                GETDATA = true;

            }
        };
        mThread.start();

        date_format = getTime();
        String[] date_split = date_format.split(" ");

        time_format = date_split[1];
        String[] time_split = time_format.split(":");
        current_time = Integer.parseInt(time_split[0]);

        time_text = findViewById(R.id.time);
        cap = (ImageView) findViewById(R.id.cap);
        left_glove = (ImageView) findViewById(R.id.left_glove);
        right_glove = (ImageView) findViewById(R.id.right_glove);
        cloth = (ImageView) findViewById(R.id.cloth);
        description = (TextView) findViewById(R.id.description);

        while (!GETDATA) {
            try{
                mThread.sleep(10);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        };

        int temp1 = weatherList.get(0).getTemp();
        int temp2 = weatherList.get(1).getTemp();
        int temp3 = weatherList.get(2).getTemp();

        temp = Math.min(Math.min(temp1, temp2), temp3);

        time_text.setText("Today\n" + (current_time) + "H");
        description.setText(weatherList.get(0).getTemp() + "\u2103\n" + weatherList.get(0).getWeather());

        if (temp < 0) {
            cap.setVisibility(View.VISIBLE);
            left_glove.setVisibility(View.VISIBLE);
            right_glove.setVisibility(View.VISIBLE);
            cloth.setVisibility(View.VISIBLE);

            cap.setImageResource(R.drawable.cap);
            left_glove.setImageResource(R.drawable.left_glove);
            right_glove.setImageResource(R.drawable.right_glove);
            cloth.setImageResource(R.drawable.paka);
        } else if (temp >= 0 && temp < 5) {
            cap.setVisibility(View.VISIBLE);
            left_glove.setVisibility(View.INVISIBLE);
            right_glove.setVisibility(View.INVISIBLE);
            cloth.setVisibility(View.VISIBLE);

            cap.setImageResource(R.drawable.white);
            cloth.setImageResource(R.drawable.paka);
        } else if (temp >= 5 && temp < 9) {
            cap.setVisibility(View.INVISIBLE);
            left_glove.setVisibility(View.INVISIBLE);
            right_glove.setVisibility(View.INVISIBLE);
            cloth.setVisibility(View.VISIBLE);

            cloth.setImageResource(R.drawable.c5_8);
        } else if (temp >= 9 && temp < 12) {
            cap.setVisibility(View.INVISIBLE);
            left_glove.setVisibility(View.INVISIBLE);
            right_glove.setVisibility(View.INVISIBLE);
            cloth.setVisibility(View.VISIBLE);

            cloth.setImageResource(R.drawable.c9_11);
        } else if (temp >= 12 && temp < 17) {
            cap.setVisibility(View.INVISIBLE);
            left_glove.setVisibility(View.INVISIBLE);
            right_glove.setVisibility(View.INVISIBLE);
            cloth.setVisibility(View.VISIBLE);

            cloth.setImageResource(R.drawable.c12_16);
        } else if (temp >= 17 && temp < 20) {
            cap.setVisibility(View.INVISIBLE);
            left_glove.setVisibility(View.INVISIBLE);
            right_glove.setVisibility(View.INVISIBLE);
            cloth.setVisibility(View.VISIBLE);

            cloth.setImageResource(R.drawable.c17_19);
        } else if (temp >= 20 && temp < 23) {
            cap.setVisibility(View.INVISIBLE);
            left_glove.setVisibility(View.INVISIBLE);
            right_glove.setVisibility(View.INVISIBLE);
            cloth.setVisibility(View.VISIBLE);

            cloth.setImageResource(R.drawable.c20_22);
        } else if (temp >= 23 && temp < 28) {
            cap.setVisibility(View.INVISIBLE);
            left_glove.setVisibility(View.INVISIBLE);
            right_glove.setVisibility(View.INVISIBLE);
            cloth.setVisibility(View.VISIBLE);

            cloth.setImageResource(R.drawable.c23_27);
        } else if (temp >= 28) {
            cap.setVisibility(View.INVISIBLE);
            left_glove.setVisibility(View.INVISIBLE);
            right_glove.setVisibility(View.INVISIBLE);
            cloth.setVisibility(View.VISIBLE);

            cloth.setImageResource(R.drawable.c_28);
        }

        TabLayout weather_tabLayout = (TabLayout) findViewById(R.id.weather_tab);
        weather_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // TODO : process tab selection event.
                int pos = tab.getPosition();
                while (!GETDATA) {
                    try{
                        mThread.sleep(10);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                };
                time = pos * 3;
                int temp1 = weatherList.get(time).getTemp();
                int temp2 = weatherList.get(time + 1).getTemp();
                int temp3 = weatherList.get(time + 2).getTemp();

                temp = Math.min(Math.min(temp1, temp2), temp3);

                if(current_time + time >= 24){
                    time_text.setText("Tomorrow\n" + (current_time + time - 24) + "H");
                }
                else{
                    time_text.setText("Today\n" + (current_time + time) + "H");
                }
                if (weatherList.get(time).getWeather() == "Rain") {
                    description.setText(weatherList.get(time).getTemp() + "\u2103\n" + weatherList.get(time).getWeather());
                } else {
                    description.setText(weatherList.get(time).getTemp() + "\u2103\n" + weatherList.get(time).getWeather());
                }

                if (temp < 0) {
                    cap.setVisibility(View.VISIBLE);
                    left_glove.setVisibility(View.VISIBLE);
                    right_glove.setVisibility(View.VISIBLE);
                    cloth.setVisibility(View.VISIBLE);

                    cap.setImageResource(R.drawable.cap);
                    left_glove.setImageResource(R.drawable.left_glove);
                    right_glove.setImageResource(R.drawable.right_glove);
                    cloth.setImageResource(R.drawable.paka);
                } else if (temp >= 0 && temp < 5) {
                    cap.setVisibility(View.VISIBLE);
                    left_glove.setVisibility(View.INVISIBLE);
                    right_glove.setVisibility(View.INVISIBLE);
                    cloth.setVisibility(View.VISIBLE);

                    cap.setImageResource(R.drawable.white);
                    cloth.setImageResource(R.drawable.paka);
                } else if (temp >= 5 && temp < 9) {
                    cap.setVisibility(View.INVISIBLE);
                    left_glove.setVisibility(View.INVISIBLE);
                    right_glove.setVisibility(View.INVISIBLE);
                    cloth.setVisibility(View.VISIBLE);

                    cloth.setImageResource(R.drawable.c5_8);
                } else if (temp >= 9 && temp < 12) {
                    cap.setVisibility(View.INVISIBLE);
                    left_glove.setVisibility(View.INVISIBLE);
                    right_glove.setVisibility(View.INVISIBLE);
                    cloth.setVisibility(View.VISIBLE);

                    cloth.setImageResource(R.drawable.c9_11);
                } else if (temp >= 12 && temp < 17) {
                    cap.setVisibility(View.INVISIBLE);
                    left_glove.setVisibility(View.INVISIBLE);
                    right_glove.setVisibility(View.INVISIBLE);
                    cloth.setVisibility(View.VISIBLE);

                    cloth.setImageResource(R.drawable.c12_16);
                } else if (temp >= 17 && temp < 20) {
                    cap.setVisibility(View.INVISIBLE);
                    left_glove.setVisibility(View.INVISIBLE);
                    right_glove.setVisibility(View.INVISIBLE);
                    cloth.setVisibility(View.VISIBLE);

                    cloth.setImageResource(R.drawable.c17_19);
                } else if (temp >= 20 && temp < 23) {
                    cap.setVisibility(View.INVISIBLE);
                    left_glove.setVisibility(View.INVISIBLE);
                    right_glove.setVisibility(View.INVISIBLE);
                    cloth.setVisibility(View.VISIBLE);

                    cloth.setImageResource(R.drawable.c20_22);
                } else if (temp >= 23 && temp < 28) {
                    cap.setVisibility(View.INVISIBLE);
                    left_glove.setVisibility(View.INVISIBLE);
                    right_glove.setVisibility(View.INVISIBLE);
                    cloth.setVisibility(View.VISIBLE);

                    cloth.setImageResource(R.drawable.c23_27);
                } else if (temp >= 28) {
                    cap.setVisibility(View.INVISIBLE);
                    left_glove.setVisibility(View.INVISIBLE);
                    right_glove.setVisibility(View.INVISIBLE);
                    cloth.setVisibility(View.VISIBLE);

                    cloth.setImageResource(R.drawable.c_28);
                }


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
    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
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

    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void bindGrid() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
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
                        switch (menuItem.getItemId()) {
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

        Button btn_insert = (Button) findViewById(R.id.btn_insert);
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*imageAdapter.addItem(R.drawable.image17);
                imageAdapter.notifyDataSetChanged();*/
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivityForResult(intent, PICK_IMAGE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = intent.getData();
                if (imageUri != null) {
                    imageAdapter.addItem((Object) imageUri);
                    imageAdapter.notifyDataSetChanged();

                }
            }
        }
    }

    public void checkPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        final int PERMISSIONS_REQUEST_CODE = 100;
        String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


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
            case 0:
                contact.setVisibility(View.VISIBLE);
                gallery.setVisibility(View.INVISIBLE);
                weather.setVisibility(View.INVISIBLE);
                break;
            case 1:
                contact.setVisibility(View.INVISIBLE);
                gallery.setVisibility(View.VISIBLE);
                weather.setVisibility(View.INVISIBLE);
                break;
            case 2:
                while (!GETDATA) {
                };
                contact.setVisibility(View.INVISIBLE);
                gallery.setVisibility(View.INVISIBLE);
                weather.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void selectData(String tableName) {
        if (database != null) {
            String sql = "SELECT * FROM " + tableName;
            Cursor cursor = database.rawQuery(sql, null);

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                String email = cursor.getString(2);
                String job = cursor.getString(3);
                itemList.add(new Item(name, number, email, job));
            }
            cursor.close();
        }
    }
}
