package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SQLiteHandlerJobType;
import info.androidhive.loginandregistration.helper.SQLiteHandlerJobs;
import info.androidhive.loginandregistration.helper.SessionManager;

public class JobActivity extends Activity {
    private static final String TAG = JobActivity.class.getSimpleName();
    private TextView tvUser;
    private EditText etContractor, etStreet, etHourStart;
    private Button btnAddJob, btnBack;
    private Spinner spCity, spJobType;
    private SessionManager session;
    private ProgressDialog pDialog;

    private SQLiteHandler db;
    private SQLiteHandlerJobs db_job;
    private SQLiteHandlerJobType db_job_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        tvUser = (TextView) findViewById(R.id.tvUser);
        etContractor = (EditText) findViewById(R.id.etContractor);
        etStreet = (EditText) findViewById(R.id.etStreet);
        etHourStart = (EditText) findViewById(R.id.etHourStart);
        //etHourEnd = (EditText) findViewById(R.id.etHourEnd);
        btnAddJob = (Button) findViewById(R.id.btnAddJob);
        btnBack = (Button) findViewById(R.id.btnBack);

        spCity = (Spinner) findViewById(R.id.spCity);
        spJobType = (Spinner) findViewById(R.id.spJobType);

        String[] item_city = new String[]{"Arad", "Timisoara", "Brasov"};
        ArrayAdapter<String> adapter_city = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, item_city);
        spCity.setAdapter(adapter_city);

        String[] item_job = new String[]{"Type A", "Type B", "Type C"};
        ArrayAdapter<String> adapter_job = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, item_job);
        spJobType.setAdapter(adapter_job);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler users
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        String user_id = user.get("uid");
        tvUser.setText(user_id);

        // SQLite database handler jobs
        db_job = new SQLiteHandlerJobs(getApplicationContext());

        // check if usere is logged in
        //if (session.isLoggedIn()) {
        //    Intent intent = new Intent(JobActivity.this, MainActivity.class);
        //    startActivity(intent);
        //    finish();
        //}

        // Add Job Button Event
        btnAddJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contractor = etContractor.getText().toString().trim();
                String street = etStreet.getText().toString().trim();
                String city = spCity.getSelectedItem().toString().trim();
                String job_type = spJobType.getSelectedItem().toString().trim();
                String hour_start = etHourStart.getText().toString().trim();

                if (!contractor.isEmpty() && !street.isEmpty() && !city.isEmpty()
                        && !job_type.isEmpty() && !hour_start.isEmpty()) {
                    insertJob(contractor, street, city, job_type, hour_start);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
    }

    private void insertJob (final String contractor, final String street,
                            final String city, final String job_type,
                            final String hour_start) {
        // tag used to cancel the request
        String tag_string_req = "req_insert";

        pDialog.setMessage("Inserting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_ADD_JOB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Insert Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // job successfully stored in mysql
                        // now store the job in sqlite
                        String sessionid = jObj.getString("sesionid");

                        JSONObject job = jObj.getJSONObject("job");
                        String contractor = job.getString("contractor");
                        String street = job.getString("street");
                        String city = job.getString("city");
                        String job_type = job.getString("job_type");
                        String hour_start = job.getString("hour_start");

                        db_job.addJob(sessionid, contractor, city, street, job_type, hour_start);

                        Toast.makeText(getApplicationContext(), "Job succesfully inserted",
                                Toast.LENGTH_LONG).show();

                        // launch main activity
                        Intent intent = new Intent(JobActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //error occured in insertion. get errpr message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG, "Insertion Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(),
                        Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("contractor", contractor);
                params.put("street", street);
                params.put("city", city);
                params.put("job_type", job_type);
                params.put("hour_start", hour_start);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getJobType () {
        String tag_string_req = "req_data";

        StringRequest strReq = new StringRequest(Method.GET, AppConfig.URL_GET_JOB_TYPE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void goBack() {
        Intent intent = new Intent(JobActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}