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
class CityData implements RegionSelectionView.OnTabSelectedListener {

    /**
     * 预置的数据
     */
    private RegionManagerData mPreData;
    private Context mContext;

    public CityData(Context context) {
        mContext = context;
    }

    /**
     * 获取本地提供的预置数据
     */
    private void provideDataSource() {
        try (InputStream inputStream = mContext.getAssets().open("address.json")) {
            StringBuilder json = new StringBuilder();
            BufferedReader addressJsonStream = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = addressJsonStream.readLine()) != null) {
                json.append(line);
            }
            // 将数据转换为对象
            mPreData = new Gson().fromJson(json.toString(), RegionManagerData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTabSelected(RegionSelectionView view, int position, @Nullable RegionData superiorRegionData) {
        if (mPreData == null) {
            provideDataSource();
        }
        if (position == 0 || superiorRegionData == null) {
            view.updateData(convert(mPreData.getProvince()));
        } else {
            view.updateData(getPreSelectedData(mPreData, position, superiorRegionData.getId()));
        }
    }

    /**
     * 获取选中的Tab对应的预置数据
     *
     * @param position Tab对应的位置
     * @return 对应的预置数据
     */
    private @NonNull
    List<RegionData> getPreSelectedData(RegionManagerData preData, int position, String parentId) {
        List<RegionData> regionData = new ArrayList<>();
        List<RegionData> allRegionData = new ArrayList<>();
        if (position == 0) {
            return convert(preData.getProvince());
        } else if (position == 1) {
            allRegionData = convert(preData.getCity());
        } else if (position == 2) {
            allRegionData = convert(preData.getDistrict());
        }
        for (RegionData data : allRegionData) {
            if (data.getParentId().equals(parentId)) {
                regionData.add(data);
            }
        }
        return regionData;
    }

    private List<RegionData> convert(List<DefaultRegionData> list) {
        List<RegionData> regionData = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            DefaultRegionData bean = list.get(i);
            regionData.add(RegionData.convert(bean.getId(), bean.getParentId(), bean.getName(), "0"));
        }
        return regionData;
    }

    private static class RegionManagerData implements Serializable {

        private List<DefaultRegionData> province;
        private List<DefaultRegionData> city;
        private List<DefaultRegionData> district;

        List<DefaultRegionData> getProvince() {
            return province;
        }

        public void setProvince(List<DefaultRegionData> province) {
            this.province = province;
        }

        List<DefaultRegionData> getCity() {
            return city;
        }

        public void setCity(List<DefaultRegionData> city) {
            this.city = city;
        }

        List<DefaultRegionData> getDistrict() {
            return district;
        }

        public void setDistrict(List<DefaultRegionData> district) {
            this.district = district;
        }
    }

    private static class DefaultRegionData {

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
