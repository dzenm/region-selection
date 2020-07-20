package com.dzenm.multi;

import android.os.Parcel;
import android.os.Parcelable;

public class RegionBean implements Parcelable {

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

    public RegionBean() {

    }

    private RegionBean(Parcel in) {
        id = in.readString();
        parentId = in.readString();
        name = in.readString();
        code = in.readString();
    }

    public static final Creator<RegionBean> CREATOR = new Creator<RegionBean>() {
        @Override
        public RegionBean createFromParcel(Parcel source) {
            return new RegionBean(source);
        }

        @Override
        public RegionBean[] newArray(int size) {
            return new RegionBean[size];
        }
    };

    public static RegionBean convert(String id, String parentId, String name, String code) {
        RegionBean regionBean = new RegionBean();
        regionBean.setId(id);
        regionBean.setParentId(parentId);
        regionBean.setName(name);
        regionBean.setCode(code);
        return regionBean;
    }
}
