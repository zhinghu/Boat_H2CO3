package org.koishi.launcher.h2co3.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatImageView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.download.RemoteVersion;
import org.koishi.launcher.h2co3.core.download.fabric.FabricAPIRemoteVersion;
import org.koishi.launcher.h2co3.core.download.fabric.FabricRemoteVersion;
import org.koishi.launcher.h2co3.core.download.forge.ForgeRemoteVersion;
import org.koishi.launcher.h2co3.core.download.game.GameRemoteVersion;
import org.koishi.launcher.h2co3.core.download.liteloader.LiteLoaderRemoteVersion;
import org.koishi.launcher.h2co3.core.download.neoforge.NeoForgeRemoteVersion;
import org.koishi.launcher.h2co3.core.download.optifine.OptiFineRemoteVersion;
import org.koishi.launcher.h2co3.core.download.quilt.QuiltAPIRemoteVersion;
import org.koishi.launcher.h2co3.core.download.quilt.QuiltRemoteVersion;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;

import java.util.ArrayList;

public class RemoteVersionListAdapter extends BaseAdapter {

    private final ArrayList<RemoteVersion> list;
    private final OnRemoteVersionSelectListener listener;
    private final Context context;

    public RemoteVersionListAdapter(Context context, ArrayList<RemoteVersion> list, OnRemoteVersionSelectListener listener) {
        super();
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(this.context).inflate(R.layout.item_remote_version, null);
            viewHolder.parent = view.findViewById(R.id.parent);
            viewHolder.icon = view.findViewById(R.id.icon);
            viewHolder.version = view.findViewById(R.id.version);
            viewHolder.tag = view.findViewById(R.id.tag);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        RemoteVersion remoteVersion = list.get(i);
        viewHolder.parent.setOnClickListener(view1 -> listener.onSelect(remoteVersion));
        viewHolder.icon.setBackground(getIcon(remoteVersion));
        viewHolder.version.setText(remoteVersion.getSelfVersion());
        viewHolder.tag.setText(getTag(remoteVersion));
        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getIcon(RemoteVersion remoteVersion) {
        if (remoteVersion instanceof LiteLoaderRemoteVersion)
            return this.context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_liteloader);
        else if (remoteVersion instanceof OptiFineRemoteVersion)
            return this.context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_optifine);
        else if (remoteVersion instanceof ForgeRemoteVersion)
            return this.context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_forge);
        else if (remoteVersion instanceof NeoForgeRemoteVersion)
            return this.context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_neoforge);
        else if (remoteVersion instanceof FabricRemoteVersion || remoteVersion instanceof FabricAPIRemoteVersion)
            return this.context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_fabric);
        else if (remoteVersion instanceof QuiltRemoteVersion || remoteVersion instanceof QuiltAPIRemoteVersion)
            return this.context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_quilt);
        else if (remoteVersion instanceof GameRemoteVersion) {
            return switch (remoteVersion.getVersionType()) {
                case RELEASE ->
                        this.context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_mods);
                case SNAPSHOT ->
                        this.context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_packs);
                default ->
                        this.context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_boat);
            };
        } else {
            return this.context.getDrawable(org.koishi.launcher.h2co3.resources.R.drawable.ic_mc_mods);
        }
    }

    private String getTag(RemoteVersion remoteVersion) {
        if (remoteVersion instanceof GameRemoteVersion) {
            return switch (remoteVersion.getVersionType()) {
                case RELEASE ->
                        this.context.getString(org.koishi.launcher.h2co3.resources.R.string.download_release);
                case SNAPSHOT ->
                        this.context.getString(org.koishi.launcher.h2co3.resources.R.string.download_snapshot);
                default ->
                        this.context.getString(org.koishi.launcher.h2co3.resources.R.string.download_old_beta);
            };
        } else {
            return remoteVersion.getGameVersion();
        }
    }

    public interface OnRemoteVersionSelectListener {
        void onSelect(RemoteVersion remoteVersion);
    }

    private static class ViewHolder {
        H2CO3CardView parent;
        AppCompatImageView icon;
        H2CO3TextView version;
        H2CO3TextView tag;
    }
}
