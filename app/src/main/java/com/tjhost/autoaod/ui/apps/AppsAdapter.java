package com.tjhost.autoaod.ui.apps;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tjhost.autoaod.R;
import com.tjhost.autoaod.data.model.UserApps;

import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.VH>{
    private List<UserApps> mAppstList;
    private AppsClickCallback appsClickCallback;
    private AppsCheckCallback appsCheckCallback;


    public void setAppsClickCallback(AppsClickCallback callback) {
        this.appsClickCallback = callback;
    }

    public void setAppsCheckCallback(AppsCheckCallback callback) {
        this.appsCheckCallback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.apps_list_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        UserApps apps = mAppstList.get(position);
        holder.name.setText(apps.name);
        holder.switchCompat.setChecked(apps.checked);
        holder.icon.setImageDrawable(apps.icon);
    }

    @Override
    public int getItemCount() {
        return mAppstList == null ? 0 : mAppstList.size();
    }

    public void setAppsList(List<UserApps> apps) {
        if (mAppstList == null) {
            mAppstList = apps;
            notifyItemRangeInserted(0, mAppstList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mAppstList.size();
                }

                @Override
                public int getNewListSize() {
                    return apps.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return (mAppstList.get(oldItemPosition).pkg.equals(apps.get(newItemPosition).pkg))
                            && (mAppstList.get(oldItemPosition).checked == apps.get(newItemPosition).checked);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return false;
                }
            });
            mAppstList = apps;
            result.dispatchUpdatesTo(this);
        }
    }

    class VH extends RecyclerView.ViewHolder{
        AppCompatImageView icon;
        AppCompatTextView name;
        SwitchCompat switchCompat;

        public VH(View v) {
            super(v);
            icon = v.findViewById(R.id.app_icon);
            name = v.findViewById(R.id.app_name);
            switchCompat = v.findViewById(R.id.app_switch);
            if (appsClickCallback != null) {
                v.setOnClickListener(vv -> appsClickCallback.onClick(
                        mAppstList.get(getAdapterPosition())));
            }
            if (appsCheckCallback != null) {
//                switchCompat.setOnCheckedChangeListener((buttonView, isChecked)
//                        -> appsCheckCallback.onCheck(mAppstList.get(
//                                getAdapterPosition()), isChecked));
                switchCompat.setOnClickListener(vv -> {
                    appsCheckCallback.onCheck(mAppstList.get(getAdapterPosition()), switchCompat.isChecked());
                });
            }
        }
    }

    public interface AppsClickCallback {
        void onClick(UserApps apps);
    }

    public interface AppsCheckCallback {
        void onCheck(UserApps apps, boolean checked);
    }
}
