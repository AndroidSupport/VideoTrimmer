package com.uniquext.android.videotrimmer.adapter;

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;


import com.uniquext.android.videotrimmer.adapter.BitmapHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 　 　　   へ　　　 　／|
 * 　　    /＼7　　　 ∠＿/
 * 　     /　│　　 ／　／
 * 　    │　Z ＿,＜　／　　   /`ヽ
 * 　    │　　　 　　ヽ　    /　　〉
 * 　     Y　　　　　   `　  /　　/
 * 　    ｲ●　､　●　　⊂⊃〈　　/
 * 　    ()　 へ　　　　|　＼〈
 * 　　    >ｰ ､_　 ィ　 │ ／／      去吧！
 * 　     / へ　　 /　ﾉ＜| ＼＼        比卡丘~
 * 　     ヽ_ﾉ　　(_／　 │／／           消灭代码BUG
 * 　　    7　　　　　　　|／
 * 　　    ＞―r￣￣`ｰ―＿
 * ━━━━━━━━━━感觉萌萌哒━━━━━━━━━━
 *
 * @author uniquext
 * @description $
 * @date 2021-03-09  15:27
 */
public class VideoTrimmerAdapter extends RecyclerView.Adapter {

    private int itemWidth;
    private List<Bitmap> mBitmaps = new ArrayList<>();

    public VideoTrimmerAdapter(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppCompatImageView imageView = new AppCompatImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BitmapHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((AppCompatImageView) holder.itemView).setImageBitmap(mBitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return mBitmaps.size();
    }

    public void addBitmap(Bitmap bitmap) {
        mBitmaps.add(bitmap);
        notifyItemInserted(getItemCount());
    }
}
