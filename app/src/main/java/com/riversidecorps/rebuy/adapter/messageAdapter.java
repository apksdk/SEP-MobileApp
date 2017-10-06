package com.riversidecorps.rebuy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.riversidecorps.rebuy.R;
import com.riversidecorps.rebuy.models.Message;

import java.util.List;

/**
 * Created by yijun on 2017/10/2.
 */

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.ViewHolder> {

private Context context;
private List<Message> messageLists;
private FirebaseStorage mStorage = FirebaseStorage.getInstance();


public class ViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView buyer;
        TextView title;
        TextView datetime;
        private View view;


        public ViewHolder(final View view) {
                super(view);
                this.view = view;
                content = (TextView) view.findViewById(R.id.messagePreviewTV);
                buyer=view.findViewById(R.id.messageAuthorTV);
                title=view.findViewById(R.id.messageTitleTV);
                datetime=view.findViewById(R.id.messageDateTV);

        }
}


        public messageAdapter(Context context, List<Message> messageLists) {
                this.context = context;
                this.messageLists = messageLists;
        }

        @Override
        public ViewHolder onCreateViewHolder (ViewGroup viewGroup, int i) {
                return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {

                viewHolder.content.setText(messageLists.get(position).getContent());
                viewHolder.buyer.setText(messageLists.get(position).getBuyer());
                viewHolder.datetime.setText(messageLists.get(position).getDatetime());
                viewHolder.title.setText(messageLists.get(position).getTitle());

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                });
        }

        @Override
        public int getItemCount() {
                return messageLists.size();
        }
}