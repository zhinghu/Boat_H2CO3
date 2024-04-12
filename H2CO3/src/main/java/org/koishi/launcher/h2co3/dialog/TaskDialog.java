package org.koishi.launcher.h2co3.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.StringProperty;
import org.koishi.launcher.h2co3.core.utils.task.FileDownloadTask;
import org.koishi.launcher.h2co3.core.utils.task.Schedulers;
import org.koishi.launcher.h2co3.core.utils.task.TaskExecutor;
import org.koishi.launcher.h2co3.core.utils.task.TaskListener;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.utils.download.TaskCancellationAction;
import org.koishi.launcher.h2co3.utils.download.TaskListPane;

import java.util.Optional;
import java.util.function.Consumer;


public class TaskDialog extends MaterialAlertDialogBuilder implements View.OnClickListener {

    private final Consumer<FileDownloadTask.SpeedEvent> speedEventHandler;
    AlertDialog paneAlert;
    private H2CO3TextView titleView;
    private H2CO3TextView speedView;
    private MaterialButton cancelButton;
    private TaskExecutor executor;
    private TaskCancellationAction onCancel;
    private TaskListPane taskListPane;
    private ListView taskListView;

    public TaskDialog(@NonNull Context context, @NotNull TaskCancellationAction cancel) {
        super(context);
        setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_task, null);
        setView(view);
        paneAlert = this.create();

        titleView = view.findViewById(R.id.title);
        taskListView = view.findViewById(R.id.list);
        speedView = view.findViewById(R.id.speed);
        cancelButton = view.findViewById(R.id.cancel);

        setCancel(cancel);

        cancelButton.setOnClickListener(this);

        speedEventHandler = speedEvent -> {
            String unit = "B/s";
            double speed = speedEvent.getSpeed();
            if (speed > 1024) {
                speed /= 1024;
                unit = "KB/s";
            }
            if (speed > 1024) {
                speed /= 1024;
                unit = "MB/s";
            }
            double finalSpeed = speed;
            String finalUnit = unit;
            Schedulers.androidUIThread().execute(() -> {
                speedView.setText(String.format("%.1f %s", finalSpeed, finalUnit));
            });
        };
        FileDownloadTask.speedEvent.channel(FileDownloadTask.SpeedEvent.class).registerWeak(speedEventHandler);
    }

    public void setExecutor(TaskExecutor executor) {
        setExecutor(executor, true);
    }

    public void setExecutor(TaskExecutor executor, boolean autoClose) {
        this.executor = executor;

        if (executor != null) {
            if (autoClose) {
                executor.addTaskListener(new TaskListener() {
                    @Override
                    public void onStop(boolean success, TaskExecutor executor) {
                        Schedulers.androidUIThread().execute(() -> dismiss());
                    }
                });
            }

            taskListPane = new TaskListPane(getContext(), executor);
            taskListView.setAdapter(taskListPane);
        }
    }

    public StringProperty titleProperty() {
        return titleView.stringProperty();
    }

    public String getTitle() {
        return titleView.getText().toString();
    }

    public void setTitle(String currentState) {
        titleView.setString(currentState);
    }

    public void setCancel(TaskCancellationAction onCancel) {
        this.onCancel = onCancel;

        cancelButton.setEnabled(onCancel != null);
    }

    public void dismiss() {
        paneAlert.dismiss();
    }

    public void onClick(View view) {
        if (view == cancelButton) {
            Optional.ofNullable(executor).ifPresent(TaskExecutor::cancel);
            onCancel.getCancellationAction().accept(this);
            dismiss();
        }
    }
}