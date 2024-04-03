
/*
 * //
 * // Created by cainiaohh on 2024-04-04.
 * //
 */

package org.koishi.launcher.h2co3.ui.fragment.directory;

import static org.koishi.launcher.h2co3.core.H2CO3Tools.MINECRAFT_DIR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.BaseRecycleAdapter;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.file.AssetsUtils;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.launcher.utils.H2CO3GameHelper;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.ui.VanillaActivity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DirectoryFragment extends H2CO3Fragment {

    private final String h2co3Dir = MINECRAFT_DIR;
    VersionRecyclerAdapter versionRecyclerAdapter;
    private MaterialAlertDialogBuilder mDialog;
    private LinearLayout page;
    private FloatingActionButton dir, ver;
    private SearchDirAdapter mAdapter;
    private String H2CO3Dir;
    private JSONObject mJsonObj;
    @SuppressLint("HandlerLeak")
    private final Handler han = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0 -> mDialog.create().dismiss();
                case 1 -> {
                    mDialog.create().dismiss();
                    try {
                        mJsonObj.getJSONArray("dirs").put(H2CO3Dir);
                        saveJsonObj(mJsonObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mAdapter.updata(getDirList());
                    H2CO3Tools.showError(requireActivity(), getString(org.koishi.launcher.h2co3.resources.R.string.ver_add_done));
                }
                case 2 ->
                        Snackbar.make(page, getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_add_done), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
            }
        }
    };
    private RecyclerView mRecyclerView, mVerRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_directory, container, false);
        page = root.findViewById(android.R.id.content);

        H2CO3Tools.loadPaths(requireContext());

        dir = root.findViewById(R.id.ver_new_dir);
        dir.setOnClickListener(v -> showDirDialog());
        ver = root.findViewById(R.id.ver_new_ver);
        ver.setOnClickListener(v -> startActivity(new Intent(requireActivity(), VanillaActivity.class)));
        mRecyclerView = root.findViewById(R.id.mRecyclerView);
        mVerRecyclerView = root.findViewById(R.id.mVerRecyclerView);
        initViews();
        initVer();
        return root;
    }

    public void initViews() {
        mJsonObj = getJsonObj();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mAdapter = new SearchDirAdapter(getDirList(), requireActivity());
        mAdapter.setRvItemOnclickListener(position -> {
            try {
                JSONArray dirs = mJsonObj.getJSONArray("dirs");
                dirs.remove(position);
                saveJsonObj(mJsonObj);
                mAdapter.updata(getDirList());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        if (!hasData(h2co3Dir)) {
            try {
                mJsonObj.getJSONArray("dirs").put(h2co3Dir);
                saveJsonObj(mJsonObj);
                mAdapter.updata(getDirList());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    public void initVer() {
        File versionlist = new File(H2CO3GameHelper.getGameDirectory() + "/versions");
        if (versionlist.isDirectory() && versionlist.exists()) {
            try (Stream<Path> paths = Files.list(versionlist.toPath())) {
                List<String> verList = paths
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .sorted(Collator.getInstance(Locale.CHINA))
                        .collect(Collectors.toList());
                mVerRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                versionRecyclerAdapter = new VersionRecyclerAdapter(requireContext(), verList);
                mVerRecyclerView.setAdapter(versionRecyclerAdapter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mVerRecyclerView.setAdapter(null);
        }
    }

    public void showDirDialog() {
        mDialog = new MaterialAlertDialogBuilder(requireActivity());
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.custom_dialog_directory, null);
        mDialog.setView(dialogView);
        mDialog.setTitle(org.koishi.launcher.h2co3.resources.R.string.add_directory);

        MaterialButton cancel = dialogView.findViewById(R.id.custom_dir_cancel);
        MaterialButton add = dialogView.findViewById(R.id.custom_dir_ok);
        TextInputLayout lay = dialogView.findViewById(R.id.dialog_dir_lay);
        lay.setError(getString(org.koishi.launcher.h2co3.resources.R.string.ver_input_hint));
        add.setEnabled(false);
        TextInputEditText et = dialogView.findViewById(R.id.dialog_dir_name);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
            }

            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
            }

            @Override
            public void afterTextChanged(Editable p1) {
                String value = Objects.requireNonNull(et.getText()).toString();
                if (value.matches("(/storage/emulated/0|/sdcard|/mnt/sdcard).*")) {
                    lay.setErrorEnabled(false);
                    add.setEnabled(true);
                } else {
                    lay.setError(getString(org.koishi.launcher.h2co3.resources.R.string.ver_input_hint));
                    add.setEnabled(false);
                }
            }
        });

        AlertDialog dialog = mDialog.create();

        cancel.setOnClickListener(v -> dialog.dismiss());
        add.setOnClickListener(v -> {
            if (!Objects.requireNonNull(et.getText()).toString().trim().isEmpty()) {
                boolean hasData = hasData(et.getText().toString().trim());
                if (!hasData) {
                    File f = new File(et.getText().toString().trim());
                    if (f.exists()) {
                        if (f.isDirectory()) {
                            H2CO3Dir = et.getText().toString();
                            newDir();
                        } else {
                            H2CO3Tools.showError(requireActivity(), getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_not_dir));
                        }
                    } else {
                        H2CO3Dir = et.getText().toString();
                        newDir();
                    }
                } else {
                    H2CO3Tools.showError(requireActivity(), getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_already_exists));
                }
                mAdapter.updata(getDirList());
            } else {
                H2CO3Tools.showError(requireActivity(), "Please input");
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    public void newDir() {
        new Thread(() -> {
            try {
                AssetsUtils.extractZipFromAssets(requireActivity(), "pack.zip", H2CO3Dir);
                han.sendEmptyMessage(1);
            } catch (IOException e) {
                H2CO3Tools.showError(requireActivity(), getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_not_right_dir) + e);
                han.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentDir = H2CO3GameHelper.getGameDirectory();
        File f = new File(currentDir);
        if (f.exists() && f.isDirectory()) {
            initVer();
        } else {
            setDir(h2co3Dir);
            H2CO3Tools.showError(requireActivity(), getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_null_dir));
            removeDir(currentDir);
            mAdapter.updata(getDirList());
            initVer();
        }
    }

    public void setDir(String dir) {
        H2CO3GameHelper.setGameDirectory(dir);
        H2CO3GameHelper.setGameAssets(dir + "/assets/virtual/legacy");
        H2CO3GameHelper.setGameAssetsRoot(dir + "/assets");
        H2CO3GameHelper.setGameCurrentVersion(dir + "/versions");
    }

    private JSONObject getJsonObj() {
        // Load the JSON object from file or create a new one if it doesn't exist
        JSONObject jsonObj = null;
        try {
            File jsonFile = new File(requireActivity().getFilesDir(), "dirs.json");
            if (jsonFile.exists()) {
                String jsonStr = FileTools.readFile(requireActivity().getFilesDir() + "/dirs.json");
                jsonObj = new JSONObject(jsonStr);
            } else {
                jsonObj = createNewJsonObj();
                saveJsonObj(jsonObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jsonObj;
    }

    private JSONObject createNewJsonObj() {
        JSONObject jsonObj = new JSONObject();
        JSONArray dirs = new JSONArray();
        dirs.put(h2co3Dir);
        try {
            jsonObj.put("dirs", dirs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    private void saveJsonObj(JSONObject jsonObj) {
        // Save the JSON object to file
        File jsonFile = new File(requireActivity().getFilesDir(), "dirs.json");
        FileTools.writeFile(jsonFile, jsonObj.toString());
    }

    private List<String> getDirList() {
        // Get the list of directories from the JSON object
        List<String> dirList = new ArrayList<>();
        if (mJsonObj != null) {
            try {
                JSONArray dirs = mJsonObj.getJSONArray("dirs");
                for (int i = 0; i < dirs.length(); i++) {
                    dirList.add(dirs.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dirList;
    }

    public void removeDir(String dir) {
        if (mJsonObj != null) {
            try {
                JSONArray dirs = mJsonObj.getJSONArray("dirs");
                for (int i = 0; i < dirs.length(); i++) {
                    if (dirs.getString(i).equals(dir)) {
                        dirs.remove(i);
                        saveJsonObj(mJsonObj);
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean hasData(String dir) {
        // Check if the directory exists in the JSON object
        if (mJsonObj != null) {
            try {
                JSONArray dirs = mJsonObj.getJSONArray("dirs");
                for (int i = 0; i < dirs.length(); i++) {
                    if (dirs.getString(i).equals(dir)) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    class SearchDirAdapter extends BaseRecycleAdapter<String> {
        public SearchDirAdapter(List<String> datas, Context mContext) {
            super(datas, mContext);
        }

        @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
        @Override
        protected void bindData(BaseViewHolder holder, final int position) {

            TextView textView = (TextView) holder.getView(R.id.tv_record);
            TextView textView1 = (TextView) holder.getView(R.id.tv_name);
            MaterialCardView lay = (MaterialCardView) holder.getView(R.id.ver_item);
            ImageView check = (ImageView) holder.getView(R.id.ver_check_icon);
            MaterialButton del = (MaterialButton) holder.getView(R.id.tv_remove_dir);
            MaterialButton delDir = (MaterialButton) holder.getView(R.id.tv_del_dir);
            textView.setText(datas.get(position));
            if (datas.get(position).equals(H2CO3GameHelper.getGameDirectory())) {
                lay.setStrokeWidth(11);
                lay.setStrokeColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                lay.setStrokeWidth(0);
            }
            if (datas.get(position).equals(h2co3Dir)) {
                del.setVisibility(View.GONE);
                delDir.setVisibility(View.GONE);
            } else {
                del.setVisibility(View.VISIBLE);
                delDir.setVisibility(View.VISIBLE);
            }

            String str1 = textView.getText().toString();
            str1 = str1.substring(0, str1.lastIndexOf("/"));
            int idx = str1.lastIndexOf("/");
            str1 = str1.substring(idx + 1).toUpperCase();
            textView1.setText(str1);

            File f = new File(textView.getText().toString());
            if (f.isDirectory() && f.exists()) {

            } else {
                check.setImageDrawable(getResources().getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
                delDir.setVisibility(View.VISIBLE);
            }
            lay.setOnClickListener(v -> {
                if (f.exists() && f.isDirectory()) {
                    setDir(textView.getText().toString());
                    mAdapter.updata(getDirList());
                    mVerRecyclerView.setAdapter(null);
                    initVer();
                } else {
                    if (null != mRvItemOnclickListener) {
                        mRvItemOnclickListener.RvItemOnclick(position);
                        mAdapter.updata(getDirList());
                        H2CO3Tools.showError(requireActivity(), getResources().getString(org.koishi.launcher.h2co3.resources.R.string.ver_null_dir));
                        mVerRecyclerView.setAdapter(null);
                    }
                }

            });
            //
            del.setOnClickListener(view -> {

                if (null != mRvItemOnclickListener) {
                    mRvItemOnclickListener.RvItemOnclick(position);
                }
            });

            delDir.setOnClickListener(view -> {
                if (null != mRvItemOnclickListener) {
                    if (datas.get(position).equals(H2CO3GameHelper.getGameDirectory())) {
                        setDir(h2co3Dir);
                    }
                    //添加"Yes"按钮
                    //添加"Yes"按钮
                    AlertDialog alertDialog1 = new MaterialAlertDialogBuilder(requireActivity())
                            .setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_action))//标题
                            .setMessage(org.koishi.launcher.h2co3.resources.R.string.ver_if_del)
                            .setPositiveButton("Yes Yes Yes", (dialogInterface, i) -> {
                                File f1 = new File(datas.get(position));
                                //TODO
                                mRvItemOnclickListener.RvItemOnclick(position);
                                mAdapter.updata(getDirList());
                                new Thread(() -> {
                                    //String file2= "/data/data/org.koishi.launcher.h2co3/app_runtime";
                                    deleteDirWihtFile(f1);
                                }).start();

                            })
                            .setNegativeButton("No No No", (dialogInterface, i) -> {
                                //TODO
                            })
                            .create();

                    alertDialog1.show();
                }
            });

        }


        public void deleteDirWihtFile(File dir) {
            if (dir == null || !dir.exists() || !dir.isDirectory())
                return;
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.isFile())
                    file.delete(); // 删除所有文件
                else if (file.isDirectory())
                    deleteDirWihtFile(file); // 递规的方式删除文件夹
            }
            dir.delete();// 删除目录本身
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_dir;
        }

        public void setDir(String dir) {
            H2CO3GameHelper.setGameDirectory(dir);
            H2CO3GameHelper.setGameAssets(dir + "/assets/virtual/legacy");
            H2CO3GameHelper.setGameAssetsRoot(dir + "/assets");
            H2CO3GameHelper.setGameCurrentVersion(dir + "/versions");
        }
    }

    class VersionRecyclerAdapter extends RecyclerView.Adapter<VersionRecyclerAdapter.MyViewHolder> {
        private final List<String> datas;
        private final LayoutInflater inflater;

        public VersionRecyclerAdapter(Context context, List<String> datas) {
            inflater = LayoutInflater.from(context);
            this.datas = datas;
        }

        //创建每一行的View 用RecyclerView.ViewHolder包装
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View itemView = inflater.inflate(R.layout.item_version_local, null);
            return new MyViewHolder(itemView);
        }

        //给每一行View填充数据
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.textview.setText(datas.get(position));
            File f = new File(H2CO3GameHelper.getGameDirectory() + "/versions/" + datas.get(position));
            String verF = H2CO3GameHelper.getGameDirectory() + "/versions/" + datas.get(position);
            if (verF.equals(H2CO3GameHelper.getGameCurrentVersion())) {
                holder.rl.setStrokeWidth(11);
                holder.rl.setStrokeColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                holder.rl.setStrokeWidth(0);
            }
            if (f.isDirectory() && f.exists()) {
            } else {
                holder.rl.setEnabled(false);
                holder.ic.setImageDrawable(getResources().getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.xicon));
            }
            holder.rl.setOnClickListener(v -> {
                holder.dirs = datas.get(position);
                showExecDialog(holder.dirs);
            });

            if (holder.rl.getTag() == null) {
                holder.rl.setTag(true);
                holder.rl.setOnClickListener(v -> {
                    if (f.exists() && f.isDirectory()) {
                        versionRecyclerAdapter.notifyItemChanged(position);
                        H2CO3GameHelper.setGameCurrentVersion(verF);
                        mVerRecyclerView.setAdapter(versionRecyclerAdapter);
                        if (verF.equals(H2CO3GameHelper.getGameCurrentVersion())) {
                            holder.rl.setStrokeWidth(15);
                            holder.rl.setStrokeColor(getResources().getColor(android.R.color.darker_gray));
                            holder.rl.setElevation(5);
                        } else {
                            holder.rl.setStrokeWidth(0);
                        }
                    }
                });
            }

            holder.btn.setOnClickListener(v -> {
                //添加"Yes"按钮
                //添加"Yes"按钮
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
                alertDialogBuilder.setTitle(getResources().getString(org.koishi.launcher.h2co3.resources.R.string.title_action));
                alertDialogBuilder.setMessage(org.koishi.launcher.h2co3.resources.R.string.ver_if_del);
                alertDialogBuilder.setPositiveButton("Yes Yes Yes", (dialogInterface, i) -> {
                    holder.btn.setVisibility(View.INVISIBLE);
                    holder.textview.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    holder.rl.setEnabled(false);
                    File f1 = new File(H2CO3GameHelper.getGameDirectory() + "/versions/" + datas.get(position));
                    new Thread(() -> {
                        if (f1.isDirectory()) {
                            deleteDirWihtFile(f1);
                        } else {
                            deleteFile(H2CO3GameHelper.getGameDirectory() + "/versions/" + datas.get(position));
                        }
                        han.sendEmptyMessage(2);
                    }).start();
                });
                alertDialogBuilder.setNegativeButton("No No No", (dialogInterface, i) -> {
                });
                AlertDialog alertDialog1 = alertDialogBuilder.create();
                alertDialog1.show();
            });
        }

        public void showExecDialog(String dir) {
        }

        //数据源的数量
        @Override
        public int getItemCount() {
            return datas.size();
        }

        public void deleteDirWihtFile(File dir) {
            if (dir == null || !dir.exists() || !dir.isDirectory())
                return;
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.isFile())
                    file.delete(); // 删除所有文件
                else if (file.isDirectory())
                    deleteDirWihtFile(file); // 递规的方式删除文件夹
            }
            dir.delete();// 删除目录本身
        }

        public void deleteFile(String filePath) {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private final TextView textview;
            private final MaterialButton btn;
            private final ImageView ic;
            private final MaterialCardView rl;
            private String dirs;

            public MyViewHolder(View itemView) {
                super(itemView);
                textview = itemView.findViewById(R.id.ver_name);
                btn = itemView.findViewById(R.id.ver_remove);
                rl = itemView.findViewById(R.id.ver_item);
                ic = itemView.findViewById(R.id.ver_icon);
            }
        }
    }
}