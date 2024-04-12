package org.koishi.launcher.h2co3.utils.download;


import static org.koishi.launcher.h2co3.core.utils.AndroidUtils.getLocalizedText;
import static org.koishi.launcher.h2co3.core.utils.Lang.tryCast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatImageView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.download.fabric.FabricAPIInstallTask;
import org.koishi.launcher.h2co3.core.download.fabric.FabricInstallTask;
import org.koishi.launcher.h2co3.core.download.forge.ForgeNewInstallTask;
import org.koishi.launcher.h2co3.core.download.forge.ForgeOldInstallTask;
import org.koishi.launcher.h2co3.core.download.game.GameAssetDownloadTask;
import org.koishi.launcher.h2co3.core.download.game.GameInstallTask;
import org.koishi.launcher.h2co3.core.download.liteloader.LiteLoaderInstallTask;
import org.koishi.launcher.h2co3.core.download.neoforge.NeoForgeInstallTask;
import org.koishi.launcher.h2co3.core.download.neoforge.NeoForgeOldInstallTask;
import org.koishi.launcher.h2co3.core.download.optifine.OptiFineInstallTask;
import org.koishi.launcher.h2co3.core.game.mod.MinecraftInstanceTask;
import org.koishi.launcher.h2co3.core.game.mod.ModpackInstallTask;
import org.koishi.launcher.h2co3.core.game.mod.ModpackUpdateTask;
import org.koishi.launcher.h2co3.core.game.mod.curse.CurseCompletionTask;
import org.koishi.launcher.h2co3.core.game.mod.curse.CurseInstallTask;
import org.koishi.launcher.h2co3.core.game.mod.mcbbs.McbbsModpackCompletionTask;
import org.koishi.launcher.h2co3.core.game.mod.mcbbs.McbbsModpackExportTask;
import org.koishi.launcher.h2co3.core.game.mod.modrinth.ModrinthCompletionTask;
import org.koishi.launcher.h2co3.core.game.mod.modrinth.ModrinthInstallTask;
import org.koishi.launcher.h2co3.core.game.mod.multimc.MultiMCModpackExportTask;
import org.koishi.launcher.h2co3.core.game.mod.multimc.MultiMCModpackInstallTask;
import org.koishi.launcher.h2co3.core.game.mod.server.ServerModpackCompletionTask;
import org.koishi.launcher.h2co3.core.game.mod.server.ServerModpackExportTask;
import org.koishi.launcher.h2co3.core.game.mod.server.ServerModpackLocalInstallTask;
import org.koishi.launcher.h2co3.core.utils.ConvertUtils;
import org.koishi.launcher.h2co3.core.utils.Lang;
import org.koishi.launcher.h2co3.core.utils.StringUtils;
import org.koishi.launcher.h2co3.core.utils.task.Schedulers;
import org.koishi.launcher.h2co3.core.utils.task.Task;
import org.koishi.launcher.h2co3.core.utils.task.TaskExecutor;
import org.koishi.launcher.h2co3.core.utils.task.TaskListener;
import org.koishi.launcher.h2co3.resources.component.H2CO3LinearProgress;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TaskListPane extends BaseAdapter {

    private final TaskExecutor executor;
    private final Map<Task<?>, ProgressListNode> nodes = new HashMap<>();
    private final List<StageNode> stageNodes = new ArrayList<>();
    private final ArrayList<View> listBox = new ArrayList<>();

    private final Context context;

    public TaskListPane(Context context, TaskExecutor taskExecutor) {
        super();
        this.context = context;
        this.executor = taskExecutor;
        setExecutor(taskExecutor);
    }

    private static String getTaskName(Context context, Task<?> task) {
        String taskName = task.getName();
        if (task instanceof GameAssetDownloadTask) {
            taskName = getLocalizedText(context, "assets_download_all");
        } else if (task instanceof GameInstallTask) {
            taskName = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_game"));
        } else if (task instanceof ForgeNewInstallTask || task instanceof ForgeOldInstallTask) {
            taskName = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_forge"));
        } else if (task instanceof NeoForgeInstallTask || task instanceof NeoForgeOldInstallTask) {
            taskName = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_neoforge"));
        } else if (task instanceof LiteLoaderInstallTask) {
            taskName = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_liteloader"));
        } else if (task instanceof OptiFineInstallTask) {
            taskName = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_optifine"));
        } else if (task instanceof FabricInstallTask) {
            taskName = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_fabric"));
        } else if (task instanceof FabricAPIInstallTask) {
            taskName = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_fabric_api"));
        } else if (task instanceof CurseCompletionTask || task instanceof ModrinthCompletionTask || task instanceof ServerModpackCompletionTask || task instanceof McbbsModpackCompletionTask) {
            taskName = getLocalizedText(context, "modpack_completion");
        } else if (task instanceof ModpackInstallTask) {
            taskName = getLocalizedText(context, "modpack_installing");
        } else if (task instanceof ModpackUpdateTask) {
            taskName = getLocalizedText(context, "modpack_update");
        } else if (task instanceof CurseInstallTask) {
            taskName = getLocalizedText(context, "modpack_install", getLocalizedText(context, "modpack_type_curse"));
        } else if (task instanceof MultiMCModpackInstallTask) {
            taskName = getLocalizedText(context, "modpack_install", getLocalizedText(context, "modpack_type_multimc"));
        } else if (task instanceof ModrinthInstallTask) {
            taskName = getLocalizedText(context, "modpack_install", getLocalizedText(context, "modpack_type_modrinth"));
        } else if (task instanceof ServerModpackLocalInstallTask) {
            taskName = getLocalizedText(context, "modpack_install", getLocalizedText(context, "modpack_type_server"));
        } else if (task instanceof McbbsModpackExportTask || task instanceof MultiMCModpackExportTask || task instanceof ServerModpackExportTask) {
            taskName = getLocalizedText(context, "modpack_export");
        } else if (task instanceof MinecraftInstanceTask) {
            taskName = getLocalizedText(context, "modpack_scan");
        }
        return taskName;
    }

    @Override
    public int getCount() {
        return listBox.size();
    }

    @Override
    public Object getItem(int i) {
        return listBox.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return listBox.get(i);
    }

    private void setExecutor(TaskExecutor executor) {
        List<String> stages = Lang.removingDuplicates(executor.getStages());
        executor.addTaskListener(new TaskListener() {
            @Override
            public void onStart() {
                Schedulers.androidUIThread().execute(() -> {
                    stageNodes.clear();
                    stageNodes.addAll(stages.stream().map(it -> new StageNode(context, it)).collect(Collectors.toList()));
                    stageNodes.forEach(stageNode -> listBox.add(stageNode.getView()));
                    notifyDataSetChanged();
                });
            }

            @Override
            public void onReady(Task<?> task) {
                if (task.getStage() != null) {
                    Schedulers.androidUIThread().execute(() -> stageNodes.stream()
                            .filter(x -> x.stage.equals(task.getStage()))
                            .findAny()
                            .ifPresent(StageNode::begin));
                }
            }

            @Override
            public void onRunning(Task<?> task) {
                if (!task.getSignificance().shouldShow() || task.getName() == null)
                    return;

                task.setName(getTaskName(context, task));

                Schedulers.androidUIThread().execute(() -> {
                    StageNode stageNode = stageNodes.stream().filter(x -> x.stage.equals(task.getInheritedStage())).findAny().orElse(null);
                    ProgressListNode node = new ProgressListNode(context, stageNode != null && stageNodes.contains(stageNode), task);
                    nodes.put(task, node);
                    listBox.add(stageNode == null || !stageNodes.contains(stageNode) ? 0 : listBox.indexOf(stageNode.getView()) + 1, node.getView());
                    notifyDataSetChanged();
                });
            }

            @Override
            public void onFinished(Task<?> task) {
                if (task.getStage() != null) {
                    Schedulers.androidUIThread().execute(() -> stageNodes.stream().filter(x -> x.stage.equals(task.getStage())).findAny().ifPresent(StageNode::succeed));
                }

                Schedulers.androidUIThread().execute(() -> {
                    ProgressListNode node = nodes.remove(task);
                    if (node == null)
                        return;
                    node.unbind();
                    listBox.remove(node.getView());
                    notifyDataSetChanged();
                });
            }

            @Override
            public void onFailed(Task<?> task, Throwable throwable) {
                if (task.getStage() != null) {
                    Schedulers.androidUIThread().execute(() -> stageNodes.stream().filter(x -> x.stage.equals(task.getStage())).findAny().ifPresent(StageNode::fail));
                }
                ProgressListNode node = nodes.remove(task);
                if (node == null)
                    return;
                Schedulers.androidUIThread().execute(() -> node.setThrowable(throwable));
            }

            @Override
            public void onPropertiesUpdate(Task<?> task) {
                if (task instanceof Task.CountTask) {
                    Schedulers.androidUIThread().execute(() -> stageNodes.stream()
                            .filter(x -> x.stage.equals(((Task<?>.CountTask) task).getCountStage()))
                            .findAny()
                            .ifPresent(StageNode::count));

                    return;
                }

                if (task.getStage() != null) {
                    Schedulers.androidUIThread().execute(() -> {
                        int total = tryCast(task.getProperties().get("total"), Integer.class).orElse(0);
                        stageNodes.stream()
                                .filter(x -> x.stage.equals(task.getStage()))
                                .findAny()
                                .ifPresent(stageNode -> stageNode.setTotal(total));
                    });
                }
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private static class StageNode {
        private final Context context;
        private final String stage;
        private final String message;
        private final View parent;
        private final H2CO3TextView title;
        private final AppCompatImageView icon;
        private int count = 0;
        private int total = 0;
        private boolean started = false;

        public StageNode(Context context, String stage) {
            this.context = context;
            this.stage = stage;

            parent = LayoutInflater.from(context).inflate(R.layout.item_downloading_state, null);
            title = parent.findViewById(R.id.title);
            icon = parent.findViewById(R.id.icon);

            String stageKey = StringUtils.substringBefore(stage, ':');
            String stageValue = StringUtils.substringAfter(stage, ':');

            // @formatter:off
            switch (stageKey) {
                case "h2co3.modpack": message = getLocalizedText(context, "install_modpack"); break;
                case "h2co3.modpack.download": message = getLocalizedText(context, "launch_state_modpack"); break;
                case "h2co3.install.assets": message = getLocalizedText(context, "assets_download"); break;
                case "h2co3.install.game": message = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_game") + " " + stageValue); break;
                case "h2co3.install.forge": message = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_forge") + " " + stageValue); break;
                case "h2co3.install.neoforge": message = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_neoforge") + " " + stageValue); break;
                case "h2co3.install.liteloader": message = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_liteloader") + " " + stageValue); break;
                case "h2co3.install.optifine": message = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_optifine") + " " + stageValue); break;
                case "h2co3.install.fabric": message = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_fabric") + " " + stageValue); break;
                case "h2co3.install.fabric-api": message = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_fabric-api") + " " + stageValue); break;
                case "h2co3.install.quilt": message = getLocalizedText(context, "install_installer_install", getLocalizedText(context, "install_installer_quilt") + " " + stageValue); break;
                default: message = getLocalizedText(context, stageKey.replace(".", "_").replace("-", "_")); break;
            }
            // @formatter:on

            title.setText(message);
            icon.setImageDrawable(context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_update));
        }

        public void begin() {
            if (started) return;
            started = true;
            icon.setImageDrawable(context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_arrow_right_black));
        }

        public void fail() {
            icon.setImageDrawable(context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
        }

        public void succeed() {
            icon.setImageDrawable(context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_baseline_done_24));
        }

        public void count() {
            updateCounter(++count, total);
        }

        public void setTotal(int total) {
            this.total = total;
            updateCounter(count, total);
        }

        @SuppressLint("DefaultLocale")
        public void updateCounter(int count, int total) {
            if (total > 0)
                title.setText(String.format("%s - %d/%d", message, count, total));
            else
                title.setText(message);
        }

        public View getView() {
            return parent;
        }
    }

    private static class ProgressListNode {
        private final View parent;

        private final H2CO3LinearProgress bar;
        private final H2CO3TextView state;

        public ProgressListNode(Context context, boolean padding, Task<?> task) {
            parent = LayoutInflater.from(context).inflate(R.layout.item_download, null);
            if (padding) {
                parent.setPadding(ConvertUtils.dip2px(context, 31), 0, 0, ConvertUtils.dip2px(context, 8));
            }

            bar = parent.findViewById(R.id.fileProgress);
            H2CO3TextView title = parent.findViewById(R.id.fileNameText);
            state = parent.findViewById(R.id.state);

            bar.percentProgressProperty().bind(task.progressProperty());
            title.setText(task.getName());
            state.stringProperty().bind(task.messageProperty());
        }

        public void unbind() {
            bar.percentProgressProperty().unbind();
            state.stringProperty().unbind();
        }

        public void setThrowable(Throwable throwable) {
            unbind();
            state.setText(throwable.getLocalizedMessage());
            bar.setProgress(0);
        }

        public View getView() {
            return parent;
        }
    }
}