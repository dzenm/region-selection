package com.dzenm.regionselection;

import android.os.Bundle;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.multi.RegionData;
import com.dzenm.multi.RegionSelectionDialog;
import com.dzenm.multi.RegionSelectionView;

public class MainActivity extends AppCompatActivity {

    private TextView address, reselected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address = findViewById(R.id.tv_address);
        reselected = findViewById(R.id.tv_already);

        // 使用View实现
        final RegionSelectionView regionView = findViewById(R.id.region_view);
        regionView.setOnSelectedListener(new RegionSelectionView.OnSelectedListener() {
            public void onCompleted(RegionData[] regionData) {
                address.setText(getText(regionData));
            }
        });

        // 使用PopupWindow实现
        reselected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RegionSelectionDialog.Builder(MainActivity.this)
                        .setOnSelectedListener(new RegionSelectionDialog.OnSelectedListener() {
                            @Override
                            public void onCompleted(PopupWindow popupWindow, RegionData[] regionData) {
                                popupWindow.dismiss();
                                reselected.setText(getText(regionData));
                            }
                        })
                        .create(getWindow().getDecorView());
            }
        });
    }

    private String getText(RegionData[] regionData) {
        StringBuilder sb = new StringBuilder();
        for (RegionData data : regionData) {
            sb.append(data.getName()).append("-");
        }
        return sb.toString();
    }
}
