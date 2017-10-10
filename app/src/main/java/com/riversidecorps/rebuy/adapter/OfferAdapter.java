package com.riversidecorps.rebuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.riversidecorps.rebuy.R;
import com.riversidecorps.rebuy.models.Offer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * This adapter is used to combine and display all data on the card view
 */

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Offer> offerLists;


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_name;
        TextView item_buyer;
        TextView item_original_price;
        TextView offer_price;
        TextView offer_id;

        private View view;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;

            item_name = (TextView) view.findViewById(R.id.item_name);
            item_buyer = (TextView) view.findViewById(R.id.item_buyer);
            item_original_price = (TextView) view.findViewById(R.id.item_original_price);
            offer_price = (TextView) view.findViewById(R.id.offer_price);
            offer_id = (TextView) view.findViewById(R.id.offer_id);
        }
    }


    public OfferAdapter(Context context, ArrayList<Offer> offerLists) {
        this.context = context;
        this.offerLists = offerLists;
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offer_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        viewHolder.item_name.setText(offerLists.get(position).getItemName());
        viewHolder.item_buyer.setText(offerLists.get(position).getItemBuyer());
        viewHolder.item_original_price.setText(offerLists.get(position).getItemOriginalPrice());
        viewHolder.offer_price.setText(offerLists.get(position).getOfferPrice());
        viewHolder.offer_id.setText(offerLists.get(position).getOfferID());

        String date = offerLists.get(position).getOfferDate();
        //viewHolder.item_date.setText(date);
    }

    @Override
    public int getItemCount() {
        return offerLists.size();
    }
}