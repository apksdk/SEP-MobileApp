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
        TextView title_pre;
        TextView item_name;

        TextView buyer_pre;
        TextView item_buyer;

        TextView original_price_pre;
        TextView item_original_price;

        TextView offer_price_pre;
        TextView item_offer_price;

        TextView quantity_pre;
        TextView item_quantity;

        TextView description_pre;
        TextView item_description;

        TextView date_pre;
        TextView item_date;
        private View view;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;

            title_pre = (TextView) view.findViewById(R.id.title_pre);
            item_name = (TextView) view.findViewById(R.id.item_name);

            buyer_pre = (TextView) view.findViewById(R.id.buyer_pre);
            item_buyer = (TextView) view.findViewById(R.id.item_buyer);

            original_price_pre = (TextView) view.findViewById(R.id.original_price_pre);
            item_original_price = (TextView) view.findViewById(R.id.item_original_price);

            offer_price_pre = (TextView) view.findViewById(R.id.offer_price_pre);
            item_offer_price = (TextView) view.findViewById(R.id.item_offer_price);

            //quantity_pre = (TextView) view.findViewById(R.id.quantity_pre);
            //item_quantity = (TextView) view.findViewById(R.id.item_quantity);

            //description_pre = (TextView) view.findViewById(R.id.description_pre);
            //item_description = (TextView) view.findViewById(R.id.item_description);

            //date_pre = (TextView) view.findViewById(R.id.date_pre);
            //item_date = (TextView) view.findViewById(R.id.item_date);

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
        viewHolder.item_offer_price.setText(offerLists.get(position).getOfferPrice());
        //viewHolder.item_quantity.setText(offerLists.get(position).getItemQuantity().toString());
        //viewHolder.item_description.setText(offerLists.get(position).getItemDescription());

        String date = offerLists.get(position).getOfferDate();
        //viewHolder.item_date.setText(date);

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,com.riversidecorps.rebuy.ViewOffersActivity.class );
                intent.putExtra("itemName", offerLists.get(viewHolder.getAdapterPosition()).getItemName());
                intent.putExtra("itemBuyer", offerLists.get(viewHolder.getAdapterPosition()).getItemBuyer());
                intent.putExtra("itemOriginalPrice", offerLists.get(viewHolder.getAdapterPosition()).getItemOriginalPrice());
                intent.putExtra("itemOfferPrice", offerLists.get(viewHolder.getAdapterPosition()).getOfferPrice());
                //intent.putExtra("itemQuantity", offerLists.get(viewHolder.getAdapterPosition()).getItemQuantity());
                //intent.putExtra("itemDes", offerLists.get(viewHolder.getAdapterPosition()).getItemDescription());
                context.startActivity(intent);
            }
        });

        /*
        this.ItemBuyer = mItemBuyer;
        this.ItemName = mItemName;
        this.ItemOriginalPrice = mItemOriginalPrice;
        this.OfferPrice = mOfferPrice;
        this.OfferDate = mOfferDate;
        this.ItemDescription = mItemDescription;
        */
    }

    @Override
    public int getItemCount() {
        return offerLists.size();
    }
}