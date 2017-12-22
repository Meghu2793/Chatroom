package example.com.inclass09_group05;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    static SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Sign Up");

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final String restoredText = prefs.getString("token", null);

        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname = ((EditText) findViewById(R.id.editTextFirstName)).getText().toString();
                String lname = ((EditText) findViewById(R.id.editTextLastName)).getText().toString();
                String username = ((EditText) findViewById(R.id.editTextRegUserName)).getText().toString();
                String password1 = ((EditText) findViewById(R.id.editTextRegPwd1)).getText().toString();
                String password2 = ((EditText) findViewById(R.id.editTextRegPwd2)).getText().toString();

                if (!password1.equalsIgnoreCase(password2)) {
                    Toast.makeText(RegisterActivity.this, "Passwords does not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                new GetRegisterDetails().execute(fname, lname, username, password1);
            }
        });

        findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });
    }

    public class GetRegisterDetails extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            Log.d("demo", "token " + s.toString());
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
//                Log.d("demo", obj.getString("token").toString());
                String data = obj.getString("token").toString();
                if (data != null && !data.isEmpty()) {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("token", data);
                    editor.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(RegisterActivity.this, MessageThreads.class);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("fname", params[0])
                    .add("lname", params[1])
                    .add("email", params[2])
                    .add("password", params[3])
                    .build();
            Request request = new Request.Builder()
                    .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/signup")
                    .post(formBody)
                    .build();

            String res = null;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                res = response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("demo", "response body " + res.toString());
            return res;
        }
    }
}
