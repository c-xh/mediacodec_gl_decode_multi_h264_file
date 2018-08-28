package com.example.cxh.mediacodec_gl_decode_multi_h264_file.ChangeView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.cxh.mediacodec_gl_decode_multi_h264_file.R;


import static android.content.Context.MODE_PRIVATE;

/**
 * Created by cxh on 2017/9/8.
 */

public class ChangeViewMode extends RelativeLayout {


    private ImageButton[] switchoverButtons = new ImageButton[9];

    private Integer[] switchoverButtonsId = new Integer[]{
            R.id.change_view_mode_layout_01, R.id.change_view_mode_layout_02, R.id.change_view_mode_layout_03,
            R.id.change_view_mode_layout_04, R.id.change_view_mode_layout_05, R.id.change_view_mode_layout_06,
            R.id.change_view_mode_layout_07, R.id.change_view_mode_layout_08, R.id.change_view_mode_layout_09};

    private Integer[] rewardIconIdUp = new Integer[]{
            R.drawable.img_view_mode_01a, R.drawable.img_view_mode_02a, R.drawable.img_view_mode_03a,
            R.drawable.img_view_mode_04a, R.drawable.img_view_mode_05a, R.drawable.img_view_mode_06a,
            R.drawable.img_view_mode_07a, R.drawable.img_view_mode_08a, R.drawable.img_view_mode_09a};

    private Integer[] rewardIconIdDown = new Integer[]{
            R.drawable.img_view_mode_01b, R.drawable.img_view_mode_02b, R.drawable.img_view_mode_03b,
            R.drawable.img_view_mode_04b, R.drawable.img_view_mode_05b, R.drawable.img_view_mode_06b,
            R.drawable.img_view_mode_07b, R.drawable.img_view_mode_08b, R.drawable.img_view_mode_09b};

    private Drawable[] rewardIconsUp = new Drawable[rewardIconIdUp.length];
    private Drawable[] rewardIconsDown = new Drawable[rewardIconIdDown.length];

    private int lastButton = 8;

    private int[][][] switchMode = new int[][][]{
            {{0, 0, 1920, 1088}},
            {{0, 0, 1920, 1088}, {960, 0, 960, 544}}, //第二个随意
            {{0, 272, 960, 544}, {960, 272, 960, 544}},
            {{960, 272, 960, 544}, {0, 0, 960, 544}, {0, 544, 960, 544}},
            {{0, 272, 960, 544}, {960, 0, 960, 544}, {960, 544, 960, 544}},
            {{0, 184, 1280, 720}, {1280, 0, 640, 360}, {1280, 360, 640, 360}, {1280, 720, 640, 360}},
            {{0, 136, 1440, 816}, {1440, 0, 480, 272}, {1440, 272, 480, 272}, {1440, 544, 480, 272}, {1440, 816, 480, 272}},
            {{0, 363, 1280, 725}, {0, 0, 640, 363}, {640, 0, 640, 363}, {1280, 0, 640, 363}, {1280, 363, 640, 363}, {1280, 726, 640, 362}},
            {{0, 180, 640, 360}, {640, 180, 640, 360},{1280, 180, 640, 360}, {0, 540, 640, 360},{640, 540, 640, 360},{1280, 540, 640, 360}}
    };

    private Context context;

    public ChangeViewMode(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.change_view_mode_layout,this,true);

        for (int x = 0; x < rewardIconsUp.length; x++) {
            rewardIconsUp[x] = getResources().getDrawable(rewardIconIdUp[x]);
            rewardIconsUp[x].setBounds(0, 0, rewardIconsUp[x].getMinimumWidth(), rewardIconsUp[x].getMinimumHeight());
        }
        for (int x = 0; x < rewardIconsDown.length; x++) {
            rewardIconsDown[x] = getResources().getDrawable(rewardIconIdDown[x]);
            rewardIconsDown[x].setBounds(0, 0, rewardIconsDown[x].getMinimumWidth(), rewardIconsDown[x].getMinimumHeight());
        }
        initViewModeButton();
        for (int i = 0; i < switchoverButtons.length; i++) {
            final int finalCurrentButton = i;
            switchoverButtons[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastButton == finalCurrentButton){
                        return;
                    }
                    switchoverButtons[lastButton].setBackground(rewardIconsUp[lastButton]);
                    lastButton = finalCurrentButton;
                    switchoverButtons[finalCurrentButton].setBackground(rewardIconsDown[finalCurrentButton]);
                    recordViewModeButton(finalCurrentButton);
                }
            });
        }
    }


    private void recordViewModeButton(int num) {
        SharedPreferences.Editor savecViewModeEditor = context.getSharedPreferences("SavecViewMode", MODE_PRIVATE).edit();
        savecViewModeEditor.putInt("ViewMode", num + 1);
        savecViewModeEditor.putInt("ViewChanneNum", switchMode[num].length);
        savecViewModeEditor.commit();
        if(listener != null)
            listener.changeViewModeOnClick(num + 1 , switchMode[num].length);
    }

    private void initViewModeButton(){
        for (int i = 0; i < switchoverButtons.length; i++) {
            switchoverButtons[i] = (ImageButton) findViewById(switchoverButtonsId[i]);
        }
        SharedPreferences viewModeEditor = context.getSharedPreferences("SavecViewMode", MODE_PRIVATE);
        if (viewModeEditor.getBoolean("FileIsGreate", false) == false) {
            SharedPreferences.Editor savecViewModeEditor = context.getSharedPreferences("SavecViewMode", MODE_PRIVATE).edit();
            savecViewModeEditor.putInt("ViewMode", 9);
            savecViewModeEditor.putInt("ViewChanneNum", switchMode[9-1].length);
            savecViewModeEditor.putBoolean("FileIsGreate", true);
            savecViewModeEditor.commit();
        }
        lastButton = viewModeEditor.getInt("ViewMode" , 9) - 1;
        Log.d("...", "initViewModeButton: " + lastButton);
        switchoverButtons[lastButton].setBackground(rewardIconsDown[lastButton]);
    }

    public int getViewModes(){
        return lastButton + 1;
    }

    public int getViewChannelNum(){
        return switchMode[lastButton].length;
    }

//    public Videomode.ViewMode.Builder getRbsChangeViewChannels(){
//
//        SharedPreferences readChannelEditor = context.getSharedPreferences("SavecViewMode", MODE_PRIVATE);
//        int viewMode = readChannelEditor.getInt("ViewMode", 0) - 1;
//        int viewChanneMun = readChannelEditor.getInt("ViewChanneNum", 0);
//
//        Videomode.ViewMode.Builder changeViewMode = Videomode.ViewMode.newBuilder();
//        changeViewMode.setCnt(viewChanneMun);
//
//        for (int i = 0; i < viewChanneMun; i++) {
//            changeViewMode.addX(switchMode[viewMode][i][0]);
//            changeViewMode.addY(switchMode[viewMode][i][1]);
//            changeViewMode.addWidth(switchMode[viewMode][i][2]);
//            changeViewMode.addHeight(switchMode[viewMode][i][3]);
//        }
//        return changeViewMode;
//    }


    //声明按钮点击监听
    private changeViewModeListener listener;

    //向外提供一个设置监听的方法
    public void setOnChangeViewModeListener(changeViewModeListener listener){
        this.listener = listener;
    }

    //创建一个监听按钮点击的接口
    public interface changeViewModeListener {
        public void changeViewModeOnClick(int ViewMode, int ViewChannelNum);
    }

}
