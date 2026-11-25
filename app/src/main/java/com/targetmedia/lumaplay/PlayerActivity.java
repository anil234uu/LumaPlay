package com.targetmedia.lumaplay;

import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Rational;
import android.view.*;
import android.widget.*;

public class PlayerActivity extends Activity {
    private VideoView video; private LinearLayout controls; private TextView status; private MediaController controller; private boolean ready=false,failed=false,wasPlaying=false; private int position=0; private int vw=16,vh=9;
    @Override public void onCreate(Bundle saved){super.onCreate(saved);Uri uri=getIntent().getData();if(uri==null||!"content".equals(uri.getScheme())){finish();return;}if(saved!=null){position=saved.getInt("position");wasPlaying=saved.getBoolean("playing");}else wasPlaying=true;
      FrameLayout root=new FrameLayout(this);root.setBackgroundColor(Color.BLACK);video=new VideoView(this);root.addView(video,new FrameLayout.LayoutParams(-1,-1,Gravity.CENTER));controls=new LinearLayout(this);controls.setOrientation(LinearLayout.VERTICAL);controls.setPadding(20,20,20,20);controls.setBackgroundColor(0xB0101521);FrameLayout.LayoutParams cp=new FrameLayout.LayoutParams(-1,-2,Gravity.TOP);root.addView(controls,cp);
      controls.setOnApplyWindowInsetsListener((v,i)->{if(android.os.Build.VERSION.SDK_INT>=30){android.graphics.Insets x=i.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());v.setPadding(20+x.left,20+x.top,20+x.right,20);}return i;});
      LinearLayout buttons=new LinearLayout(this);Button close=new Button(this);close.setText("Back");close.setOnClickListener(v->finish());buttons.addView(close);Button pip=new Button(this);pip.setText("Picture-in-picture");pip.setOnClickListener(v->enterMini());buttons.addView(pip);controls.addView(buttons);status=new TextView(this);status.setTextColor(Color.WHITE);status.setText("Loading video…");controls.addView(status);setContentView(root);controller=new MediaController(this);controller.setAnchorView(video);video.setMediaController(controller);
      video.setOnPreparedListener(mp->{ready=true;vw=mp.getVideoWidth();vh=mp.getVideoHeight();status.setText("Local video • Tap for controls");if(position>0)video.seekTo(position);if(wasPlaying)video.start();});video.setOnErrorListener((mp,what,extra)->{failed=true;status.setText("Unable to play this file. Try a supported MP4 or WebM video.");return true;});video.setOnCompletionListener(mp->{wasPlaying=false;status.setText("Playback complete • Tap to replay");});video.setVideoURI(uri);
    }
    private void enterMini(){if(!ready||failed||!getPackageManager().hasSystemFeature("android.software.picture_in_picture")){Toast.makeText(this,"Picture-in-picture is unavailable",Toast.LENGTH_SHORT).show();return;}controller.hide();float ratio=vh==0?16f/9:vw/(float)vh;Rational r=ratio>2.39f?new Rational(239,100):ratio<0.42f?new Rational(100,239):new Rational(Math.max(1,vw),Math.max(1,vh));try{if(!enterPictureInPictureMode(new PictureInPictureParams.Builder().setAspectRatio(r).build()))Toast.makeText(this,"Picture-in-picture is disabled",Toast.LENGTH_SHORT).show();}catch(RuntimeException e){Toast.makeText(this,"Picture-in-picture unavailable",Toast.LENGTH_SHORT).show();}}
    @Override public void onPictureInPictureModeChanged(boolean mini,Configuration config){super.onPictureInPictureModeChanged(mini,config);controls.setVisibility(mini?View.GONE:View.VISIBLE);if(mini)controller.hide();}
    @Override protected void onPause(){super.onPause();if(!isInPictureInPictureMode()&&video!=null&&ready&&!failed){position=video.getCurrentPosition();wasPlaying=video.isPlaying();video.pause();}}
    @Override protected void onResume(){super.onResume();if(video!=null&&ready&&!failed&&wasPlaying&&!isInPictureInPictureMode()){video.seekTo(position);video.start();}}
    @Override protected void onStop(){super.onStop();if(video!=null&&ready&&!failed&&!isInPictureInPictureMode())video.pause();}
    @Override protected void onSaveInstanceState(Bundle out){super.onSaveInstanceState(out);out.putInt("position",video!=null&&ready&&!failed?video.getCurrentPosition():position);out.putBoolean("playing",video!=null&&ready&&!failed?video.isPlaying():wasPlaying);}
    @Override protected void onDestroy(){if(video!=null)video.stopPlayback();super.onDestroy();}
}
