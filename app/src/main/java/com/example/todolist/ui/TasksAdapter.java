package com.example.todolist.ui;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.data.Category;
import com.example.todolist.data.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksAdapter extends ListAdapter<Task, TasksAdapter.TaskViewHolder> {

    private OnTaskClickListener listener;
    private Map<Integer, String> categoryMap = new HashMap<>();

    public interface OnTaskClickListener {
        void onCheckChanged(Task task, boolean isChecked);
    }

    public TasksAdapter(OnTaskClickListener listener) {
        super(new DiffCallback());
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        categoryMap.clear();
        if (categories != null) {
            for (Category cat : categories) {
                categoryMap.put(cat.id, cat.name);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = getItem(position);
        holder.bind(task);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView categoryText;
        private CheckBox checkBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_task_name);
            categoryText = itemView.findViewById(R.id.text_category);
            checkBox = itemView.findViewById(R.id.checkBox_done);

            checkBox.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCheckChanged(getItem(position), checkBox.isChecked());
                }
            });
        }

        public void bind(Task task) {
            nameText.setText(task.name);
            checkBox.setChecked(task.isDone);

            String catName = categoryMap.get(task.categoryId);
            categoryText.setText(catName != null ? catName : "Unknown");

            if (task.isDone) {
                nameText.setPaintFlags(nameText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                nameText.setPaintFlags(nameText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
    }

    static class DiffCallback extends DiffUtil.ItemCallback<Task> {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.name.equals(newItem.name) &&
                    oldItem.isDone == newItem.isDone &&
                    oldItem.categoryId == newItem.categoryId;
        }
    }
}
