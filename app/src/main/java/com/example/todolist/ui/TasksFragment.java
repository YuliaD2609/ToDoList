package com.example.todolist.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.data.Category;
import com.example.todolist.data.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private MainViewModel mViewModel;
    private TasksAdapter mAdapter;
    private List<Category> mCategories = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new TasksAdapter((task, isChecked) -> {
            task.isDone = isChecked;
            if (isChecked) {
                task.timestampDone = System.currentTimeMillis();
            } else {
                task.timestampDone = 0;
            }
            mViewModel.update(task);
        });
        recyclerView.setAdapter(mAdapter);

        mViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            mCategories = categories;
            mAdapter.setCategories(categories);
        });

        mViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            mAdapter.submitList(tasks);
        });

        view.findViewById(R.id.fab_add_task).setOnClickListener(v -> showAddTaskDialog());
        view.findViewById(R.id.fab_add_category).setOnClickListener(v -> showAddCategoryDialog());
    }

    private void showAddTaskDialog() {
        if (mCategories.isEmpty()) {
            new AlertDialog.Builder(getContext())
                    .setTitle("No Categories")
                    .setMessage("Please create a category first.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_add_task_title);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText input = new EditText(getContext());
        input.setHint(R.string.hint_task_name);
        layout.addView(input);

        final Spinner categorySpinner = new Spinner(getContext());
        List<String> categoryNames = new ArrayList<>();
        for (Category c : mCategories)
            categoryNames.add(c.name);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        layout.addView(categorySpinner);

        builder.setView(layout);

        builder.setPositiveButton(R.string.action_save, (dialog, which) -> {
            String taskName = input.getText().toString().trim();
            if (!taskName.isEmpty()) {
                int selectedIndex = categorySpinner.getSelectedItemPosition();
                if (selectedIndex >= 0 && selectedIndex < mCategories.size()) {
                    Category selectedCat = mCategories.get(selectedIndex);
                    Task newTask = new Task(taskName, selectedCat.id, System.currentTimeMillis());
                    mViewModel.insert(newTask);
                }
            }
        });
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.dialog_add_category_title);

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.hint_category_name);
        builder.setView(input);

        builder.setPositiveButton(R.string.action_save, (dialog, which) -> {
            String catName = input.getText().toString().trim();
            if (!catName.isEmpty()) {
                mViewModel.insertCategory(new Category(catName));
            }
        });
        builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
