package com.riversidecorps.rebuy.adapter;

/**
 * Created by tom on 2017/10/8.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.riversidecorps.rebuy.R;
import com.riversidecorps.rebuy.SlidingMenu;
import com.riversidecorps.rebuy.models.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * created by yhao on 2017/8/18.
 */


public class messageAdapter extends RecyclerView.Adapter<messageAdapter.MyViewHolder> {


    private List<Message> messageLists;
    private Context mContext;

    private SlidingMenu mOpenMenu;
    private SlidingMenu mScrollingMenu;

    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser myFirebaseUser;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mdatabaseReference;
    private ProgressDialog progressDialog;
    private String userName;
    public SlidingMenu getScrollingMenu() {
        return mScrollingMenu;
    }

    public void setScrollingMenu(SlidingMenu scrollingMenu) {
        mScrollingMenu = scrollingMenu;
    }

    public void holdOpenMenu(SlidingMenu slidingMenu) {
        mOpenMenu = slidingMenu;
    }

    public void closeOpenMenu() {
        if (mOpenMenu != null && mOpenMenu.isOpen()) {
            mOpenMenu.closeMenu();
            mOpenMenu = null;
        }
    }

    public messageAdapter(List<Message> data, Context context) {
        myFirebaseAuth = FirebaseAuth.getInstance();
        myFirebaseUser = myFirebaseAuth.getCurrentUser();
        mdatabaseReference = FirebaseDatabase.getInstance().getReference();
        messageLists = data;
        mContext = context;
        progressDialog=new ProgressDialog(mContext);
        DatabaseReference userRef = mDatabase.getReference().child("users").child(myFirebaseUser.getUid()).child("username");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.message, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        TextView menuText;
        ImageView imageView;
        RelativeLayout content;
        SlidingMenu slidingMenu;

        holder.messagePreviewTV.setText(messageLists.get(position).getContent());
        holder.messageAuthorTV.setText(messageLists.get(position).getSender());
        holder.messageDateTV.setText(messageLists.get(position).getDatetime());
        holder.messageTitleTV.setText(messageLists.get(position).getTitle());
        holder.delete_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {closeOpenMenu();
                String userId = myFirebaseUser.getUid();
                String message_id=messageLists.get(position).getMessage_id();
                mdatabaseReference.child("users").child(userId).child("messages").child(message_id).removeValue();

                boolean top=true;
                if (mOnClickListener != null) {
                    mOnClickListener.onMenuClick(position, top);
                }
            }
        });


        holder.menuText.setText("Reply");
        holder.menuText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOpenMenu();
                boolean top=true;
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Message");
                builder.setIcon(R.drawable.ic_message_dialog);

                final EditText input = new EditText(mContext);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage(mContext.getString(R.string.creating_listing_message));
                        progressDialog.show();
                        String datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(new Date());
                        String message = input.getText().toString();
                        String sender_id = messageLists.get(position).getSender_id();
                        String itemName = messageLists.get(position).getTitle();
                        final String messageID = mdatabaseReference.child("users").child(sender_id).child("messages").push().getKey();
                        mdatabaseReference.child("users").child(sender_id).child("messages").child(messageID).child("content").setValue(message);
                        mdatabaseReference.child("users").child(sender_id).child("messages").child(messageID).child("title").setValue(itemName);
                        mdatabaseReference.child("users").child(sender_id).child("messages").child(messageID).child("sender").setValue(userName);
                        mdatabaseReference.child("users").child(sender_id).child("messages").child(messageID).child("sender_id").setValue(sender_id);
                        mdatabaseReference.child("users").child(sender_id).child("messages").child(messageID).child("message_id").setValue(messageID);
                        mdatabaseReference.child("users").child(sender_id).child("messages").child(messageID).child("datetime").setValue(datetime);
                        progressDialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                if (mOnClickListener != null) {
                    mOnClickListener.onMenuClick(position, top);
                }
            }
        });
        holder.slidingMenu.setCustomOnClickListener(new SlidingMenu.CustomOnClickListener() {
            @Override
            public void onClick() {
                if (mOnClickListener != null) {
                    mOnClickListener.onContentClick(position);
                }
            }
        });

    }

    public interface OnClickListener {
        void onMenuClick(int position, boolean top);

        void onContentClick(int position);
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }


    @Override
    public int getItemCount() {
        return messageLists.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView menuText;
        TextView delete_message;
        ImageView imageView;
    //    RelativeLayout content;
        SlidingMenu slidingMenu;
        TextView messageTitleTV_Pre;
        TextView messageTitleTV;

        TextView messagePreviewTV_Pre;
        TextView messagePreviewTV;

        TextView messageAuthorTV_Pre;
        TextView messageAuthorTV;

        TextView messageDateTV;


        MyViewHolder(View itemView) {
            super(itemView);
            menuText = (TextView) itemView.findViewById(R.id.menuText);
            delete_message = (TextView) itemView.findViewById(R.id.delete_message);
            imageView = (ImageView) itemView.findViewById(R.id.mailReadStatusIV);
        //    content = (RelativeLayout) itemView.findViewById(R.id.content);
            slidingMenu = (SlidingMenu) itemView.findViewById(R.id.slidingMenu);
            messageTitleTV_Pre=itemView.findViewById(R.id.messageTitleTV_Pre);
            messageTitleTV=itemView.findViewById(R.id.messageTitleTV);
            messagePreviewTV_Pre=itemView.findViewById(R.id.messagePreviewTV_Pre);
            messagePreviewTV=itemView.findViewById(R.id.messagePreviewTV);
            messageAuthorTV_Pre=itemView.findViewById(R.id.messageAuthorTV_Pre);
            messageAuthorTV=itemView.findViewById(R.id.messageAuthorTV);
            messageDateTV=itemView.findViewById(R.id.messageDateTV);

        }
    }

}