package com.dzenm.multi;

import android.os.Parcel;
import android.os.Parcelable;

public class RegionData implements Parcelable {

    private String id;          // 当前id
    private String parentId;    // 父id
    private String name;        // 地区名称
    private String code;        // 当前地区对应的编号

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(parentId);
        dest.writeString(name);
        dest.writeString(code);
    }

    public RegionData() {

    }

    private RegionData(Parcel in) {
        id = in.readString();
        parentId = in.readString();
        name = in.readString();
        code = in.readString();
    }

    public static final Creator<RegionData> CREATOR = new Creator<RegionData>() {
        @Override
        public RegionData createFromParcel(Parcel source) {
            return new RegionData(source);
        }

        @Override
        public RegionData[] newArray(int size) {
            return new RegionData[size];
        }
    };

    public static RegionData convert(String id, String parentId, String name, String code) {
        RegionData regionData = new RegionData();
        regionData.setId(id);
        regionData.setParentId(parentId);
        regionData.setName(name);
        regionData.setCode(code);
        return regionData;
    }
}
