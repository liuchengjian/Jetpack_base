package com.liucj.lib_common.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liucj.lib_common.utils.HiDisplayUtil;

public abstract class BaseFragment extends Fragment {
    protected View layoutView;

//    @LayoutRes
//    public abstract int getLayoutId();
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        layoutView = inflater.inflate(getLayoutId(), container, false);
//        return layoutView;
//    }

    //检测 宿主 是否还存活
    public boolean isAlive() {
        if (isRemoving() || isDetached() || getActivity() == null) {
            return false;
        }
        return true;
    }

    public void clipBottomPadding( ViewGroup targetView) {
        if (targetView != null) {
            targetView.setPadding(0, 0, 0, HiDisplayUtil.dp2px(50f));
            targetView.setClipToPadding(false);
        }
    }
}
