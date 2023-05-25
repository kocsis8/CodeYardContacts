package com.example.codeyardcontacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsItemAdapter extends RecyclerView.Adapter<ContactsItemAdapter.ViewHolder> implements Filterable {

    private ArrayList<Contact> mContactItemData = new ArrayList<>();
    private ArrayList<Contact> mContactItemDataAll = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    ContactsItemAdapter(Context context, ArrayList<Contact> itemsData){
        this.mContactItemData = itemsData;
        this.mContactItemDataAll = itemsData;
        this.mContext = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder( ContactsItemAdapter.ViewHolder holder, int position) {
        // jelenlegi elem.
        Contact currentItem = mContactItemData.get(position);

        holder.bindTo(currentItem);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // másik megfejelenítés megnyitása
                Intent i = new Intent(mContext, ContactDetailActivity.class);
                i.putExtra("name", currentItem.getName());
                i.putExtra("email", currentItem.getEmail());
                i.putExtra("address", currentItem.getAddress());
                i.putExtra("phoneNumber", currentItem.getPhoneNumber());
                i.putExtra("imgURL", currentItem.getImageURL());


                // át irányítás új aktivitire,
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() { return mContactItemData.size(); }

    // filterezőt meghívó függvény
    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    // filterezés beirt szöveg alapján
    private Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Contact> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = mContactItemDataAll.size();
                results.values = mContactItemDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(Contact item : mContactItemDataAll) {
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mContactItemData = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };

    // vh a megjelenításhez
    class ViewHolder extends RecyclerView.ViewHolder {

        // Member Variables for the TextViews
        private TextView mNameText;
        private TextView mAddressText;
        private TextView mEmailText;
        private CircleImageView mImage;


        ViewHolder(View itemView) {
            super(itemView);

            // nézet init.
            mNameText = itemView.findViewById(R.id.textviewName);
            mAddressText = itemView.findViewById(R.id.textviewAddress);
            mEmailText = itemView.findViewById(R.id.textviewEmail);
            mImage = itemView.findViewById(R.id.circleImageView);

        }

        void bindTo(Contact currentItem){
            mNameText.setText(currentItem.getName());
            mAddressText.setText(currentItem.getAddress());
            mEmailText.setText(currentItem.getEmail());

            // kep betöltése.
            Picasso.get()
                    .load(currentItem.getImageURL())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(mImage);
        }
    }
}

