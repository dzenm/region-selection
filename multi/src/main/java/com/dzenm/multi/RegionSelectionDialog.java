package com.dzenm.multi;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.ColorInt;

public class RegionSelectionDialog {

    private RegionSelectionDialog() { }

    public static class Builder implements RegionSelectionView.OnSelectedListener, RegionSelectionView.OnClosedListener {

        private Activity mActivity;
        private RegionSelectionView mView;
        private PopupWindow mPopupWindow;
        private OnSelectedListener mOnSelectedListener;

        public Builder(Activity activity) {
            mActivity = activity;
            mView = new RegionSelectionView(activity);
            mPopupWindow = new PopupWindow();
        }

        public Builder setTitle(CharSequence title) {
            mView.setTitle(title);
            return this;
        }

        public Builder setTitle(int resId) {
            mView.setTitle(resId);
            return this;
        }

        public Builder setSelectedTextColor(@ColorInt int selectedTextColor) {
            mView.setSelectedTextColor(selectedTextColor);
            return this;
        }

        public Builder setUnselectedTextColor(@ColorInt int unselectedTextColor) {
            mView.setUnselectedTextColor(unselectedTextColor);
            return this;
        }

        public Builder setTabCount(int tabCount) {
            mView.setTabCount(tabCount);
            return this;
        }

        public Builder setEnabledIndicator(boolean enabledIndicator) {
            mView.setEnabledIndicator(enabledIndicator);
            return this;
        }

        public Builder setOnSelectedListener(OnSelectedListener listener) {
            mOnSelectedListener = listener;
            return this;
        }

        public Builder setOnTabSelectedListener(RegionSelectionView.OnTabSelectedListener listener) {
            mView.setOnTabSelectedListener(listener);
            return this;
        }

        public void create(View parent) {
            mView.setOnSelectedListener(this);
            mView.setOnClosedListener(this);
            mPopupWindow.setContentView(mView);
            mPopupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            int height = Resources.getSystem().getDisplayMetrics().heightPixels;
            mPopupWindow.setHeight((int) (height * 0.7));
            // 设置背景
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
            // 外部点击事件
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            setBackgroundAlpha(0.7f);
            mPopupWindow.setAnimationStyle(R.style.PopupWindowBottomAnimation);
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setBackgroundAlpha(1.0f);
                }
            });
            mPopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        }

        private void setBackgroundAlpha(float alpha) {
            // 弹出popupWindow时父控件显示为灰色
            Window window = mActivity.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.alpha = alpha;
            window.setAttributes(layoutParams);
        }

        public void dismiss() {
            mPopupWindow.dismiss();
        }

        @Override
        public void onCompleted(RegionData[] regionData) {
            mOnSelectedListener.onCompleted(mPopupWindow, regionData);
        }

        @Override
        public void onClosed() {
            mPopupWindow.dismiss();
        }
    }

    public interface OnSelectedListener {
        void onCompleted(PopupWindow popupWindow, RegionData[] regionData);
    }
}
