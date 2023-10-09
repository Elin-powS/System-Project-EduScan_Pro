package com.example.eduscan_pro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class semester_model_Adapter extends RecyclerView.Adapter<semester_model_Adapter.ViewHolder> {

    Context context;
    private static View.OnLongClickListener longClickListener ;
    ArrayList<semester_model> list;

    public semester_model_Adapter(Context context, ArrayList<semester_model> list){

        this.context = context;
        this.list = list;
    }
    public semester_model getItemAt(int pos){
        try{
            return  list.get(pos);
        }
        catch (Exception e){
            return null;
        }
    }

    @NonNull
    @Override
    public semester_model_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.semester_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull semester_model_Adapter.ViewHolder holder, int position) {

        semester_model user = list.get(position);

        holder.degree.setText(user.getdegree());
        holder.semester.setText(user.getBatch());


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView degree,semester;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            degree = itemView.findViewById(R.id.Degree);
            semester = itemView.findViewById(R.id.Semester);

        }
    }
}
