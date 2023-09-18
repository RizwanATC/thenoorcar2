package com.noor.thenoorcar.Adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.noor.thenoorcar.Class.SurahClass;
import com.noor.thenoorcar.Common.PreferenceManagerTranslation;
import com.noor.thenoorcar.R;


import java.util.ArrayList;
import java.util.List;

public class SurahAdapterSurahFragment extends RecyclerView.Adapter<SurahAdapterSurahFragment.MyViewHolder> implements Filterable {

    private List<SurahClass> menuList;
    private List<SurahClass> mData;
    Activity activity;
    private List<SurahClass> mDataListFiltered;
    private onClickJobByMonth mListener;
    private RecyclerView mRecyclerList = null;
    private int currentPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_nama_surah, textView_count,textView_nama_samaran,textView_surah_arab;
        ImageView imageView_play;
        LinearLayout linear_selected_surah;
        public MyViewHolder(View view) {
            super(view);

            textView_nama_surah = view.findViewById(R.id.textView_nama_surah);
            textView_count = view.findViewById(R.id.textView_count);
            textView_nama_samaran = view.findViewById(R.id.textView_nama_samaran);
            textView_surah_arab = view.findViewById(R.id.textView_surah_arab);
            linear_selected_surah = view.findViewById(R.id.linear_selected_surah);

            textView_nama_surah.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Avenir-Black-03.ttf"));
            textView_nama_samaran.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Avenir-Book-01.ttf"));

            textView_surah_arab.setTypeface(Typeface.createFromAsset(activity.getAssets(), "_PDMS_Saleem_QuranFont.ttf"));
            textView_count.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Avenir-Black-03.ttf"));


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(menuList.get(getAdapterPosition()));
                }
            });
        }
    }


    public SurahAdapterSurahFragment(RecyclerView mRecyclerList, List<SurahClass> menuList, Activity activity, onClickJobByMonth mListener) {
        this.mRecyclerList = mRecyclerList;
        this.mData = menuList;
        this.menuList = menuList;
        this.activity = activity;
        this.mListener = mListener;
    }

    public interface onClickJobByMonth {
        void onClick(SurahClass jobByMonthClass);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_surah_layout_surah_fragment, parent, false);

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
        final SurahClass menu = menuList.get(position);

        holder.textView_count.setText(menu.getNumber());
        holder.textView_nama_surah.setText(menu.getName());

        PreferenceManagerTranslation languageSession = new PreferenceManagerTranslation(activity);
        if(languageSession.getUserDetails().get(PreferenceManagerTranslation.KEY_THEME_IMAGE).equals("ms")){
            holder.textView_nama_samaran.setText(menu.getMalay()+" ("+menu.getTotalVerses()+")");
        }
        if(languageSession.getUserDetails().get(PreferenceManagerTranslation.KEY_THEME_IMAGE).equals("en")){
            holder.textView_nama_samaran.setText(menu.getEnglishName()+" ("+menu.getTotalVerses()+")");
        }
        if(languageSession.getUserDetails().get(PreferenceManagerTranslation.KEY_THEME_IMAGE).equals("ar")){
            holder.textView_nama_samaran.setText(" ("+menu.getTotalVerses()+") "+menu.getArabicName());
        }
        if(languageSession.getUserDetails().get(PreferenceManagerTranslation.KEY_THEME_IMAGE).equals("id")){
            holder.textView_nama_samaran.setText(menu.getIndonesia()+" ("+menu.getTotalVerses()+")");
        }
        if(languageSession.getUserDetails().get(PreferenceManagerTranslation.KEY_THEME_IMAGE).equals("ta")){
            holder.textView_nama_samaran.setText(menu.getTamil()+" ("+menu.getTotalVerses()+")");
        }
        if(languageSession.getUserDetails().get(PreferenceManagerTranslation.KEY_THEME_IMAGE).equals("tk")){
            holder.textView_nama_samaran.setText(menu.getTurkiName()+" ("+menu.getTotalVerses()+")");
        }
        if(languageSession.getUserDetails().get(PreferenceManagerTranslation.KEY_THEME_IMAGE).equals("zh")){
            holder.textView_nama_samaran.setText(menu.getChina()+" ("+menu.getTotalVerses()+")");
        }

        holder.textView_surah_arab.setText(menu.getArabicName());


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
                List<SurahClass> filteredList = new ArrayList<>();
                if (charString.isEmpty()) {
                    filteredList.addAll(mData);

                    mDataListFiltered = filteredList;
                } else {
                    for (SurahClass row : mData) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
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
                menuList = (ArrayList<SurahClass>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
