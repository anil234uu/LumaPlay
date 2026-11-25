package com.targetmedia.lumaplay;

import android.app.Activity;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class MainActivity extends Activity {
    private LinearLayout root, body, nav; private String tab="Home", theme="Dark"; private int fg, sub, bg, glass, border, accent;
    private android.content.SharedPreferences prefs;
    private final ArrayList<String> recent = new ArrayList<>();
    private int dp(float v){return Math.round(v*getResources().getDisplayMetrics().density);}
    @Override public void onCreate(Bundle state){super.onCreate(state);prefs=getSharedPreferences("preferences",MODE_PRIVATE);theme=prefs.getString("theme","Dark"); if(state!=null)tab=state.getString("tab","Home"); for(String x:prefs.getString("recent", "").split("\n"))if(!x.isEmpty())recent.add(x); render();}
    @Override protected void onSaveInstanceState(Bundle state){state.putString("tab",tab);super.onSaveInstanceState(state);}
    private void palette(){boolean light=theme.equals("Light"); fg=Color.parseColor(light?"#142035":"#F1F5FF");sub=Color.parseColor(light?"#53647E":"#A8B6D0");bg=Color.parseColor(light?"#E9EFFA":theme.equals("AMOLED")?"#000000":"#101521");glass=Color.parseColor(light?"#DFFFFFFF":theme.equals("AMOLED")?"#151515":"#DF263247");border=Color.parseColor(light?"#A3B9D7":"#465570");accent=Color.parseColor(light?"#264BA2":"#9BBEFF");}
    private GradientDrawable shape(int color,int r){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(r));d.setStroke(dp(1),border);return d;}
    private LinearLayout col(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}
    private LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    private TextView text(String s,int sp,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(sp);t.setTextColor(color);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    private void add(LinearLayout p,View v,int top){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(top);p.addView(v,lp);}
    private TextView button(String label,Runnable action){TextView b=text(label,15,fg,true);b.setGravity(Gravity.CENTER);b.setMinHeight(dp(48));b.setPadding(dp(12),dp(10),dp(12),dp(10));b.setBackground(shape(glass,22));b.setOnClickListener(v->action.run());b.setFocusable(true);return b;}
    private void render(){palette(); root=col(); root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{bg,theme.equals("AMOLED")?bg:Color.parseColor(theme.equals("Light")?"#D9E7FA":"#1C2941"),bg}));root.setPadding(dp(18),0,dp(18),0);setContentView(root);
      root.setOnApplyWindowInsetsListener((v,i)->{if(android.os.Build.VERSION.SDK_INT>=30){android.graphics.Insets in=i.getInsets(android.view.WindowInsets.Type.systemBars()|android.view.WindowInsets.Type.displayCutout());v.setPadding(dp(18)+in.left,in.top+dp(8),dp(18)+in.right,in.bottom+dp(8));}return i;});root.requestApplyInsets();
      LinearLayout header=row();TextView logo=text("◈  LumaPlay",26,fg,true);header.addView(logo,new LinearLayout.LayoutParams(0,-2,1));header.addView(button("Settings",()->switchTab("Settings")));add(root,header,4);
      ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setClipToPadding(false);body=col();body.setPadding(0,dp(6),0,dp(20));scroll.addView(body);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
      if(tab.equals("Home"))home();else if(tab.equals("Shorts"))shorts();else if(tab.equals("Library"))library();else settings();
      nav=row();nav.setBackground(shape(glass,28));nav.setPadding(dp(3),dp(5),dp(3),dp(5));for(String s:new String[]{"Home","Shorts","Library","Settings"}){TextView t=text(s,12,s.equals(tab)?accent:sub,true);t.setGravity(Gravity.CENTER);t.setMinHeight(dp(48));t.setFocusable(true);t.setOnClickListener(v->switchTab(s));nav.addView(t,new LinearLayout.LayoutParams(0,-2,1));}add(root,nav,0);
    }
    private void switchTab(String s){tab=s;render();}
    private void title(String title,String desc){add(body,text(title,30,fg,true),14);add(body,text(desc,15,sub,false),5);}
    private void card(String tag,String title,String desc,String label,Runnable action){LinearLayout panel=col();panel.setBackground(shape(glass,24));panel.setPadding(dp(18),dp(15),dp(18),dp(15));add(panel,text(tag,12,accent,true),0);add(panel,text(title,23,fg,true),10);add(panel,text(desc,15,sub,false),6);add(panel,button(label,action),16);add(body,panel,14);}
    private void home(){title("Discover","Your videos, in one clean space.");EditText query=new EditText(this);query.setSingleLine();query.setTextColor(fg);query.setHintTextColor(sub);query.setTextSize(16);query.setHint("Search YouTube");query.setPadding(dp(16),dp(12),dp(16),dp(12));query.setBackground(shape(glass,22));query.setImeOptions(EditorInfo.IME_ACTION_SEARCH);query.setOnEditorActionListener((v,a,e)->{if(a==EditorInfo.IME_ACTION_SEARCH){search(query.getText().toString());return true;}return false;});add(body,query,20);add(body,button("Search on YouTube",()->search(query.getText().toString())),8);
      card("WATCH  /  YOUTUBE","Explore videos","Continue in the official YouTube app or website.","Open YouTube",()->openWeb("https://www.youtube.com/"));
      card("LOCAL PLAYER","Your own media","Open a video from your device with playback controls and picture-in-picture.","Choose video",this::pickVideo);
      card("DIRECT LINK","Open a video URL","Open an official YouTube link in its player.","Paste YouTube link",this::linkDialog);
      add(body,text("Explore by topic",20,fg,true),22);for(String s:new String[]{"Music","Technology","Gaming","Travel"})add(body,button(s,()->search(s)),8);
    }
    private void shorts(){title("Shorts","Discover short-form video on YouTube.");card("VERTICAL VIDEO","Shorts feed","Watch the official Shorts feed without changing its playback controls.","Open Shorts",()->openWeb("https://www.youtube.com/shorts/"));card("YOUR CLIPS","Watch a local clip","Choose a vertical or horizontal video from your device.","Choose clip",this::pickVideo);}
    private void library(){title("Library","Your recent YouTube searches are saved only on this device.");add(body,button("Open local video",this::pickVideo),18);if(recent.isEmpty())add(body,text("No recent searches yet.",15,sub,false),22);for(String s:recent)add(body,button(s,()->search(s)),8);if(!recent.isEmpty())add(body,button("Clear search history",()->{recent.clear();prefs.edit().remove("recent").apply();render();}),20);}
    private void settings(){title("Settings","Make LumaPlay your own.");add(body,text("Appearance",20,fg,true),18);for(String s:new String[]{"Light","Dark","AMOLED"})add(body,button((s.equals(theme)?"✓  ":"")+s,()->{theme=s;prefs.edit().putString("theme",s).apply();render();}),8);card("PRIVACY","On-device by design","No analytics, sign-in, broad storage permissions or background network access. YouTube links open externally and follow its privacy settings.","Google privacy policy",()->openWeb("https://policies.google.com/privacy"));add(body,text("LumaPlay 2.0  •  Independent video companion",13,sub,false),20);}
    private void search(String input){String q=input.trim().replace('\n',' ').replace('\r',' ');if(q.isEmpty()){Toast.makeText(this,"Enter a search term",Toast.LENGTH_SHORT).show();return;}recent.remove(q);recent.add(0,q);while(recent.size()>20)recent.remove(recent.size()-1);prefs.edit().putString("recent",String.join("\n",recent)).apply();openWeb("https://www.youtube.com/results?search_query="+Uri.encode(q));}
    private void linkDialog(){EditText e=new EditText(this);e.setSingleLine();e.setHint("https://www.youtube.com/watch?v=...");new android.app.AlertDialog.Builder(this).setTitle("Open YouTube link").setView(e).setNegativeButton("Cancel",null).setPositiveButton("Open",(d,w)->{Uri u=Uri.parse(e.getText().toString().trim());String h=u.getHost();if(!"https".equalsIgnoreCase(u.getScheme())||h==null||!(h.equalsIgnoreCase("youtube.com")||h.equalsIgnoreCase("www.youtube.com")||h.equalsIgnoreCase("m.youtube.com")||h.equalsIgnoreCase("youtu.be"))||u.getUserInfo()!=null||(u.getPort()!=-1&&u.getPort()!=443)){Toast.makeText(this,"Enter a valid HTTPS YouTube link",Toast.LENGTH_LONG).show();return;}openWeb(u.toString());}).show();}
    private void openWeb(String url){try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}catch(ActivityNotFoundException e){Toast.makeText(this,"No compatible app or browser found",Toast.LENGTH_LONG).show();}}
    private void pickVideo(){try{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("video/*");i.addCategory(Intent.CATEGORY_OPENABLE);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivityForResult(i,101);}catch(ActivityNotFoundException e){Toast.makeText(this,"No file picker found",Toast.LENGTH_SHORT).show();}}
    @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==101&&result==RESULT_OK&&data!=null&&data.getData()!=null){Intent i=new Intent(this,PlayerActivity.class);i.setData(data.getData());i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(i);}}
}
