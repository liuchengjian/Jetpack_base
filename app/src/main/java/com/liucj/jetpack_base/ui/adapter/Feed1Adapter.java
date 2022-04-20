package com.liucj.jetpack_base.ui.adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.button.MaterialButton;
import com.liucj.jetpack_base.R;
import com.liucj.jetpack_base.databinding.LayoutFeedTypeImageBinding;
import com.liucj.jetpack_base.databinding.LayoutFeedTypeVideoBinding;
import com.liucj.jetpack_base.model.ppjor.Feed;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Feed1Adapter extends BaseMultiItemQuickAdapter<Feed, BaseViewHolder> {
    private String mCategory;
    public Feed1Adapter(List<Feed> data, String category) {
        super(data);
        this.mCategory = category;
        // 绑定 layout 对应的 type
        addItemType(Feed.TYPE_IMAGE_TEXT, R.layout.layout_feed_type_image);
        addItemType(Feed.TYPE_VIDEO, R.layout.layout_feed_type_video);
    }

    @Override
    public int getItemViewType(int position) {
        Feed feed = getItem(position);
        return feed.itemType;
    }

    @Override
    protected void onItemViewHolderCreated(@NotNull BaseViewHolder viewHolder, int viewType) {
//        super.onItemViewHolderCreated(viewHolder, viewType);
        // 绑定View
        DataBindingUtil.bind(viewHolder.itemView);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, Feed feed) {
        if (feed == null) {
            return;
        }
        ViewDataBinding binding =  helper.getBinding();
        // 获取 Binding
        if (binding instanceof LayoutFeedTypeImageBinding) {
            LayoutFeedTypeImageBinding imageBinding = (LayoutFeedTypeImageBinding) binding;
            imageBinding.setFeed(feed);
            imageBinding.feedImage.bindData(feed.width, feed.height, 16, feed.cover);
        }else {
            LayoutFeedTypeVideoBinding videoBinding = (LayoutFeedTypeVideoBinding) binding;
            videoBinding.setFeed(feed);
            videoBinding.listPlayerView.bindData(mCategory,feed.width,
                    feed.height,feed.cover,feed.url);
        }
    }

}
