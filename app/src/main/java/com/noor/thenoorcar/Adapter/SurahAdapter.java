package com.noor.thenoorcar.Adapter;

import static com.noor.thenoorcar.Fragment.AlquranFragment.current_play_position;
import static com.noor.thenoorcar.Fragment.AlquranFragment.current_play_surah_string;
import static com.noor.thenoorcar.Fragment.AlquranFragment.which_current_play;
import static com.noor.thenoorcar.Fragment.AlquranFragment.yourCountDownTimer;


import android.app.Activity;
import android.graphics.drawable.Drawable;
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
import com.noor.thenoorcar.Fragment.AlquranFragment;
import com.noor.thenoorcar.R;


import java.util.ArrayList;
import java.util.List;

public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.MyViewHolder> implements Filterable {

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
            imageView_play = view.findViewById(R.id.imageView_play);
            linear_selected_surah = view.findViewById(R.id.linear_selected_surah);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(getAdapterPosition() != -1){
                        mListener.onClick(menuList.get(getAdapterPosition()));
                    }

                    ImageView imageView_plays = null;
                    View views = null;
                    ImageView imageView_plays_2 = null;
                    View views_2 = null;
                    LinearLayout linear_selected_surah = null;
                    if (getAdapterPosition() != current_play_position && current_play_position != -1){

                        if(mRecyclerList.findViewHolderForAdapterPosition(current_play_position) == null){
                            if(mRecyclerList.findViewHolderForAdapterPosition(which_current_play) == null){

                            }else{
                                views = mRecyclerList.findViewHolderForAdapterPosition(which_current_play).itemView;
                                imageView_plays = views.findViewById(R.id.imageView_play);
                                linear_selected_surah = views.findViewById(R.id.linear_selected_surah);
                                imageView_plays.setVisibility(View.INVISIBLE);
                                linear_selected_surah.setVisibility(View.INVISIBLE);
                            }

                        }else{
                            views = mRecyclerList.findViewHolderForAdapterPosition(current_play_position).itemView;
                            imageView_plays = views.findViewById(R.id.imageView_play);
                            linear_selected_surah = views.findViewById(R.id.linear_selected_surah);
                            imageView_plays.setVisibility(View.INVISIBLE);
                            linear_selected_surah.setVisibility(View.INVISIBLE);
                        }


                        views_2 = mRecyclerList.findViewHolderForAdapterPosition(getAdapterPosition()).itemView;
                        imageView_plays_2 = views_2.findViewById(R.id.imageView_play);
                        linear_selected_surah = views_2.findViewById(R.id.linear_selected_surah);
                        Drawable pause_icon = activity.getResources().getDrawable(R.drawable.pause_icon);
                        imageView_plays_2.setImageDrawable(pause_icon);
                        imageView_plays_2.setVisibility(View.VISIBLE);
                        linear_selected_surah.setVisibility(View.VISIBLE);


                        current_play_position = getAdapterPosition();

                    }else{
                        if(AlquranFragment.status_play_surah.equals("0") ) {  //BARU START NK PLAY
                            current_play_position = getAdapterPosition();
                            views = mRecyclerList.findViewHolderForAdapterPosition(current_play_position).itemView;
                            imageView_plays = views.findViewById(R.id.imageView_play);
                            linear_selected_surah = views.findViewById(R.id.linear_selected_surah);
                            Drawable pause_icon = activity.getResources().getDrawable(R.drawable.pause_icon);
                            imageView_plays.setImageDrawable(pause_icon);
                            imageView_plays.setVisibility(View.VISIBLE);
                            linear_selected_surah.setVisibility(View.VISIBLE);


                        }else if(AlquranFragment.status_play_surah.equals("1")){  //TENGAH PLAY NAK PAUSE

                            //KALAU CLICK SURAH YANG SAMA
                            if(menuList.get(getAdapterPosition()).getId().equals(AlquranFragment.copy_id_surah)){
                                current_play_position = getAdapterPosition();
                                views = mRecyclerList.findViewHolderForAdapterPosition(current_play_position).itemView;
                                linear_selected_surah = views.findViewById(R.id.linear_selected_surah);
                                imageView_plays = views.findViewById(R.id.imageView_play);
                                Drawable pause_icon = activity.getResources().getDrawable(R.drawable.play_iconv2);
                                imageView_plays.setImageDrawable(pause_icon);
                                imageView_plays.setVisibility(View.VISIBLE);
                                linear_selected_surah.setVisibility(View.VISIBLE);


                            }else{

                            }
                        }else if(AlquranFragment.status_play_surah.equals("2")) {  //TENGAH PAUSE NAK PLAY
                            //KALAU CLICK SURAH YANG SAMA
                            if(menuList.get(getAdapterPosition()).getId().equals(AlquranFragment.copy_id_surah)){
                                current_play_position = getAdapterPosition();
                                views = mRecyclerList.findViewHolderForAdapterPosition(current_play_position).itemView;
                                linear_selected_surah = views.findViewById(R.id.linear_selected_surah);
                                imageView_plays = views.findViewById(R.id.imageView_play);
                                Drawable pause_icon = activity.getResources().getDrawable(R.drawable.pause_icon);
                                imageView_plays.setImageDrawable(pause_icon);
                                imageView_plays.setVisibility(View.VISIBLE);
                                linear_selected_surah.setVisibility(View.VISIBLE);
                                if(yourCountDownTimer!=null){
                                    yourCountDownTimer.start();
                                }
                            }else{

                            }
                        }else if(AlquranFragment.status_play_surah.equals("3") || AlquranFragment.status_play_surah.equals("4")) {  //BARU START NK PLAY
                            current_play_position = getAdapterPosition();
                            views = mRecyclerList.findViewHolderForAdapterPosition(current_play_position).itemView;
                            imageView_plays = views.findViewById(R.id.imageView_play);
                            linear_selected_surah = views.findViewById(R.id.linear_selected_surah);
                            Drawable pause_icon = activity.getResources().getDrawable(R.drawable.pause_icon);
                            imageView_plays.setImageDrawable(pause_icon);
                            imageView_plays.setVisibility(View.VISIBLE);
                            linear_selected_surah.setVisibility(View.VISIBLE);

                        }
                    }

                    //TO GET POSITION OF SURAH PLAY
                    for (int i = 0; i <mData.size(); i++){
                        if(menuList.get(getAdapterPosition()).getName().equals(mData.get(i).getName())){
                            which_current_play = i;
                        }
                    }

                }
            });
        }
    }


    public SurahAdapter(RecyclerView mRecyclerList, List<SurahClass> menuList, Activity activity, onClickJobByMonth mListener) {
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
                .inflate(R.layout.adapter_surah_layout, parent, false);

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
        holder.textView_nama_samaran.setText(menu.getEnglishName());




        holder.textView_surah_arab.setText(menu.getArabicName());

        if(current_play_position == -1){
            holder.imageView_play.setVisibility(View.INVISIBLE);
            holder.linear_selected_surah.setVisibility(View.INVISIBLE);
        }else{
            if(current_play_surah_string.equals(menu.getName())){
                if(AlquranFragment.status_play_surah.equals("1")){
                    Drawable pause_icon = activity.getResources().getDrawable(R.drawable.pause_icon);
                    holder.imageView_play.setImageDrawable(pause_icon);
                }else if(AlquranFragment.status_play_surah.equals("2")) {
                    Drawable pause_icon = activity.getResources().getDrawable(R.drawable.play_iconv2);
                    holder.imageView_play.setImageDrawable(pause_icon);
                }else if(AlquranFragment.status_play_surah.equals("3")) {
                    Drawable pause_icon = activity.getResources().getDrawable(R.drawable.pause_icon);
                    holder.imageView_play.setImageDrawable(pause_icon);
                }else if(AlquranFragment.status_play_surah.equals("4")) {
                    Drawable pause_icon = activity.getResources().getDrawable(R.drawable.pause_icon);
                    holder.imageView_play.setImageDrawable(pause_icon);
                }
                holder.imageView_play.setVisibility(View.VISIBLE);
                holder.linear_selected_surah.setVisibility(View.VISIBLE);
            }else{
                holder.linear_selected_surah.setVisibility(View.INVISIBLE);
                holder.imageView_play.setVisibility(View.INVISIBLE);
            }
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
