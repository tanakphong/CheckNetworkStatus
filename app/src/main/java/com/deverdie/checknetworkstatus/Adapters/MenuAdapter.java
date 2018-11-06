package com.deverdie.checknetworkstatus.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deverdie.checknetworkstatus.Models.MenuModel;
import com.deverdie.checknetworkstatus.R;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter  extends RecyclerView.Adapter<MenuAdapter.ViewHolder>  {

    private Context context;
    private List<MenuModel> datas = new ArrayList<>();
    private ItemClickListener itemClickListener;

    public MenuAdapter(Context context, List<MenuModel> datas, ItemClickListener itemClickListener) {
        this.context = context;
        this.datas = datas;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.widget_recycler_rowitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuModel menu = getItem(position);
        holder.header.setText(menu.getHeader());
        holder.subHeader.setText(menu.getSubheader());
        holder.title.setText(menu.getTitle());
        holder.desc.setText(menu.getDesc());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public MenuModel getItem(int id) {
        return datas.get(id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView header, subHeader, title, desc;

        ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv_rowitem);
            header = itemView.findViewById(R.id.tv_header);
            subHeader = itemView.findViewById(R.id.tv_subheader);
            title = itemView.findViewById(R.id.tv_title);
            desc = itemView.findViewById(R.id.tv_desc);
            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                try {
                    itemClickListener.onItemClick(view, getAdapterPosition());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position) throws ClassNotFoundException;
    }
}
