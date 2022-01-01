package com.example.project1_2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.CustomViewHolder> implements Filterable {

    private Context context;
    private List<Item> unFilteredlist;
    private List<Item> filteredList;
    private Intent intent;

    public RvAdapter(Context context, List<Item> list) {
        this.context = context;
        this.unFilteredlist = list;
        this.filteredList = list;
    }

    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        final Item item = filteredList.get(position);
        holder.name.setText(item.getItem_name());

        holder.card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent = new Intent(v.getContext(), ItemActivity.class);
                intent.putExtra("name", item.getItem_name());
                intent.putExtra("number", item.getItem_number());
                intent.putExtra("email", item.getItem_email());
                intent.putExtra("job", item.getItem_job());
                v.getContext().startActivity(intent);
                Toast.makeText(v.getContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int getItemCount() {
        return this.filteredList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CardView card;
        public CustomViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_tv_name);
            card = itemView.findViewById(R.id.item_cardview);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    filteredList = unFilteredlist;
                } else {
                    ArrayList<Item> filteringList = new ArrayList<>();
                    for(Item item : unFilteredlist) {
                        if(item.getItem_name().toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(item);
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Item>)results.values;
                notifyDataSetChanged();
            }
        };
    }

}