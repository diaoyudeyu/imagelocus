package m.z.imagelocus.activity.map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.googlecode.androidannotations.annotations.*;
import m.z.common.CommonView;
import m.z.imagelocus.R;
import m.z.imagelocus.config.SystemAdapter;
import m.z.imagelocus.entity.Lbs;
import m.z.imagelocus.entity.convert.LbsConvert;
import m.z.imagelocus.service.Service;
import m.z.imagelocus.service.http.LbsYunService;
import m.z.imagelocus.view.map.LocationMapView;
import m.z.imagelocus.view.map.LocationOverlay;
import m.z.util.CalendarUtil;
import m.z.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoTitle
@EActivity(R.layout.activity_map_friend)
public class MapFriendActivity extends Activity{

    public static MapFriendActivity instance = null;
    private LayoutInflater inflater;

    @ViewById(R.id.tv_middle)
    TextView tv_middle;
    //右上角第一个按钮
    @ViewById(R.id.btn_right)
    Button btn_right;

    List<Lbs> lbsList = null;    //位置数据
    List<LocationOverlay> locationOverlayList = null;   //位置图层

    //弹出泡泡图层
    private PopupOverlay pop  = null;     //弹出泡泡图层，浏览节点时使用
    private View popView = null;    //泡泡view

    //地图相关，使用继承MapView的LocationMapView目的是重写touch事件实现泡泡处理
    //如果不处理touch事件，则无需继承，直接使用MapView即可
    @ViewById(R.id.bmap_view)
    LocationMapView mMapView;	    // 地图View
    MapController mMapController = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        inflater = LayoutInflater.from(this);
    }

    @AfterViews
    void init() {

        tv_middle.setText("朋友们的位置");

        //地图初始化
        mMapController = mMapView.getController();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);

        //创建弹出泡泡图层
        initPaopao();
        //创建位置数据
        readLbsData();
    }

    /**
     * 创建弹出泡泡图层
     */
    public void initPaopao(){
        popView = getLayoutInflater().inflate(R.layout.impress_paopao_view, null);
        //泡泡点击响应回调
        PopupClickListener popListener = new PopupClickListener(){
            @Override
            public void onClickedPopup(int index) {
                Log.v("click", "clickpaopao");
            }
        };
        pop = new PopupOverlay(mMapView, popListener);
        mMapView.pop = pop;
    }


    /**
     * 创建位置数据
     * */
    private void readLbsData() {

        //lbsDataHistory = Service.lbsService.findByUser_id(user_id);

        //String app_user_id = SystemAdapter.currentUser.getApp_user_id();
        //new LbsYunService(instance, LbsYunService.FunctionName.findLbsByApp_User_id, app_user_id) {
        new LbsYunService(instance, LbsYunService.FunctionName.findAllLbs) {
            @Override
            public void doResult(Map<String, Object> resultMap) {
                CommonView.displayShort(instance, (String) resultMap.get("msg"));
                lbsList = (List<Lbs>) resultMap.get("lbsList");
                setLocationOverlay();
            }
        };
    }

    /**
     * 创建位置图层
     */
    private void setLocationOverlay() {
        if(lbsList != null && lbsList.size() != 0) {
            //清除之前的位置数据
            mMapView.getOverlays().clear();
            locationOverlayList = new ArrayList<LocationOverlay>();

            for(Lbs lbs : lbsList) {
                LocationOverlay locOverlay = new LocationOverlay(mMapView, pop, popView, false);
                //设置为0则不显示精度圈
                lbs.setRadius(0);
                locOverlay.setData(lbs);
                locationOverlayList.add(locOverlay);
            }

            mMapView.getOverlays().addAll(locationOverlayList);
        }
        mMapView.refresh();

    }


    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMapView.onRestoreInstanceState(savedInstanceState);
    }

}




