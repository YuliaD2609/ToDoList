package com.example.todolist.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.data.NotificationTime;

import java.util.Locale;

public class NotificationTimesAdapter extends ListAdapter<NotificationTime, NotificationTimesAdapter.ViewHolder> {

    private OnTimeInteractionListener listener;

    public interface OnTimeInteractionListener {
        void onToggle(NotificationTime time, boolean isEnabled);
        void onDelete(NotificationTime time);
    }

    public NotificationTimesAdapter(OnTimeInteractionListener listener) {
        super(new DiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_time, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_notify);
            deleteButton = itemView.findViewById(R.id.image_delete);

            checkBox.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    NotificationTime item = getItem(pos);
                    // Update local state temporarily to avoid UI flicker if list reload is slow
                    item.isEnabled = checkBox.isChecked(); 
                    listener.onToggle(item, checkBox.isChecked());
                }
            });

            deleteButton.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onDelete(getItem(pos));
                }
            });
        }

        public void bind(NotificationTime time) {
            checkBox.setText(String.format(Locale.getDefault(), "%02d:%02d", time.hour, time.minute));
            checkBox.setChecked(time.isEnabled);
        }
    }

    static class DiffCallback extends DiffUtil.ItemCallback<NotificationTime> {
        @Override
        public boolean areItemsTheSame(@NonNull NotificationTime oldItem, @NonNull NotificationTime newItem) {
            // Uniquely identified by hour/minute
            return oldItem.hour == newItem.hour && oldItem.minute == newItem.minute;
        }

        @Override
        public boolean areContentsTheSame(@NonNull NotificationTime oldItem, @NonNull NotificationTime newItem) {
            return oldItem.isEnabled == newItem.isEnabled;
        }
    }
}
