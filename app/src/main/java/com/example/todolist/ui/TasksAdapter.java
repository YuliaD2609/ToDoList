package com.example.todolist.ui;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.data.Category;
import com.example.todolist.data.Task;

public class TasksAdapter extends ListAdapter<TasksAdapter.Item, RecyclerView.ViewHolder> {

    private static final int TYPE_CATEGORY_HEADER = 0;
    private static final int TYPE_TASK = 1;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onCheckChanged(Task task, boolean isChecked);
        void onCategoryDelete(Category category);
        void onCategoryRename(Category category);
        void onTaskDelete(Task task);
    }

    public interface Item {
        int getType();
        long getId();
    }

    public static class CategoryHeaderItem implements Item {
        public Category category;
        public CategoryHeaderItem(Category category) { this.category = category; }
        @Override public int getType() { return TYPE_CATEGORY_HEADER; }
        @Override public long getId() { return category.id * -1L; } // Negative IDs for headers to avoid collision
    }

    public static class TaskItem implements Item {
        public Task task;
        public TaskItem(Task task) { this.task = task; }
        @Override public int getType() { return TYPE_TASK; }
        @Override public long getId() { return task.id; }
    }

    public TasksAdapter(OnItemClickListener listener) {
        super(new DiffCallback());
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CATEGORY_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(((CategoryHeaderItem) getItem(position)).category);
        } else if (holder instanceof TaskViewHolder) {
            ((TaskViewHolder) holder).bind(((TaskItem) getItem(position)).task);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private ImageView deleteIcon;
        private ImageView renameIcon;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_header_name);
            deleteIcon = itemView.findViewById(R.id.image_delete);
            renameIcon = itemView.findViewById(R.id.image_rename);

            deleteIcon.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    CategoryHeaderItem item = (CategoryHeaderItem) getItem(pos);
                    listener.onCategoryDelete(item.category);
                }
            });

            renameIcon.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    CategoryHeaderItem item = (CategoryHeaderItem) getItem(pos);
                    listener.onCategoryRename(item.category);
                }
            });
        }

        public void bind(Category category) {
            nameText.setText(category.name);
        }
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private CheckBox checkBox;
        private ImageView deleteButton;
        private View rootView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            nameText = itemView.findViewById(R.id.text_task_name);
            checkBox = itemView.findViewById(R.id.checkBox_done);
            deleteButton = itemView.findViewById(R.id.image_delete_task);

            checkBox.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    TaskItem item = (TaskItem) getItem(position);
                    listener.onCheckChanged(item.task, checkBox.isChecked());
                }
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    TaskItem item = (TaskItem) getItem(position);
                    listener.onTaskDelete(item.task);
                }
            });
        }

        public void bind(Task task) {
            nameText.setText(task.name);
            checkBox.setChecked(task.isDone);

            if (task.isDone) {
                nameText.setPaintFlags(nameText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                rootView.setAlpha(0.5f); // Lighter when checked
            } else {
                nameText.setPaintFlags(nameText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                rootView.setAlpha(1.0f);
            }
        }
    }

    static class DiffCallback extends DiffUtil.ItemCallback<Item> {
        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            if (oldItem.getType() != newItem.getType()) return false;
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            if (oldItem instanceof CategoryHeaderItem && newItem instanceof CategoryHeaderItem) {
                return ((CategoryHeaderItem) oldItem).category.name.equals(((CategoryHeaderItem) newItem).category.name);
            }
            if (oldItem instanceof TaskItem && newItem instanceof TaskItem) {
                Task t1 = ((TaskItem) oldItem).task;
                Task t2 = ((TaskItem) newItem).task;
                return t1.name.equals(t2.name) &&
                        t1.isDone == t2.isDone &&
                        t1.categoryId == t2.categoryId;
            }
            return false;
        }
    }
}
