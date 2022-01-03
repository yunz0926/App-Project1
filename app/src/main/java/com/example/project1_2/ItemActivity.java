package com.example.project1_2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemActivity extends AppCompatActivity {
    private Intent intent;
    private String name;
    private String name2;
    private String number;
    private String email;
    private String job;
    private TextView text_name, text_number, text_email, text_job;

    public static ArrayList<Item> itemList_temp = new ArrayList<>();

    private UserDatabaseHelper userDatabaseHelper;
    public static final String TABLE_NAME = "user";
    SQLiteDatabase database;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);

        userDatabaseHelper = UserDatabaseHelper.getInstance(MainActivity.MainActivity_context);
        database = userDatabaseHelper.getWritableDatabase();

        intent = getIntent();
        name = intent.getStringExtra("name");
        number = intent.getStringExtra("number");
        email = intent.getStringExtra("email");
        job = intent.getStringExtra("job");


        text_name = findViewById(R.id.item_detail_name);
        text_number = findViewById(R.id.item_detail_number);
        text_email = findViewById(R.id.item_detail_email);
        text_job = findViewById(R.id.item_detail_job);

        String content_number = text_number.getText().toString();
        String content_email = text_email.getText().toString();
        String content_job = text_job.getText().toString();

        content_number = content_number + number;
        content_email = content_email + email;
        content_job = content_job + job;

        SpannableString spannable_number = new SpannableString(content_number);
        SpannableString spannable_email = new SpannableString(content_email);
        SpannableString spannable_job = new SpannableString(content_job);

        String number_word = "number";
        String email_word = "email";
        String job_word = "job";

        int number_start = content_number.indexOf(number_word) + number_word.length();
        int number_end = content_number.length();

        int email_start = content_email.indexOf(email_word) + email_word.length();
        int email_end = content_email.length();

        int job_start = content_job.indexOf(job_word) + job_word.length();
        int job_end = content_job.length();

        spannable_number.setSpan(new ForegroundColorSpan(Color.parseColor("#1A77C8")), number_start, number_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_number.setSpan(new RelativeSizeSpan(0.8f), number_start, number_end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_email.setSpan(new ForegroundColorSpan(Color.parseColor("#1A77C8")), email_start, email_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_email.setSpan(new RelativeSizeSpan(0.8f), email_start, email_end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_job.setSpan(new ForegroundColorSpan(Color.parseColor("#1A77C8")), job_start, job_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_job.setSpan(new RelativeSizeSpan(0.8f), job_start, job_end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        text_name.setText(name);
        text_number.setText(spannable_number);
        text_email.setText(spannable_email);
        text_job.setText(spannable_job);

        Button openBtn = findViewById(R.id.open_Btn);
        openBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Button deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteData(name);
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Button call_Btn = findViewById(R.id.call_Btn);
        call_Btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+number));
                startActivity(intent);
            }
        });
    }

    /*
    private void deleteData(String name) {
        String sql = "DELETE FROM user name=" + "'" + name + "'";
        database.execSQL(sql);
    }

     */

    private void deleteData(String name) {
        String sql = "SELECT * FROM " + "user";
        Cursor cursor = database.rawQuery(sql, null);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            String name_temp = cursor.getString(0);
            String number_temp = cursor.getString(1);
            String email_temp = cursor.getString(2);
            String job_temp = cursor.getString(3);

            if (!name.equals(name_temp)) {
                itemList_temp.add(new Item(name_temp, number_temp, email_temp, job_temp));
            }
        }

        database.execSQL("DELETE FROM "+"user");

        for (Item item : itemList_temp) {
            String sql2 = "INSERT INTO user VALUES(" + "'" +  item.getItem_name() + "'" + ", " + "'" + item.getItem_number() + "'" + ", " + "'" + item.getItem_email() + "'" + ", " + "'" + item.getItem_job() + "'" + ")";
            database.execSQL(sql2);
        }
        itemList_temp.clear();
    }

}
