package example.com.inclass09_group05;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    List<Messages> messagesList;
    Context mContext;

    public MessageAdapter(List<Messages> messagesList, Context mContext) {
        this.messagesList = messagesList;
        this.mContext = mContext;
    }

    @Override

    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messgae_layout, parent, false);
        MessageAdapter.ViewHolder viewHolder = new MessageAdapter.ViewHolder(mContext, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
        Messages m = messagesList.get(position);
        holder.msg_text.setText(m.getMessage());
        holder.msg_name.setText(m.getFname()+" "+m.getLname());
        holder.msg_time.setText(m.getTime() + " from now");

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView msg_text, msg_name, msg_time;
        public ViewHolder(Context context, View itemView) {
            super(itemView);
            msg_text = (TextView)itemView.findViewById(R.id.message_text);
            msg_name = (TextView)itemView.findViewById(R.id.message_text_name);
            msg_time = (TextView)itemView.findViewById(R.id.message_text_time);
        }
    }
}