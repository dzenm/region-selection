package com.dzenm.multi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class ViewHelper {

    /**
     * 创建列表的布局
     *
     * @param context 上下文
     * @return 列表布局
     */
    static LinearLayout createItemLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);

        // 设置列表显示的文本
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setTextSize(14);
        textView.setLayoutParams(params);
        int v = dp2px(12);
        int h = dp2px(24);
        textView.setPadding(h, v, h, v);

        // 设置选中的状态
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(params);
        imageView.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.ic_region_selected);

        linearLayout.addView(textView);
        linearLayout.addView(imageView);
        return linearLayout;
    }

    /**
     * 创建展示选择区域的布局
     *
     * @param context 上下文
     * @return 选择区域布局
     */
    static LinearLayout createRegionLayout(Context context) {
        // 根布局
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(context.getResources().getColor(android.R.color.white, null));

        // 头部标题
        FrameLayout titleLayout = new FrameLayout(context);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        int padding = dp2px(16);
        titleLayout.setPadding(padding, padding, padding, 0);
        TextView title = new TextView(context);
        FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.gravity = Gravity.CENTER;
        title.setLayoutParams(titleParams);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(16);

        ImageView close = new ImageView(context);
        int imageSize = dp2px(32);
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(imageSize, imageSize);
        closeParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        close.setLayoutParams(closeParams);
        int paddingSmall = dp2px(8);
        close.setPadding(paddingSmall, paddingSmall, paddingSmall, paddingSmall);
        close.setImageResource(R.drawable.ic_region_closed);

        titleLayout.addView(title);
        titleLayout.addView(close);

        // 装载选中的区域的Layout
        LinearLayout regionLayout = new LinearLayout(context);
        regionLayout.setOrientation(LinearLayout.HORIZONTAL);
        regionLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp2px(30)
        ));
        regionLayout.setGravity(Gravity.CENTER_VERTICAL);
        regionLayout.setPadding(padding, 0, padding, 0);

        // 指示器
        View indicator = new View(context);
        LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(0, dp2px(2));
        indicatorParams.bottomMargin = dp2px(4);
        indicator.setLayoutParams(indicatorParams);

        // 显示数据的列表
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
        ));
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));

        linearLayout.addView(titleLayout);
        linearLayout.addView(regionLayout);
        linearLayout.addView(indicator);
        linearLayout.addView(recyclerView);
        return linearLayout;
    }

    /**
     * 创建选中的区域布局
     *
     * @param context 上下文
     * @return 单个选中的区域布局
     */
    static TextView createItemView(Context context) {
        TextView itemView = new TextView(context);
        itemView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));
        int padding = dp2px(8);
        itemView.setPadding(padding, 0, padding, 0);
        itemView.setGravity(Gravity.CENTER);
        itemView.setMaxLines(1);
        itemView.setEllipsize(TextUtils.TruncateAt.END);
        itemView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        itemView.setTextSize(14);
        return itemView;
    }

    private static int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                Resources.getSystem().getDisplayMetrics());
    }
}