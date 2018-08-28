package com.example.cxh.mediacodec_gl_decode_multi_h264_file.ChangeView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import static android.content.Context.MODE_PRIVATE;
import com.example.cxh.mediacodec_gl_decode_multi_h264_file.R;

/**
 * Created by cxh on 2017/9/11.
 */

public class ChangeViewChannel {

    private Button[][] channelButtons = new Button[6][6];
    private Integer[][] channelButtonsId = new Integer[][]{
            {R.id.channel1_1, R.id.channel1_2, R.id.channel1_3, R.id.channel1_4, R.id.channel1_5, R.id.channel1_6},
            {R.id.channel2_1, R.id.channel2_2, R.id.channel2_3, R.id.channel2_4, R.id.channel2_5, R.id.channel2_6},
            {R.id.channel3_1, R.id.channel3_2, R.id.channel3_3, R.id.channel3_4, R.id.channel3_5, R.id.channel3_6},
            {R.id.channel4_1, R.id.channel4_2, R.id.channel4_3, R.id.channel4_4, R.id.channel4_5, R.id.channel4_6},
            {R.id.channel5_1, R.id.channel5_2, R.id.channel5_3, R.id.channel5_4, R.id.channel5_5, R.id.channel5_6},
            {R.id.channel6_1, R.id.channel6_2, R.id.channel6_3, R.id.channel6_4, R.id.channel6_5, R.id.channel6_6},
    };


    private Drawable rewardIcon1Up;
    private Drawable rewardIcon1Dowm;
    private Activity mActivity;
    private Context mContext;
    public ChangeViewChannel(Context context , Activity act, int ViewChannelNum){
        mContext = context;
        mActivity = act;
        rewardIcon1Up = context.getResources().getDrawable(R.drawable.img_view_channel_01);
        rewardIcon1Up.setBounds(0, 0, rewardIcon1Up.getMinimumWidth(), rewardIcon1Up.getMinimumHeight());
        rewardIcon1Dowm = context.getResources().getDrawable(R.drawable.img_view_channel_02);
        rewardIcon1Dowm.setBounds(0, 0, rewardIcon1Dowm.getMinimumWidth(), rewardIcon1Dowm.getMinimumHeight());
        setChannelButton(ViewChannelNum);

    }




    private void clsChannelButton(int view , int channel) {
        for (int i = 0; i < 6; i++) {
            if (i != view) {
                channelButtons[i][channel].setBackground(rewardIcon1Up);
            }
        }
    }

    public int[] getChannelBuff() {
        return saveChannelBuff;
    }

    int[] saveChannelBuff = new int[]{0,0,0,0,0,0};

    private void saveChannel(int view, int channel) {
        saveChannelBuff[channel] = view;
        if (listener != null) {
            listener.ChangeViewChannelOnClick(saveChannelBuff);
        }
        SharedPreferences.Editor saveChannelEditor = mContext.getSharedPreferences("saveChannel", MODE_PRIVATE).edit();
        saveChannelEditor.putInt("Channel" + channel, view);
        Log.d("channel", "saveChannel: channel,ciew(" + channel + "," + view);
        saveChannelEditor.commit();
    }

    private void setChannelButton(int ViewChannelNum){

        SharedPreferences getChannels = mContext.getSharedPreferences("saveChannel", MODE_PRIVATE);
        if (getChannels.getBoolean("FileIsGreate", false) == false){
            SharedPreferences.Editor saveChanneleditor = mContext.getSharedPreferences("saveChannel", MODE_PRIVATE).edit();
            for (int i = 0; i < 6; i++) {
                saveChanneleditor.putInt("Channel" + i, i);
                saveChannelBuff[i] = i;
            }
            saveChanneleditor.putBoolean("FileIsGreate",true);
            saveChanneleditor.commit();
        } else {
            for (int i = 0; i < 6; i++) {
                saveChannelBuff[i] = getChannels.getInt("Channel" + i, i);
                Log.d("setChannelButton", "setChannelButton: " + saveChannelBuff[i]);
            }
        }

        for (int view_t = 0; view_t < 6; view_t++) {
            for (int channel_t = 0; channel_t < 6; channel_t++ ) {

                channelButtons[view_t][channel_t] = (Button) mActivity.findViewById(channelButtonsId[view_t][channel_t]);
                final int finalView_t = view_t;
                final int finalChannel_t = channel_t;
                channelButtons[view_t][channel_t].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        channelButtons[finalView_t][finalChannel_t].setBackground(rewardIcon1Dowm);
                        clsChannelButton(finalView_t, finalChannel_t);
                        saveChannel(finalView_t, finalChannel_t);
                    }
                });

                if (channel_t < ViewChannelNum) {
                    channelButtons[view_t][channel_t].setVisibility(View.VISIBLE);
                } else {
                    channelButtons[view_t][channel_t].setVisibility(View.INVISIBLE);
                }

                if(saveChannelBuff[channel_t] == view_t) {
                    channelButtons[view_t][channel_t].setBackground(rewardIcon1Dowm);
                }
            }
        }
    }


    public void setChannelNum(int viewChannelNum){
        for (int view_t = 0; view_t < 6; view_t++) {
            for (int channel_t = 0; channel_t < 6; channel_t++) {
                if (channel_t < viewChannelNum) {
                    channelButtons[view_t][channel_t].setVisibility(View.VISIBLE);
                } else {
                    channelButtons[view_t][channel_t].setVisibility(View.INVISIBLE);
                }
            }
        }
    }

//    public Videomode.ChangeView.Builder getRbsChangeViewChannels(){
//
//        SharedPreferences readChannelEditor = mContext.getSharedPreferences("SavecViewMode", MODE_PRIVATE);
//        SharedPreferences RreadcChannel = mContext.getSharedPreferences("saveChannel", MODE_PRIVATE);
//        int viewChanneMun = readChannelEditor.getInt("ViewChanneNum", 0);
//        Videomode.ChangeView.Builder changeViewChannels = Videomode.ChangeView.newBuilder();
//
//        for (int i = 0; i < viewChanneMun; i++) {
//            int Channel_t = RreadcChannel.getInt("Channel" + i, -1);
//            if (Channel_t != -1)
//                changeViewChannels.addChannels(channel_To_ChannelType(Channel_t));
//        }
//        return changeViewChannels;
//    }
//
//    private Settings.ChannelType channel_To_ChannelType(int i){
//        switch (i){
//            case 0 : return Settings.ChannelType.CAM_TEACHER_OVERALL;
//            case 1 : return Settings.ChannelType.CAM_TEACHER_CLOSEUP;
//            case 2 : return Settings.ChannelType.CAM_STUDENT_OVERALL;
//            case 3 : return Settings.ChannelType.CAM_STUDENT_CLOSEUP;
//            case 4 : return Settings.ChannelType.RBS_VGA;
//            case 5 : return Settings.ChannelType.RBS_WHITEBOARD;
//        }
//        return Settings.ChannelType.CHANNEL_NONE;
//    }

    //声明按钮点击监听
    private changeViewChannelListener listener;

    //向外提供一个设置监听的方法
    public void setOnChangeViewChannelListener(changeViewChannelListener listener){
        this.listener = listener;
    }

    //创建一个监听按钮点击的接口
    public interface changeViewChannelListener {
        public void ChangeViewChannelOnClick(int[] channelBuff);
    }
}
