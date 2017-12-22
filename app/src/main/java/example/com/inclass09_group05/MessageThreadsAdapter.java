package example.com.inclass09_group05;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.content.Context.MODE_PRIVATE;


public class MessageThreadsAdapter extends RecyclerView.Adapter<MessageThreadsAdapter.ViewHolder> {
    ArrayList<User> list;
    Context mContext;
    static String restoredText;

    public MessageThreadsAdapter(ArrayList<User> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public MessageThreadsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thread_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(mContext, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageThreadsAdapter.ViewHolder holder, int position) {
        SharedPreferences prefs = mContext.getSharedPreferences(RegisterActivity.PREFS_NAME, MODE_PRIVATE);
        restoredText = prefs.getString("token", null);

        final User user = list.get(position);
        holder.textView.setText(user.getTitle());
        holder.user = user;
        if (user.getMyMessage()) {
            holder.imageButtonRemove.setVisibility(View.VISIBLE);
        }

        holder.imageButtonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(user);
                notifyDataSetChanged();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://ec2-54-164-74-55.compute-1.amazonaws.com/api/thread/delete/"+user.getId())
                        .header("Authorization", "BEARER " + restoredText)
                        .build();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageButton imageButtonRemove;
        User user;

        public ViewHolder(final Context context, View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textViewThreads);
            imageButtonRemove = (ImageButton) itemView.findViewById(R.id.imageButtonRemove10);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chatIntent = new Intent(context, Chatroom.class);
                    chatIntent.putExtra("chat", user);
                    context.startActivity(chatIntent);
                }
            });
        }
    }
}
