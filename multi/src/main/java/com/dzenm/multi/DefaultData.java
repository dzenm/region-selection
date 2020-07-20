package com.dzenm.multi;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 预设数据
 */
public class DefaultData implements RegionSelectionView.OnTabSelectedListener {

    /**
     * 预置的数据
     */
    private RegionManagerBean mPreData;
    private Context mContext;
    private int mIndex = 1;

    public DefaultData(Context context) {
        mContext = context;
    }

    /**
     * 获取本地提供的预置数据
     */
    private void provideDataSource() {
        StringBuilder json = new StringBuilder();
        try (InputStream inputStream = mContext.getAssets().open("address.json")) {
            BufferedReader addressJsonStream = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = addressJsonStream.readLine()) != null) {
                json.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将数据转换为对象
        mPreData = new Gson().fromJson(json.toString(), RegionManagerBean.class);
    }

    @Override
    public void onTabSelected(RegionSelectionView view, int position, @Nullable RegionBean superiorRegionBean) {
        if (mPreData == null) {
            provideDataSource();
        }
        if (position == 0 || superiorRegionBean == null) {
            view.updateData(convert(mPreData.getProvince()));
        } else {
            view.updateData(getPreSelectedData(mPreData, position, superiorRegionBean.getId()));
            if (mIndex % 2 == 0) {
            } else {

            }
            mIndex++;
        }
    }

    /**
     * 获取选中的Tab对应的预置数据
     *
     * @param position Tab对应的位置
     * @return 对应的预置数据
     */
    private @NonNull
    List<RegionBean> getPreSelectedData(RegionManagerBean preData, int position, String parentId) {
        List<RegionBean> regionBeans = new ArrayList<>();
        List<RegionBean> allRegionBean = new ArrayList<>();
        if (position == 0) {
            return convert(preData.getProvince());
        } else if (position == 1) {
            allRegionBean = convert(preData.getCity());
        } else if (position == 2) {
            allRegionBean = convert(preData.getDistrict());
        }
        for (RegionBean regionBean : allRegionBean) {
            if (regionBean.getParentId().equals(parentId)) {
                regionBeans.add(regionBean);
            }
        }
        return regionBeans;
    }

    private List<RegionBean> convert(List<DefaultRegionBean> list) {
        List<RegionBean> regionBeans = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            DefaultRegionBean bean = list.get(i);
            regionBeans.add(RegionBean.convert(bean.getId(), bean.getParentId(), bean.getName(),"0"));
        }
        return regionBeans;
    }

    public static class RegionManagerBean implements Serializable {

        private List<DefaultRegionBean> province;
        private List<DefaultRegionBean> city;
        private List<DefaultRegionBean> district;

        List<DefaultRegionBean> getProvince() {
            return province;
        }

        public void setProvince(List<DefaultRegionBean> province) {
            this.province = province;
        }

        List<DefaultRegionBean> getCity() {
            return city;
        }

        public void setCity(List<DefaultRegionBean> city) {
            this.city = city;
        }

        List<DefaultRegionBean> getDistrict() {
            return district;
        }

        public void setDistrict(List<DefaultRegionBean> district) {
            this.district = district;
        }
    }

    static class DefaultRegionBean {

        @SerializedName("i")
        private String id;          // 当前id
        @SerializedName("p")
        private String parentId;    // 父id
        @SerializedName("n")
        private String name;        // 地区名称

        public String getId() {
            return id == null ? "" : id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentId() {
            return parentId == null ? "" : parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getName() {
            return name == null ? "" : name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
