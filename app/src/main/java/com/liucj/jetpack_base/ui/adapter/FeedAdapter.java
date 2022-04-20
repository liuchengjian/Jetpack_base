package com.liucj.jetpack_base.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.liucj.jetpack_base.R;
import com.liucj.jetpack_base.databinding.LayoutFeedTypeImageBinding;
import com.liucj.jetpack_base.databinding.LayoutFeedTypeVideoBinding;
import com.liucj.jetpack_base.model.ppjor.Feed;

import org.jetbrains.annotations.NotNull;

public class FeedAdapter extends PagedListAdapter<Feed, FeedAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    protected Context mContext;
    protected String mCategory;

    public FeedAdapter(Context context, String category) {
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Feed oldItem, @NonNull @NotNull Feed newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Feed oldItem, @NonNull @NotNull Feed newItem) {
                return oldItem.equals(newItem);
            }
        });
        inflater = LayoutInflater.from(context);
        mContext = context;
        mCategory = category;
    }

    @NonNull
    @NotNull
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        if (viewType == Feed.TYPE_IMAGE_TEXT) {
            binding = LayoutFeedTypeImageBinding.inflate(inflater);
        } else {
            binding = LayoutFeedTypeVideoBinding.inflate(inflater);
        }
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public int getItemViewType(int position) {
        Feed feed = getItem(position);
//        if (feed.itemType == Feed.TYPE_IMAGE_TEXT) {
//            return R.layout.layout_feed_type_image;
//        } else if (feed.itemType == Feed.TYPE_VIDEO) {
//            return R.layout.layout_feed_type_video;
//        }
        return feed.itemType;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FeedAdapter.ViewHolder holder, int position) {
        final Feed feed = getItem(position);
        holder.bindData(feed);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding mBinding;

        public ViewHolder(@NonNull @NotNull View itemView, ViewDataBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public void bindData(Feed item) {
            if (mBinding instanceof LayoutFeedTypeImageBinding) {
                LayoutFeedTypeImageBinding imageBinding = (LayoutFeedTypeImageBinding) mBinding;
                imageBinding.setFeed(item);
                imageBinding.feedImage.bindData(item.width, item.height, 16, item.cover);
            }else {
                LayoutFeedTypeVideoBinding videoBinding = (LayoutFeedTypeVideoBinding) mBinding;
                videoBinding.setFeed(item);
                videoBinding.listPlayerView.bindData(mCategory,item.width,
                        item.height,item.cover,item.url);
            }
        }
    }
}
