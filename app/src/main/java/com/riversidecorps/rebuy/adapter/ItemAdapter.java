package com.riversidecorps.rebuy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.riversidecorps.rebuy.R;
import com.riversidecorps.rebuy.models.Listing;

import java.util.List;


/**
 * This adapter is used to combine and display all data on the card view
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private List<Listing> itemLists;
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title_pre;
        TextView item_name;
        TextView price_pre;
        TextView item_price;
        TextView description_pre;
        TextView item_description;
        TextView date_pre;
        TextView item_date;
        private View view;
        ImageView itemImage;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            title_pre = (TextView) view.findViewById(R.id.title_pre);
            item_name = (TextView) view.findViewById(R.id.item_name);

            price_pre = (TextView) view.findViewById(R.id.price_pre);
            item_price = (TextView) view.findViewById(R.id.item_price);
            itemImage = (ImageView) view.findViewById(R.id.item_image);
            description_pre = (TextView) view.findViewById(R.id.description_pre);
            item_description = (TextView) view.findViewById(R.id.item_description);

            date_pre = (TextView) view.findViewById(R.id.date_pre);
            item_date = (TextView) view.findViewById(R.id.item_date);


        }
    }


    public ItemAdapter(Context context, List<Listing> itemLists) {
        this.context = context;
        this.itemLists = itemLists;
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        viewHolder.title_pre.setText(R.string.title_pre);
        viewHolder.price_pre.setText(R.string.price_pre);
//        viewHolder.seller_pre.setText(R.string.seller_pre);
        viewHolder.description_pre.setText(R.string.description_pre);
        viewHolder.date_pre.setText(R.string.date_pre);

        viewHolder.item_name.setText(itemLists.get(position).getItemName());
        viewHolder.item_price.setText(itemLists.get(position).getItemPrice());
        viewHolder.item_description.setText(itemLists.get(position).getItemDescription());
        String date = itemLists.get(position).getCreatedDate();
        viewHolder.item_date.setText(date);

        String itemId=itemLists.get(position).getmItemId();
        String imagePath = "itemImageListings/" + itemId + ".png";
        StorageReference itemImageRef = mStorage.getReference(imagePath);
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(itemImageRef)
                .into(viewHolder.itemImage);
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,com.riversidecorps.rebuy.SingleListingActivity.class );
                intent.putExtra("itemName", itemLists.get(viewHolder.getAdapterPosition()).getItemName());
                intent.putExtra("itemPrice", itemLists.get(viewHolder.getAdapterPosition()).getItemPrice());
                intent.putExtra("itemDes", itemLists.get(viewHolder.getAdapterPosition()).getItemDescription());
                intent.putExtra("itemQuantity", itemLists.get(viewHolder.getAdapterPosition()).getItemQuantity());
                intent.putExtra("itemId", itemLists.get(viewHolder.getAdapterPosition()).getmItemId());
                intent.putExtra("itemSellerId", itemLists.get(viewHolder.getAdapterPosition()).getmItemSellerId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemLists.size();
    }
}