package com.dzenm.multi;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dzenm
 * @date 2020/06/15 15:17
 * <pre>
 * <com.dzenm.lib.RegionSelectionView
 *      android:id="@+id/region_view"
 *      android:layout_width="match_parent"
 *      android:layout_height="500dp"
 *      app:layout_constraintBottom_toBottomOf="parent" />
 *
 * final RegionSelectionView regionView = findViewById(R.id.region_view);
 * regionView.setOnSelectedCompletedListener(new RegionSelectionView.OnSelectedCompletedListener() {
 *     public void onCompleted(RegionBean[] regionBeans) {
 *         address.setText(getText(regionBeans));
 *     }
 * });
 * </pre>
 */
public class RegionSelectionView extends FrameLayout {

    private final static String TAG = RegionSelectionView.class.getSimpleName();
    /**
     * Tab切换时, 数据更新的延时的时间
     */
    private final static long DEFAULT_DELAY_MILLIS = 150;
    /**
     * 每个区域选择Tab中选中列表的默认位置
     */
    private final static int DEFAULT_INDEX = -1;
    /**
     * 每个Tab更新数据对应的字段
     */
    private final static int HANDLER_DATA = 0x01;

    private final Context mContext;

    /**
     * 选中的颜色, 每个正在选择的区域Tab的颜色，列表选中的位置的颜色, setup by {@link #setSelectedTextColor(int)}
     */
    private int mSelectedTextColor;
    /**
     * 未选中的颜色, 未被选择的区域Tab的颜色，列表未选中的位置的颜色, setup by {@link #setUnselectedTextColor(int)}
     */
    private int mUnselectedTextColor;
    /**
     * 最上方区域选择的标题, setup by {@link #setTitle(CharSequence)} or {@link #setTitle(int)}
     */
    private CharSequence mTitle;
    /**
     * 每个区域选择Tab中提示的标题
     */
    private CharSequence mTabTitle;
    /**
     * 根布局
     */
    private LinearLayout mDecorView;
    /**
     * Tab显示的数量, 能够选择的级别的数量, setup by {@link #setTabCount(int)}
     */
    private int mTabCount = 3;
    /**
     * 当前选中的Tab所在的位置
     */
    private int mSelectedTabPosition = 0;
    /**
     * Tab的父布局, 管理Tab的动态显示
     */
    private LinearLayout mTabLayout;
    /**
     * 显示数据的RecyclerView
     */
    private RecyclerView mRecyclerView;
    /**
     * RecyclerView展示数据使用的Adapter
     */
    private ListAdapter mAdapter;
    /**
     * 当前选中的Tab所在的位置的指示器
     */
    private View mSelectedTabIndicator;
    /**
     * 展示在recyclerview的数据
     */
    private List<RegionData> mData = new ArrayList<>();
    /**
     * 选中的Tab中对应的数据
     */
    private List<SelectedTabData> mSelectedTabData = new ArrayList<>();
    /**
     * 是否设置选中的指示器, setup by {@link #setEnabledIndicator(boolean)}
     */
    private boolean isEnabledIndicator = true;
    /**
     * 在最后一个Tab列表未选中点击Tab时, 会另外增加一个新的Tab, 此时并不知道这个新增加的父Tab选中的是哪一个,
     * 因此增加这个判断来保证在每一个Tab都有一个父Tab列表存在选中的选项
     */
    private boolean isTabClick = false;

    private OnSelectedListener mOnSelectedListener;
    private OnClosedListener mOnClosedListener;
    private OnTabSelectedListener mOnTabSelectedListener;

    public RegionSelectionView(Context context) {
        this(context, null);
    }

    public RegionSelectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RegionSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.RegionSelectionView);
        mSelectedTextColor = t.getColor(R.styleable.RegionSelectionView_selectedTextColor,
                getResources().getColor(android.R.color.holo_red_light));
        mUnselectedTextColor = t.getColor(R.styleable.RegionSelectionView_selectedTextColor,
                Color.parseColor("#757575"));
        mTitle = t.getString(R.styleable.RegionSelectionView_title);
        isEnabledIndicator = t.getBoolean(R.styleable.RegionSelectionView_isEnabledIndicator, isEnabledIndicator);
        t.recycle();

        if (TextUtils.isEmpty(mTitle)) {
            mTitle = mContext.getText(R.string.region_title);
        }
        mTabTitle = mContext.getText(R.string.region_default_text);

        initData();

        // 选择区域的根布局
        mDecorView = ViewHelper.createRegionLayout(mContext);
        addView(mDecorView);
        invalidateChileView();
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
        invalidateChileView();
    }

    public void setTitle(int resId) {
        this.mTitle = mContext.getText(resId);
        invalidateChileView();
    }

    public void setSelectedTextColor(@ColorInt int selectedTextColor) {
        this.mSelectedTextColor = selectedTextColor;
        invalidateChileView();
    }

    public void setUnselectedTextColor(@ColorInt int unselectedTextColor) {
        this.mUnselectedTextColor = unselectedTextColor;
        invalidateChileView();
    }

    public void setTabCount(int tabCount) {
        this.mTabCount = tabCount;
    }

    public void setEnabledIndicator(boolean enabledIndicator) {
        isEnabledIndicator = enabledIndicator;
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        mOnSelectedListener = listener;
    }

    public void setOnClosedListener(OnClosedListener listener) {
        this.mOnClosedListener = listener;
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.mOnTabSelectedListener = listener;
    }

    public RegionSelectionView getRegionSelectionView() {
        return this;
    }

    public void updateData(final List<RegionData> regionData) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = HANDLER_DATA;
                message.obj = regionData;
                mWeakHandler.sendMessage(message);
            }
        }, DEFAULT_DELAY_MILLIS);
    }

    private void initData() {
        // 初始化默认的本地数据  也提供了方法接收外面数据
        post(new Runnable() {
            @Override
            public void run() {
                if (mOnTabSelectedListener == null) {
                    mOnTabSelectedListener = new CityData(mContext);
                }
                if (mTabCount == 0) {
                    return;
                }
                // 获取本地提供的预置数据
                updateListData();
            }
        });
    }

    private void invalidateChileView() {
        // 关闭按钮
        FrameLayout titleLayout = (FrameLayout) mDecorView.getChildAt(0);
        TextView title = (TextView) titleLayout.getChildAt(0);
        title.setText(mTitle);
        title.setTextColor(Color.parseColor("#212121"));

        ImageView close = (ImageView) titleLayout.getChildAt(1);
        close.setImageTintList(ColorStateList.valueOf(mSelectedTextColor));
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClosedListener != null) {
                    mOnClosedListener.onClosed();
                }
            }
        });

        // 指示器
        mSelectedTabIndicator = mDecorView.getChildAt(2);
        mSelectedTabIndicator.setVisibility(isEnabledIndicator ? View.VISIBLE : View.GONE);
        mSelectedTabIndicator.setBackgroundColor(mSelectedTextColor);

        // Tab初始化
        mTabLayout = (LinearLayout) mDecorView.getChildAt(1);

        // RecyclerView Adapter的绑定
        mRecyclerView = (RecyclerView) mDecorView.getChildAt(3);
        mAdapter = new ListAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 校验选中的结果并回调
     */
    private void verifySelectedResult() {
        if (mSelectedTabData.size() == mTabCount && mSelectedTabData.get(mTabCount - 1).isSelected) {
            if (mOnSelectedListener != null) {
                RegionData[] regionData = new RegionData[mTabCount];
                for (int i = 0; i < mSelectedTabData.size(); i++) {
                    regionData[i] = mSelectedTabData.get(i).regionData;
                }
                mOnSelectedListener.onCompleted(regionData);
            }
        } else {
            Toast.makeText(mContext, "请选择完整的地址", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取新的数据并更新列表展示的数据
     */
    private void updateListData() {
        if (mOnTabSelectedListener == null) {
            return;
        }

        // 重置RecyclerView显示的数据
        int position = isLastTab() && !isTabClick ? mSelectedTabPosition + 1 : mSelectedTabPosition;
        RegionData superiorRegionData = position == 0
                ? null
                : getSelectedTabData(position - 1).regionData;
        Log.d(TAG, "tab selected position: " + position);
        mOnTabSelectedListener.onTabSelected(this, position, superiorRegionData);
    }

    /**
     * 接收获取到的显示在页面的数据, 并进行更新页面
     */
    @SuppressLint("HandlerLeak")
    private final Handler mWeakHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == HANDLER_DATA) {
                Log.d(TAG, "refresh data child count: " + mTabLayout.getChildCount());
                Log.d(TAG, "refresh data selected tab position: " + mSelectedTabPosition);

                if ((mTabLayout.getChildCount() == 0 || isLastTab()) && !isTabClick) {
                    // 如果是最后一个添加一个新的Tab
                    addNewTab();
                }

                // 更新新Tab对应的数据
                List<RegionData> regionData = (List<RegionData>) msg.obj;
                mData.clear();
                mData.addAll(regionData);
                mAdapter.notifyDataSetChanged();

                // 如果是向前选择则滚动到这个位置
                SelectedTabData selectedData = getSelectedTabData(mSelectedTabPosition);
                if (selectedData.isSelected) {
                    mRecyclerView.smoothScrollToPosition(selectedData.position);
                }
                isTabClick = false;
            }
        }
    };

    /**
     * 添加一个新的Tab
     */
    private void addNewTab() {
        if (isLastTab()) {
            mSelectedTabPosition++;
        }
        Log.d(TAG, "add new tab position: " + mSelectedTabPosition);
        mSelectedTabData.add(new SelectedTabData());

        final TextView itemView = ViewHelper.createItemView(mContext);
        itemView.setTextColor(mUnselectedTextColor);
        itemView.setText(mTabTitle);
        itemView.setTag(mSelectedTabPosition);
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isTabClick = true;
                mSelectedTabPosition = (int) view.getTag();
                updateSelectedIndicatorAnimation();
                updateSelectedTextColor(mSelectedTabPosition);
                updateListData();
            }
        });
        mTabLayout.addView(itemView);
        updateSelectedIndicatorAnimation();
        updateSelectedTextColor(mSelectedTabPosition);
    }

    /**
     * 下面显示数据的adapter
     */
    private class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ViewHelper.createItemLayout(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.title.setText(mData.get(position).getName());
            holder.title.setTextColor(mUnselectedTextColor);
            holder.selected.setVisibility(View.GONE);

            Log.d(TAG, "onBindViewHolder SelectedTabPosition: " + mSelectedTabPosition);
            // 设置选中效果的颜色
            SelectedTabData selectedData = getSelectedTabData(mSelectedTabPosition);
            RegionData selectedRegionData = selectedData.regionData;
            if (selectedData.isSelected
                    && selectedRegionData != null
                    && mData.get(position).getId().equals(selectedRegionData.getId())) {
                holder.title.setTextColor(mSelectedTextColor);
                holder.selected.setVisibility(View.VISIBLE);
            }
            // 设置点击之后的事件
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetData(holder, mSelectedTabPosition, position);
                }
            });
        }

        private void resetData(ListAdapter.ViewHolder holder, final int selectedTabPosition, int position) {
            Log.d(TAG, "onBindViewHolder click SelectedTabPosition: " + selectedTabPosition);

            // 获取列表选中的数据并更新显示结果
            RegionData regionData = mData.get(position);
            holder.title.setTextColor(mSelectedTextColor);
            holder.selected.setVisibility(View.VISIBLE);

            // 更新选中的Tab对应保存的列表中的数据
            SelectedTabData selectedData = getSelectedTabData(selectedTabPosition);
            notifyItemRangeChanged(selectedData.position, 1);
            selectedData.regionData = regionData;
            selectedData.position = position;
            selectedData.isSelected = true;
            selectedData.title = regionData.getName();
            mSelectedTabData.set(selectedTabPosition, selectedData);
            notifyItemRangeChanged(position, 1);

            // 更新Tab显示及选中的Tab所在的位置
            TextView itemView = (TextView) mTabLayout.getChildAt(selectedTabPosition);
            itemView.setText(selectedData.title);
            updateSelectedIndicatorAnimation();

            // 重新选择时, 清除后面的数据
            int tabCount = mTabLayout.getChildCount();
            if (tabCount <= mTabCount && selectedTabPosition < tabCount) {
                for (int i = tabCount - 1; i > selectedTabPosition; i--) {
                    mTabLayout.removeViewAt(i);
                    mSelectedTabData.remove(i);
                }
            }

            // 判断当前Tab是最后一个Tab, 如果是最后一个Tab, 选择将结束, 如果不是最后一个Tab, 自动跳到下一个Tab
            if (selectedTabPosition < mTabCount - 1) {
                // 创建下一个Tab和数据
                updateListData();
            } else if (selectedTabPosition == mTabCount - 1) {
                verifySelectedResult();
            } else {
                throw new ArrayIndexOutOfBoundsException("selected Tab's position is over total");
            }
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView selected;

            private ViewHolder(View itemView) {
                super(itemView);
                LinearLayout linearLayout = (LinearLayout) itemView;
                title = (TextView) linearLayout.getChildAt(0);
                title.setTextColor(mUnselectedTextColor);
                selected = (ImageView) linearLayout.getChildAt(1);
                selected.setImageTintList(ColorStateList.valueOf(mSelectedTextColor));
            }
        }
    }

    /**
     * 获取选中的Tab对应的数据
     *
     * @param position 选中的位置
     * @return 选中的数据
     * @see SelectedTabData
     */
    public SelectedTabData getSelectedTabData(int position) {
        if (position < 0) {
            return null;
        } else if (position < mSelectedTabData.size()) {
            return mSelectedTabData.get(position);
        }
        return null;
    }

    /**
     * 更新选中的文本颜色
     *
     * @param position 选中的位置
     */
    private void updateSelectedTextColor(int position) {
        int count = mTabLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            int color = i == position ? mSelectedTextColor : mUnselectedTextColor;
            TextView textView = (TextView) mTabLayout.getChildAt(i);
            textView.setTextColor(color);
        }
    }

    /**
     * 更新选中的指示器的动画
     */
    private void updateSelectedIndicatorAnimation() {
        if (!isEnabledIndicator) {
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
                updateSelectedIndicatorAnimation(mSelectedTabPosition);
            }
        });
    }

    /**
     * 更新选中的指示器的动画
     *
     * @param position 选中的位置
     */
    private void updateSelectedIndicatorAnimation(int position) {
        View tab = mTabLayout.getChildAt(position);
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(mSelectedTabIndicator, "x", mSelectedTabIndicator.getX(), tab.getX());

        final ViewGroup.LayoutParams params = mSelectedTabIndicator.getLayoutParams();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(params.width, tab.getMeasuredWidth());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.width = (int) animation.getAnimatedValue();
                mSelectedTabIndicator.setLayoutParams(params);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new FastOutSlowInInterpolator());
        animatorSet.playTogether(xAnimator, widthAnimator);
        animatorSet.start();
    }

    /**
     * 判断是否是最后一个Tab
     *
     * @return 是否是最后一个Tab
     */
    private boolean isLastTab() {
        int childCount = mTabLayout.getChildCount();
        return mSelectedTabPosition < mTabCount - 1
                && mSelectedTabPosition == childCount - 1
                && childCount < mTabCount;
    }

    /**
     * 选中的数据
     */
    private static class SelectedTabData {
        private CharSequence title = "";
        private RegionData regionData = null;
        private int position = DEFAULT_INDEX;
        private boolean isSelected = false;
    }

    /**
     * 选中完成按钮回调
     */
    public interface OnSelectedListener {
        void onCompleted(RegionData[] regionData);
    }

    /**
     * 点击关闭按钮回调
     */
    public interface OnClosedListener {
        void onClosed();
    }

    /**
     * Tab选中的监听事件, 选中时用于自定义显示的数据
     */
    public interface OnTabSelectedListener {
        /**
         * Tab选中之后, 进行数据的获取, 这个过程可以是网络请求, 在获取成功之后, 通过
         * {@link RegionSelectionView#updateData(List)} 进行数据的更新, 这个过程是异步的
         *
         * @param view               当前View
         * @param position           选中Tab的位置
         * @param superiorRegionData 上一级的数据。
         */
        void onTabSelected(RegionSelectionView view, int position, @Nullable RegionData superiorRegionData);
    }
}