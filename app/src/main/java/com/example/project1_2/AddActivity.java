package com.example.project1_2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    private String name;
    private String number;
    private String email;
    private String job;

    private UserDatabaseHelper userDatabaseHelper;
    public static final String TABLE_NAME = "user";
    SQLiteDatabase database;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        userDatabaseHelper = UserDatabaseHelper.getInstance(MainActivity.MainActivity_context);
        database = userDatabaseHelper.getWritableDatabase();

        EditText add_name = (EditText) findViewById(R.id.add_name);
        EditText add_number = (EditText) findViewById(R.id.add_number);
        EditText add_email = (EditText) findViewById(R.id.add_email);
        EditText add_job = (EditText) findViewById(R.id.add_job);

        Button add_item_add_Btn = (Button) findViewById(R.id.add_item_add_Btn);
        add_item_add_Btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                name = add_name.getText().toString();
                number = (String) add_number.getText().toString();
                email = add_email.getText().toString();
                job = add_job.getText().toString();
                if (name.equals("")) {
                    insertData("'" + number + "'", "'" + number + "'", "'" + email + "'", "'" + job + "'");
                } else {
                    insertData("'" + name + "'", "'" + number + "'", "'" + email + "'", "'" + job + "'");
                }
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(v.getContext(), "added", Toast.LENGTH_SHORT).show();
            }
        });

        Button add_item_back_Btn = (Button) findViewById(R.id.add_item_back_Btn);
        add_item_back_Btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(v.getContext(), "go back", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void insertData(String name, String number, String email, String job) {
        if (database != null) {
            String sql = "INSERT INTO user VALUES(" + name + ", " + number + ", " + email + ", " + job + ")";
            database.execSQL(sql);
        }
    }
}
