package example.com.inclass09_group05;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Message;
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
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chatroom extends AppCompatActivity {

    User user;
    static List<Messages> messages = new ArrayList<>();
    static String restoredText;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    Messages message;
    EditText editTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        getSupportActionBar().setTitle("Chatroom");

        SharedPreferences prefs = getSharedPreferences(RegisterActivity.PREFS_NAME, MODE_PRIVATE);
        restoredText = prefs.getString("token", null);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_message_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(getIntent().getExtras().containsKey("chat")){
            user = (User) getIntent().getExtras().getSerializable("chat");
        }
        new GetMessage().execute(restoredText, user.getId());

        //Log.d("demo","User ID "+ user.getId().toString());

        findViewById(R.id.imageButtonSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextMessage = (EditText) findViewById(R.id.sendMessage);
                new PostMessage().execute(editTextMessage.getText().toString(),user.getId().toString());
            }
        });



    }

     class GetMessage extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("messages");
                for(int i =0;i<jsonArray.length();i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    message = new Messages();
                    message.setMessage(jsonObject1.getString("message"));
                    message.setMessage_id(jsonObject1.getString("id"));
                    message.setFname(jsonObject1.getString("user_fname"));
                    message.setLname(jsonObject1.getString("user_lname"));
                    message.setTime(jsonObject1.getString("created_at"));

                    Log.d("Demo ","Message in chat room "+message.toString());

                    messages.add(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mAdapter = new MessageAdapter(messages, Chatroom.this);
            mRecyclerView.setAdapter(mAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/messages/"+strings[1])
                    .header("Authorization", "BEARER " + strings[0])
                    .build();

            String res = null;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("demo", "Response Threads Get messages " + res.toString());
            return res;
        }
    }

    private class PostMessage extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject obj = jsonObject.getJSONObject("message");
                message = new Messages();
                message.setMessage_id(obj.getString("id"));
                message.setMessage(obj.getString("message"));
                messages.add(message);

                mAdapter = new MessageAdapter(messages, Chatroom.this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("message", params[0])
                    .add("id", params[1])
                    .build();

            Request request = new Request.Builder()
                    .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/message/add")
                    .header("Authorization", "BEARER " +restoredText)
                    .post(formBody)
                    .build();

            String res = null;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("demo", "Response Threads after messaginh in chat " + res.toString());
            return res;
        }

    }
}