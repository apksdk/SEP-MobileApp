package com.riversidecorps.rebuy;

/**
 * Created by tom on 2017/10/8.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.riversidecorps.rebuy.models.Message;

import java.util.List;

/**
 * created by yhao on 2017/8/18.
 */


class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    private List<Message> messageLists;
    private Context mContext;

    private SlidingMenu mOpenMenu;
    private SlidingMenu mScrollingMenu;

    SlidingMenu getScrollingMenu() {
        return mScrollingMenu;
    }

    void setScrollingMenu(SlidingMenu scrollingMenu) {
        mScrollingMenu = scrollingMenu;
    }

    void holdOpenMenu(SlidingMenu slidingMenu) {
        mOpenMenu = slidingMenu;
    }

    void closeOpenMenu() {
        if (mOpenMenu != null && mOpenMenu.isOpen()) {
            mOpenMenu.closeMenu();
            mOpenMenu = null;
        }
    }

    MyAdapter(List<Message> data, Context context) {
        messageLists = data;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.message, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
//       holder.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
    //   holder.menuText.setText(messageLists.get(position).getTitle());
        TextView menuText;
        ImageView imageView;
        RelativeLayout content;
        SlidingMenu slidingMenu;

        holder.messagePreviewTV.setText(messageLists.get(position).getContent());
        holder.messageAuthorTV.setText(messageLists.get(position).getBuyer());
        holder.messageDateTV.setText(messageLists.get(position).getDatetime());
        holder.messageTitleTV.setText(messageLists.get(position).getTitle());
        holder.menuText.setText("Reply");
        holder.menuText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOpenMenu();
                boolean top;
                if (holder.menuText.getText().toString().equals("置顶")) {
                    holder.menuText.setText("取消置顶");
                    holder.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cardview_light_background));
                    top = true;
                } else {
                    holder.menuText.setText("置顶");
                    holder.imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                    top = false;
                }
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

    interface OnClickListener {
        void onMenuClick(int position, boolean top);

        void onContentClick(int position);
    }

    private OnClickListener mOnClickListener;

    void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }


    @Override
    public int getItemCount() {
        return messageLists.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView menuText;
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