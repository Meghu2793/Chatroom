package example.com.inclass09_group05;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageThreads extends AppCompatActivity {

    static ArrayList<User> threads = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    EditText editText;
    User user;
    static String restoredText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_threads);
        getSupportActionBar().setTitle("Message Threads");

        SharedPreferences prefs = getSharedPreferences(RegisterActivity.PREFS_NAME, MODE_PRIVATE);
        restoredText = prefs.getString("token", null);
        Log.d("demo", "RestoredText " + restoredText);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewThreads);
        editText = (EditText) findViewById(R.id.editTextNewThread);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(MessageThreads.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);

        new GetMesssageThread().execute(restoredText);

        findViewById(R.id.imageButtonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SendMessageThread().execute(restoredText, editText.getText().toString());
            }
        });

    }

    private class GetMesssageThread extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject obj = new JSONObject(s);
                Log.d("demo", "JSONArray Object " + obj.toString());
                JSONArray jsonArray = obj.getJSONArray("threads");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj1 = jsonArray.getJSONObject(i);
                    user = new User();
                    Log.d("demo", "JSONArray " + obj1.toString());
                    user.setTitle(obj1.getString("title").toString());
                    user.setId(obj1.getString("id").toString());

                    threads.add(user);
                    Log.d("demo", "List " + threads.toString());
                }

                mAdapter = new MessageThreadsAdapter(threads, MessageThreads.this);
                mRecyclerView.setAdapter(mAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread")
                    .header("Authorization", "BEARER " + params[0])
                    .build();

            String res = null;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("demo", "Response Threads " + res.toString());
            return res;
        }
    }

    private class SendMessageThread extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
                JSONObject obj1 = obj.getJSONObject("thread");
                user = new User();
                user.setId(obj1.getString("id"));
                user.setTitle(obj1.getString("title"));
                Log.d("demo", "ID " + user.getId().toString());
                Log.d("demo", "Title " + user.getTitle().toString());
                user.setMyMessage(true);
                threads.add(user);

                mAdapter = new MessageThreadsAdapter(threads, MessageThreads.this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            RequestBody formBody = new FormBody.Builder()
                    .add("title", params[1])
                    .build();

            Request request = new Request.Builder()
                    .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread/add")
                    .header("Authorization", "BEARER " + params[0])
                    .post(formBody)
                    .build();

            String res = null;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("demo", "Response Threads after adding " + res.toString());
            return res;
        }
    }


    private class DeleteThread extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread/delete")
                    .header("Authorization", "BEARER " + params[0])
                    .addHeader("id",params[1])
                    .build();

            String res = null;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("demo", "Response Threads after Deletion" + res.toString());
            return res;
        }
    }
}