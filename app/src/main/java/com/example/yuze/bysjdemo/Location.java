package com.example.yuze.bysjdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

public class Location extends AppCompatActivity {

    private static final String LTAG = Location.class.getSimpleName();
    private MapView mapView;
    FrameLayout layout;
    private boolean mEnableCustomStyle = true;
//    private static final int OPEN_ID = 0;
//    private static final int CLOSE_ID = 1;
//    private static String PATH = "custom_config_dark.json";
    //custome_config_dark.json  用于设置个性化地图的样式文件，精简为1套样式模板

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapStatus.Builder builder = new MapStatus.Builder();
        LatLng center = new LatLng(39.915071, 116.403907);//默认天安门
        float zoom = 11.0f; //默认11级
        Intent intent = getIntent();
        if (null != intent) {
            mEnableCustomStyle = intent.getBooleanExtra("customStyle", true);
            center = new LatLng(intent.getDoubleExtra("y", 39.915071), intent.getDoubleExtra("x", 116.403907));
            zoom = intent.getFloatExtra("level", 11.0f);
        }
        builder.target(center).zoom(zoom);


//        setMapCustomFile(this,PATH);

        mapView = new MapView(this, new BaiduMapOptions());
        initView(this);
        setContentView(layout);

//        MapView.setMapCustomEnable(true);
    }

//    private void setMapCustomFile (Context context,String PATH){
//        FileOutputStream out = null;
//        InputStream inputStream = null;
//        String moduleName = null;
//        try {
//            inputStream = context.getAssets()
//                    .open("customConfigdir/" + PATH);
//            byte[] b = new byte[inputStream.available()];
//            inputStream.read(b);
//
//            moduleName = context.getFilesDir().getAbsolutePath();
//            File f = new File(moduleName + "/" + PATH);
//            if (f.exists()) {
//                f.delete();
//            }
//            f.createNewFile();
//            out = new FileOutputStream(f);
//            out.write(b);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        MapView.setCustomMapStylePath(moduleName + "/" + PATH);
//    }

    private void initView(Context context) {
        layout = new FrameLayout(this);
        layout.addView(mapView);
        RadioGroup group = new RadioGroup(context);
        group.setBackgroundColor(Color.BLACK);
//        final RadioButton openBtn = new RadioButton(context);
//        openBtn.setText("开启个性化地图");
//        openBtn.setId(OPEN_ID);
//        openBtn.setTextColor(Color.WHITE);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT);

//        group.addView(openBtn, params);
//        final RadioButton closeBtn = new RadioButton(context);
//        closeBtn.setText("关闭个性化地图");
//        closeBtn.setTextColor(Color.WHITE);
//        closeBtn.setId(CLOSE_ID);
//        group.addView(closeBtn, params);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        if (mEnableCustomStyle) {
//            openBtn.setChecked(true);
//        } else {
//            closeBtn.setChecked(true);
//        }

//        layout.addView(group, layoutParams);

//        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == OPEN_ID) {
//                    MapView.setMapCustomEnable(true);
//                } else if (checkedId == CLOSE_ID) {
//                    MapView.setMapCustomEnable(false);
//                }
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        MapView.setMapCustomEnable(false);
        mapView.onDestroy();
    }
}
