
package com.example.hangman;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<com.example.hangman.MyListAdapter.ViewHolder> {
    private List<MyListData> listdata;
    private View listItem;

    // RecyclerView recyclerView;
    public MyListAdapter(List<MyListData> listdata) {
        this.listdata = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyListData myListData = listdata.get(position);
        holder.textView.setText(listdata.get(position).getDescription().get(position));
        holder.number.setText(listdata.get(position).getNum().get(position));
        holder.scTextView.setText(listdata.get(position).getUserScore().get(position));
        if (((holder.number.getText().equals("1")) || (holder.number.getText().equals("2")) || (holder.number.getText().equals("3"))) && (HomePage.tempUser.equals(holder.textView.getText()))) {
            listItem.setBackgroundResource(R.drawable.temp_user_background);
        }

        if (holder.number.getText().equals("1") && !(HomePage.tempUser.equals(holder.textView.getText()))) {
            holder.number.setBackgroundResource(R.drawable.num_background);
        } else if (holder.number.getText().equals("2") && !(HomePage.tempUser.equals(holder.textView.getText()))) {
            holder.number.setBackgroundResource(R.drawable.num_2_back);
        } else if (holder.number.getText().equals("3") && !(HomePage.tempUser.equals(holder.textView.getText()))) {
            holder.number.setBackgroundResource(R.drawable.num_3_back);
        }
//        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(),"click on item: "+myListData.getDescription(), Toast.LENGTH_LONG).show();
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView number;
        public TextView textView;
        public TextView scTextView;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.number = (TextView) itemView.findViewById(R.id.num_win);
            this.textView = (TextView) itemView.findViewById(R.id.us_username);
            this.scTextView = (TextView) itemView.findViewById(R.id.us_score);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }
}  