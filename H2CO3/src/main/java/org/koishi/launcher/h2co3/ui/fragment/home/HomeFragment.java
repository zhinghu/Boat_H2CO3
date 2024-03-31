/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.ui.fragment.home;

import static org.koishi.launcher.h2co3.core.H2CO3Auth.serversFile;
import static org.koishi.launcher.h2co3.core.H2CO3Auth.usersFile;
import static org.koishi.launcher.h2co3.ui.H2CO3LauncherClientActivity.attachControllerInterface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.circularreveal.CircularRevealFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.adapter.HomeAdapterListUser;
import org.koishi.launcher.h2co3.application.H2CO3Application;
import org.koishi.launcher.h2co3.core.H2CO3Auth;
import org.koishi.launcher.h2co3.core.H2CO3Loader;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.bean.UserBean;
import org.koishi.launcher.h2co3.core.login.other.AuthResult;
import org.koishi.launcher.h2co3.core.login.other.LoginUtils;
import org.koishi.launcher.h2co3.core.login.other.Servers;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.component.H2CO3Button;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3ProgressDialog;
import org.koishi.launcher.h2co3.ui.H2CO3LauncherClientActivity;
import org.koishi.launcher.h2co3.ui.MicrosoftLoginActivity;
import org.koishi.launcher.h2co3.utils.HomeLoginHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author caini
 */
public class HomeFragment extends H2CO3Fragment implements View.OnClickListener {

    private static final int MICROSOFT_LOGIN_REQUEST_CODE = 1001;
    private final Gson GLOBAL_GSON = new GsonBuilder().setPrettyPrinting().create();
    private final HomeLoginHandler loginHandler = new HomeLoginHandler(HomeFragment.this);
    public AlertDialog loginDialogAlert;
    public H2CO3ProgressDialog progressDialog;
    public H2CO3TextView homeUserName;
    public H2CO3TextView homeUserState;
    public AppCompatImageView homeUserIcon;
    public RecyclerView recyclerView;
    H2CO3Button homeGamePlayButton;
    private HomeAdapterListUser adapterUser;
    private CircularRevealFrameLayout loginNameLayout;
    private TextInputEditText loginName, loginPassword;
    private ConstraintLayout loginApi;
    private TextInputLayout loginPasswordLayout;
    private H2CO3Button login, homeUserListButton;
    private H2CO3CustomViewDialog loginDialog;
    private List<UserBean> userList = new ArrayList<>();
    private Spinner serverSpinner;
    private H2CO3Button register;
    private Servers servers;
    private String currentBaseUrl;
    private String currentRegisterUrl;
    private H2CO3CardView homeUserListLayout;
    private ArrayAdapter<String> serverSpinnerAdapter;
    private MaterialAlertDialogBuilder alertDialogBuilder;
    private boolean isLoginDialogShowing = false;
    private String user;
    private String pass;


    View view;
    private final LoginUtils.Listener loginUtilsListener = new LoginUtils.Listener() {

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSuccess(AuthResult authResult) {
            requireActivity().runOnUiThread(() -> {
                progressDialog.dismiss();
                if (authResult.getSelectedProfile() != null) {
                    H2CO3Auth.addUserToJson(authResult.getSelectedProfile().getName(), user, pass, "2", currentBaseUrl, authResult.getSelectedProfile().getId(), UUID.randomUUID().toString(), "0", authResult.getAccessToken(), "0", "0", true, false);
                    reLoadUser();
                    loginDialogAlert.dismiss();
                } else {
                    String[] items = authResult.getAvailableProfiles().stream().map(AuthResult.AvailableProfiles::getName).toArray(String[]::new);
                    alertDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
                    alertDialogBuilder.setTitle("请选择角色");
                    alertDialogBuilder.setItems(items, (dialog, which) -> {
                        AuthResult.AvailableProfiles selectedProfile = authResult.getAvailableProfiles().get(which);
                        H2CO3Auth.addUserToJson(selectedProfile.getName(), user, pass, "2", currentBaseUrl, selectedProfile.getId(), UUID.randomUUID().toString(), "0", authResult.getAccessToken(), "0", "0", true, false);
                        reLoadUser();
                        loginDialogAlert.dismiss();
                    });
                    alertDialogBuilder.setNegativeButton(requireActivity().getString(org.koishi.launcher.h2co3.resources.R.string.button_cancel), null);
                    alertDialogBuilder.show();
                }
            });
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onFailed(String error) {
            requireActivity().runOnUiThread(() -> {
                progressDialog.dismiss();
                adapterUser.notifyDataSetChanged();
                loginDialogAlert.dismiss();
                alertDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
                alertDialogBuilder.setTitle(org.koishi.launcher.h2co3.resources.R.string.title_warn);
                alertDialogBuilder.setMessage(error);
                alertDialogBuilder.setPositiveButton(requireActivity().getString(org.koishi.launcher.h2co3.resources.R.string.button_ok), null);
                alertDialogBuilder.show();
            });
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        findView();
        init();
        return view;
    }

    private void findView() {
        homeGamePlayButton = findViewById(view, R.id.home_game_play_button);
        homeGamePlayButton.setOnClickListener(this);
        homeUserName = findViewById(view, R.id.home_user_name);
        homeUserState = findViewById(view, R.id.home_user_state);
        homeUserIcon = findViewById(view, R.id.home_user_icon);
        homeUserListButton = findViewById(view, R.id.home_user_open_list);
        homeUserListButton.setOnClickListener(this);
        homeUserListLayout = findViewById(view, R.id.home_user_list_layout);
        recyclerView = findViewById(view, R.id.recycler_view_user_list);
    }

    private void init() {
        if (TextUtils.isEmpty(H2CO3Auth.getUserJson()) || "{}".equals(H2CO3Auth.getUserJson())) {
            if ("{}".equals(H2CO3Auth.getUserJson())) {
                setDefaultUserState();
            } else {
                FileTools.writeFile(usersFile, "{}");
                setDefaultUserState();
            }
        } else {
            setUserStateFromJson();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        reLoadUser();

        View contentView1 = LayoutInflater.from(requireActivity()).inflate(R.layout.item_user_add, null);
        H2CO3CardView userAdd = contentView1.findViewById(R.id.login_user_add);
        userAdd.setOnClickListener(v1 -> showLoginDialog());
    }

    @Override
    public void onClick(View v) {
        if (v == homeGamePlayButton) {
            startActivity(new Intent(requireActivity(), H2CO3LauncherClientActivity.class));
            attachControllerInterface();
        } else if (v == homeUserListButton) {
            if (homeUserListLayout.getVisibility() == View.GONE) {
                homeUserListLayout.setVisibility(View.VISIBLE);
            } else {
                homeUserListLayout.setVisibility(View.GONE);
            }
        }
    }

    public void showLoginDialog() {
        if (isLoginDialogShowing) {
            return;
        }

        isLoginDialogShowing = true;

        loginDialog = new H2CO3CustomViewDialog(requireActivity());
        loginDialog.setCustomView(R.layout.custom_dialog_login);
        loginDialog.setTitle(getString(org.koishi.launcher.h2co3.resources.R.string.title_activity_login));

        loginDialogAlert = loginDialog.create();
        loginDialogAlert.show();
        loginDialog.setOnDismissListener(dialog -> isLoginDialogShowing = false);
        loginDialogAlert.setOnDismissListener(dialog -> isLoginDialogShowing = false);

        loginName = loginDialog.findViewById(R.id.login_name);
        loginPassword = loginDialog.findViewById(R.id.login_password);
        loginApi = loginDialog.findViewById(R.id.server_selector);
        loginNameLayout = loginDialog.findViewById(R.id.login_name_layout);
        loginPasswordLayout = loginDialog.findViewById(R.id.login_password_layout);
        login = loginDialog.findViewById(R.id.login);
        progressDialog = new H2CO3ProgressDialog(requireActivity());
        progressDialog.setCancelable(false);

        serverSpinner = loginDialog.findViewById(R.id.server_spinner);
        register = loginDialog.findViewById(R.id.register);
        TabLayout tab = loginDialog.findViewById(R.id.login_tab);
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1:
                        loginNameLayout.setVisibility(View.GONE);
                        loginPasswordLayout.setVisibility(View.GONE);
                        loginApi.setVisibility(View.GONE);
                        break;
                    case 2:
                        loginNameLayout.setVisibility(View.VISIBLE);
                        loginPasswordLayout.setVisibility(View.VISIBLE);
                        loginApi.setVisibility(View.VISIBLE);
                        break;
                    case 0:
                    default:
                        loginNameLayout.setVisibility(View.VISIBLE);
                        loginPasswordLayout.setVisibility(View.GONE);
                        loginApi.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        login.setOnClickListener(p1 -> {
            String text = loginName.getText().toString();
            int selectedTabPosition = tab.getSelectedTabPosition();
            switch (selectedTabPosition) {
                case 1:
                    startActivityForResult(new Intent(requireActivity(), MicrosoftLoginActivity.class), MICROSOFT_LOGIN_REQUEST_CODE);
                    break;
                case 2:
                    progressDialog.showWithProgress();
                    H2CO3Application.sExecutorService.execute(() -> {
                        user = loginName.getText().toString();
                        pass = loginPassword.getText().toString();
                        if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pass)) {
                            try {
                                LoginUtils.getINSTANCE().setBaseUrl(currentBaseUrl);
                                LoginUtils.getINSTANCE().login(user, pass, loginUtilsListener);
                            } catch (IOException e) {
                                requireActivity().runOnUiThread(() -> {
                                });
                            }
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                progressDialog.dismiss();
                            });
                        }
                    });
                    break;
                case 0:
                default:
                    if (isValidUsername(text)) {
                        H2CO3Auth.addUserToJson(text, "0", "0", "0", "0", "0", UUID.randomUUID().toString(), "0", "0", "0", "0", true, false);
                        reLoadUser();
                        loginDialogAlert.dismiss();
                    }
            }
        });
        refreshServer();
        serverSpinner.setAdapter(serverSpinnerAdapter);
        register.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
            alertDialogBuilder.setTitle("请选择认证服务器类型");
            alertDialogBuilder.setItems(new String[]{"外置登录", "统一通行证"}, (dialog, which) -> {
                EditText editText = new EditText(requireActivity());
                editText.setMaxLines(1);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                MaterialAlertDialogBuilder inputDialogBuilder = new MaterialAlertDialogBuilder(requireActivity());
                inputDialogBuilder.setTitle("提示");
                inputDialogBuilder.setView(editText);
                inputDialogBuilder.setPositiveButton(this.getString(org.koishi.launcher.h2co3.resources.R.string.button_ok), (dialogInterface, i) -> {
                    progressDialog.showWithProgress();
                    H2CO3Application.sExecutorService.execute(() -> {
                        String baseUrl = which == 0 ? editText.getText().toString() : "https://auth.mc-user.com:233/" + editText.getText().toString();
                        String data = LoginUtils.getINSTANCE().getServeInfo(baseUrl);
                        requireActivity().runOnUiThread(() -> {
                            progressDialog.dismiss();
                            if (data != null) {
                                try {
                                    Servers.Server server = new Servers.Server();
                                    JSONObject jsonObject = new JSONObject(data);
                                    JSONObject meta = jsonObject.optJSONObject("meta");
                                    if (meta != null) {
                                        server.setServerName(meta.optString("serverName"));
                                    }
                                    server.setBaseUrl(editText.getText().toString());
                                    if (which == 0) {
                                        JSONObject links = null;
                                        if (meta != null) {
                                            links = meta.optJSONObject("links");
                                        }
                                        if (links != null) {
                                            server.setRegister(links.optString("register"));
                                        }
                                    } else {
                                        server.setBaseUrl("https://auth.mc-user.com:233/" + editText.getText().toString());
                                        server.setRegister("https://login.mc-user.com:233/" + editText.getText().toString() + "/loginreg");
                                    }
                                    Optional.ofNullable(servers)
                                            .orElseGet(() -> {
                                                servers = new Servers();
                                                servers.setServer(new ArrayList<>());
                                                return servers;
                                            });
                                    servers.getServer().add(server);
                                    H2CO3Tools.write(serversFile.getAbsolutePath(), GLOBAL_GSON.toJson(servers, Servers.class));
                                    refreshServer();
                                    currentBaseUrl = server.getBaseUrl();
                                    currentRegisterUrl = server.getRegister();
                                } catch (Exception ignored) {
                                }
                            }
                        });
                    });
                });
                inputDialogBuilder.setNegativeButton(this.getString(org.koishi.launcher.h2co3.resources.R.string.button_cancel), null);
                inputDialogBuilder.show();
            });
            alertDialogBuilder.setNegativeButton(this.getString(org.koishi.launcher.h2co3.resources.R.string.button_cancel), null);
            alertDialogBuilder.show();
        });
    }

    public void refreshServer() {
        List<String> serverList = new ArrayList<>();
        if (serversFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(serversFile))) {
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                servers = new Gson().fromJson(json.toString(), Servers.class);
                if (servers != null) {
                    currentBaseUrl = servers.getServer().get(0).getBaseUrl();
                    for (Servers.Server server : servers.getServer()) {
                        serverList.add(server.getServerName());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (servers == null) {
            serverList.add("无认证服务器");
        }
        if (serverSpinnerAdapter == null) {
            serverSpinnerAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, serverList);
            serverSpinner.setAdapter(serverSpinnerAdapter);
        } else {
            serverSpinnerAdapter.clear();
            serverSpinnerAdapter.addAll(serverList);
            serverSpinnerAdapter.notifyDataSetChanged();
        }
    }

    private boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username) && username.length() >= 3 && username.length() <= 16 && username.matches("\\w+");
    }

    public void reLoadUser() {
        userList.clear();
        try {
            H2CO3Auth.parseJsonToUser(new JSONObject(H2CO3Auth.getUserJson()));
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
        userList = H2CO3Auth.getUserList();
        adapterUser = new HomeAdapterListUser(this, userList);
        recyclerView.setAdapter(adapterUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MICROSOFT_LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            progressDialog.showWithProgress();
            progressDialog.setCancelable(false);
            loginHandler.login(data);
        }
    }

    private void setUserStateFromJson() {
        String apiUrl = H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_API_URL, H2CO3Tools.LOGIN_ERROR, String.class);
        homeUserName.setText(H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_AUTH_PLAYER_NAME, "", String.class));
        String userType = H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_USER_TYPE, "0", String.class);
        String userSkinTexture = H2CO3Tools.getH2CO3Value(H2CO3Tools.LOGIN_USER_SKINTEXTURE, "", String.class);

        final String MICROSOFT_USER_STATE = getString(org.koishi.launcher.h2co3.resources.R.string.user_state_microsoft);
        final String OTHER_USER_STATE = getString(org.koishi.launcher.h2co3.resources.R.string.user_state_other);
        final String OFFLINE_USER_STATE = getString(org.koishi.launcher.h2co3.resources.R.string.user_state_offline);

        switch (userType) {
            case "1":
                homeUserState.setText(MICROSOFT_USER_STATE);
                H2CO3Loader.getHead(requireActivity(), userSkinTexture, homeUserIcon);
                break;
            case "2":
                homeUserState.setText(OTHER_USER_STATE + apiUrl);
                homeUserIcon.setImageDrawable(ContextCompat.getDrawable(requireActivity(), org.koishi.launcher.h2co3.resources.R.drawable.ic_home_user));
                break;
            default:
                homeUserState.setText(OFFLINE_USER_STATE);
                homeUserIcon.setImageDrawable(ContextCompat.getDrawable(requireActivity(), org.koishi.launcher.h2co3.resources.R.drawable.ic_home_user));
                break;
        }
        if (TextUtils.isEmpty(homeUserName.getText())) {
            setDefaultUserState();
        }
    }

    private void setDefaultUserState() {
        homeUserName.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        homeUserState.setText(getString(org.koishi.launcher.h2co3.resources.R.string.user_add));
        homeUserIcon.setImageDrawable(ContextCompat.getDrawable(requireActivity(), org.koishi.launcher.h2co3.resources.R.drawable.xicon));
    }
}