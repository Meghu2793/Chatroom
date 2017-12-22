package example.com.inclass09_group05;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Chat Room");

        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
                String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
                new GetUserDetails().execute(email, password);
            }
        });

        findViewById(R.id.buttonSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public class GetUserDetails extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
                String data = obj.getString("token").toString();
                Log.d("demo", "Login "+data.toString());
                if (data != null && !data.isEmpty()) {
                    SharedPreferences.Editor editor = getSharedPreferences(RegisterActivity.PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("token", data);
                    editor.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(MainActivity.this, MessageThreads.class);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("email", params[0])
                    .add("password", params[1])
                    .build();

            Request request = new Request.Builder()
                    .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/login").post(formBody)
                    .build();

            String res = null;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }
    }
}

