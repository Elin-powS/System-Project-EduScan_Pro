package com.example.eduscan_pro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class semester_model_Adapter extends RecyclerView.Adapter<semester_model_Adapter.ViewHolder> {

    private static ClickListener onclickListener;
    Context context;
    private FirebaseUser firebaseuser;
    DatabaseReference databaseReference;

    ArrayList<semester_model> list;

    String degree_del_or_edit, semester_del_or_edit, Edit_degree, Edit_year, Edit_semester,image_UI;
    Button Edit_semester_info;
    EditText Degree, Year, Semester;
    TextView Tittle_edit_semester;
    String imageuri;
    int x = 0;

    public semester_model_Adapter(Context context, ArrayList<semester_model> list) {

        this.context = context;
        this.list = list;
    }

    public semester_model getItemAt(int pos) {
        try {
            return list.get(pos);
        } catch (Exception e) {
            return null;
        }
    }

    @NonNull
    @Override
    public semester_model_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.semester_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull semester_model_Adapter.ViewHolder holder, int position) {

        semester_model user = list.get(position);

        holder.degree.setText(user.getdegree());
        holder.semester.setText(user.getBatch());
        imageuri = user.getImageUI();


        degree_del_or_edit = user.getdegree();
        semester_del_or_edit = user.getBatch();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                degree_del_or_edit = user.getdegree();
                semester_del_or_edit = user.getBatch();
                onclickListener.onItemClick(position, null);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        TextView degree,semester;
        ImageButton imageButton;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            degree = itemView.findViewById(R.id.Degree);
            semester = itemView.findViewById(R.id.Semester);
            imageButton = itemView.findViewById(R.id.option);
            imageView = itemView.findViewById(R.id.Recycle_card_image);


            imageButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager").child(firebaseuser.getUid()).child("Semester Information");
            show_menu_option(v);

        }

        private void show_menu_option(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.option_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (item.getItemId() == R.id.option_edit) {

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_semester_layout);

                Degree = (EditText) dialog.findViewById(R.id.degree);
                Year = (EditText) dialog.findViewById(R.id.year);
                Semester = (EditText) dialog.findViewById(R.id.semester);
                Tittle_edit_semester = (TextView) dialog.findViewById(R.id.add_edit_semester);
                Tittle_edit_semester.setText("Edit Semester Information");

                Edit_semester_info = dialog.findViewById(R.id.button_insert);

                Edit_semester_info.setText("Edit");

                Edit_semester_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        x = 1;

                        Edit_degree = Degree.getText().toString().trim();
                        Edit_year = Year.getText().toString().trim();
                        Edit_semester = Semester.getText().toString().trim();

                        if (Edit_degree.isEmpty()) {
                            Degree.setError("Required!");
                            Degree.requestFocus();
                            x = 0;
                            return;
                        }
                        if (Edit_year.isEmpty()) {
                            Year.setError("Required!");
                            Year.requestFocus();
                            x = 0;
                            return;
                        }
                        if (Edit_semester.isEmpty()) {
                            Semester.setError("Required!");
                            Semester.requestFocus();
                            x = 0;
                            return;
                        }

                        if (x == 1) {


                            databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager").child(firebaseuser.getUid()).child("Semester Information");
                            databaseReference.child(degree_del_or_edit).child(semester_del_or_edit).get().addOnSuccessListener(dataSnapshot -> {
                                databaseReference.child(Edit_degree).child(Edit_year +" & "+Edit_semester).setValue(dataSnapshot.getValue());
                                databaseReference.child(degree_del_or_edit).child(semester_del_or_edit).removeValue();


                                HashMap<String, Object> map = new HashMap<>();

                                map.put("Degree", Edit_degree.trim());
                                map.put("Year", Edit_year.trim());
                                map.put("Semester", Edit_semester.trim());

                                databaseReference = FirebaseDatabase.getInstance().getReference("Department Manager").child(firebaseuser.getUid()).child("Semester Information");
                                databaseReference.child(Edit_degree).child(Edit_year +" & "+Edit_semester).child("Dummy Data")
                                        .updateChildren(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(context, "Edit Semester Information.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(context, Recycle_view_of_Batch.class);
                                                    context.startActivity(intent);
                                                } else {
                                                    Toast.makeText(context, "Edit Semester Information failed, Try again!!", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });


                            });
                        }
                    }

                });

                dialog.setCancelable(true);
                dialog.show();

                return true;

            }

            else if (item.getItemId() == R.id.option_delete) {
                databaseReference.child(degree_del_or_edit).child(semester_del_or_edit).removeValue();
                Toast.makeText(context, "Semester Deleting Successfully Done.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, Recycle_view_of_Batch.class);
                context.startActivity(intent);
                return true;
            }
            else {
                return false;
            }
        }
    }

    public interface ClickListener {
        View.OnClickListener onItemClick(int position, View view);
    }

    public static void setClickListener(ClickListener onclickListener) {
        semester_model_Adapter.onclickListener = onclickListener;
    }
}

