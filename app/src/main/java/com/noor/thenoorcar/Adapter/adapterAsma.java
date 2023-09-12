package com.noor.thenoorcar.Adapter;



import static com.noor.thenoorcar.Fragment.AsmaFragment.position_scroll;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.noor.thenoorcar.Class.AsmaClass;
import com.noor.thenoorcar.R;

import java.util.ArrayList;
import java.util.List;

public class adapterAsma extends RecyclerView.Adapter<adapterAsma.MyViewHolder> implements Filterable {

    private List<AsmaClass> menuList;
    private List<AsmaClass> mData;
    Activity activity;
    private List<AsmaClass> mDataListFiltered;
    private onClickJobByMonth mListener;
    private RecyclerView mRecyclerList = null;
    private int currentPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_name,textView_meaning,textView_count;
        ImageView imageView_asma;
        LinearLayout linear_1;

        public MyViewHolder(View view) {
            super(view);

            textView_name = view.findViewById(R.id.textView_name);
            textView_meaning = view.findViewById(R.id.textView_meaning);
            imageView_asma = view.findViewById(R.id.imageView_asma);
            textView_count = view.findViewById(R.id.textView_count);
            linear_1 = view.findViewById(R.id.linear_1);

            linear_1.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(menuList.get(getAdapterPosition()));
                }
            });
        }
    }


    public adapterAsma(RecyclerView mRecyclerList, List<AsmaClass> menuList, Activity activity, onClickJobByMonth mListener) {
        this.mRecyclerList = mRecyclerList;
        this.mData = menuList;
        this.menuList = menuList;
        this.activity = activity;
        this.mListener = mListener;
    }

    public interface onClickJobByMonth {
        void onClick(AsmaClass jobByMonthClass);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_asma, parent, false);

        return new MyViewHolder(itemView);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final AsmaClass menu = menuList.get(position);
        holder.textView_name.setText(menu.getNameTranscription());
        holder.textView_meaning.setText(menu.getName());
        holder.textView_count.setText(menu.getNumber());


        for (int i =1 ; i < 100; i++){
            if(menu.getNumber().equals(String.valueOf(i))){
                int id = activity.getResources().getIdentifier("asma_icon_"+i, "drawable", activity.getPackageName());
                holder.imageView_asma.setImageResource(id);
            }
        }


        if (position_scroll != -1){
            if(position == position_scroll){
                holder.linear_1.setVisibility(View.VISIBLE);
            }else{
                holder.linear_1.setVisibility(View.GONE);
            }
        }else {
            holder.linear_1.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return menuList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                List<AsmaClass> filteredList = new ArrayList<>();
                if (charString.isEmpty()) {
                    filteredList.addAll(mData);

                    mDataListFiltered = filteredList;
                } else {
                    for (AsmaClass row : mData) {
                        if (row.getArabicName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mDataListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                menuList = (ArrayList<AsmaClass>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}

