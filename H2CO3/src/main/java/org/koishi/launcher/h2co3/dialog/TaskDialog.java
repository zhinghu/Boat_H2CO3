package org.koishi.launcher.h2co3.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.utils.task.FileDownloadTask;
import org.koishi.launcher.h2co3.core.utils.task.Schedulers;
import org.koishi.launcher.h2co3.core.utils.task.TaskExecutor;
import org.koishi.launcher.h2co3.core.utils.task.TaskListener;
import org.koishi.launcher.h2co3.resources.component.H2CO3Button;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.utils.download.TaskCancellationAction;
import org.koishi.launcher.h2co3.utils.download.TaskListPane;

import java.util.Optional;
import java.util.function.Consumer;

public class TaskDialog extends H2CO3CustomViewDialog implements View.OnClickListener {

    private H2CO3TextView titleView;
    private H2CO3TextView speedView;
    private final Consumer<FileDownloadTask.SpeedEvent> speedEventHandler;

    private TaskExecutor executor;
    private TaskCancellationAction onCancel;
    private final ListView taskListView;

    private TaskListPane taskListPane;
    private H2CO3Button cancelButton;
    public AlertDialog alertDialog;

    @SuppressLint("DefaultLocale")
    public TaskDialog(@NonNull Context context) {
        super(context);
        setCustomView(R.layout.dialog_task);
        setCancelable(false);

        titleView = findViewById(R.id.title);
        taskListView = findViewById(R.id.list);
        speedView = findViewById(R.id.speed);
        cancelButton = findViewById(R.id.cancel);

        cancelButton.setOnClickListener(this);

        speedEventHandler = speedEvent -> {
            double speed = speedEvent.getSpeed();
            String[] units = new String[]{"B/s", "KB/s", "MB/s"};
            int unitIndex = 0;
            while (speed > 1024 && unitIndex < units.length - 1) {
                speed /= 1024;
                unitIndex++;
            }
            String finalUnit = units[unitIndex];
            double finalSpeed = speed;
            Schedulers.androidUIThread().execute(() -> {
                speedView.setText(String.format("%.1f %s", finalSpeed, finalUnit));
            });
        };
        FileDownloadTask.speedEvent.channel(FileDownloadTask.SpeedEvent.class).registerWeak(speedEventHandler);
    }

    public void setAlertDialog(AlertDialog dialog) {
        this.alertDialog = dialog;
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
                        Schedulers.androidUIThread().execute(() -> alertDialog.dismiss());
                    }
                });
            }

            taskListPane = new TaskListPane(getContext(), executor);
            taskListView.setAdapter(taskListPane);
        }
    }

    public void setCancel(TaskCancellationAction onCancel) {
        this.onCancel = onCancel;

        cancelButton.setEnabled(onCancel != null);
    }

    @Override
    public void onClick(View view) {
        if (view == cancelButton) {
            Optional.ofNullable(executor).ifPresent(TaskExecutor::cancel);
            onCancel.getCancellationAction().accept(this);
            alertDialog.dismiss();
        }
    }
}
