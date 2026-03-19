package com.stdili.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.models.Student;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<Student> students;

    public StudentAdapter(List<Student> students) {
        this.students = students;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = students.get(position);
        holder.tvName.setText(student.getName());
        holder.tvLevel.setText("Level " + student.getLevel());
        holder.tvCoins.setText(student.getCoins() + " Coins");
        holder.tvActivity.setText("Activity: " + student.getActivity());
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLevel, tvCoins, tvActivity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLevel = itemView.findViewById(R.id.tvLevel);
            tvCoins = itemView.findViewById(R.id.tvCoins);
            tvActivity = itemView.findViewById(R.id.tvActivity);
        }
    }
}