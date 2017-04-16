package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.helper.SQLiteHandler;

public class JobActivity extends Activity {

    private TextView tvUser;
    private Button btnBack;

    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        tvUser = (TextView) findViewById(R.id.tvUser);
        btnBack = (Button) findViewById(R.id.btnBack);

        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        String user_id = user.get("unique_id");

        tvUser.setText(user_id);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
    }

    private void goBack() {
        Intent intent = new Intent(JobActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
