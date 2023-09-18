package com.noor.thenoorcar.Adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.noor.thenoorcar.Class.ReciterClass;
import com.noor.thenoorcar.Common.PreferenceManagerReciter;
import com.noor.thenoorcar.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReciterAdapter extends RecyclerView.Adapter<ReciterAdapter.MyViewHolder> {

    private List<ReciterClass> menuList;
    Activity activity;
    private onClickJobByMonth mListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_name;
        CircleImageView circleImageView;
        LinearLayout linear_selected;
        public MyViewHolder(View view) {
            super(view);

            textView_name =  view.findViewById(R.id.textView_name);
            circleImageView = view.findViewById(R.id.profile_image);
            linear_selected = view.findViewById(R.id.linear_selected);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClick(menuList.get(getAdapterPosition()));
                }
            });
        }
    }


    public ReciterAdapter(List<ReciterClass> menuList, Activity activity, onClickJobByMonth mListener) {
        this.menuList = menuList;
        this.activity = activity;
        this.mListener = mListener;
    }

    public interface onClickJobByMonth {
        void onClick(ReciterClass jobByMonthClass);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_reciter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ReciterClass menu = menuList.get(position);
        holder.textView_name.setText(menu.getName());
        Picasso.get().load(menu.getImage_url()).into(holder.circleImageView);

        PreferenceManagerReciter sessionReciter = new PreferenceManagerReciter(activity);

        if(sessionReciter.getUserDetails().get(PreferenceManagerReciter.KEY_THEME_NAME).equals(menu.getName())){
            holder.linear_selected.setVisibility(View.VISIBLE);
        }else {
            holder.linear_selected.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return menuList.size();
    }

}
