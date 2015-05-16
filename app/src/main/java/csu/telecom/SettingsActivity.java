package csu.telecom;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.alertdialogpro.AlertDialogPro;

import java.util.ArrayList;
import java.util.HashMap;

import csu.utils.ActionBarUtil;

public class SettingsActivity extends ActionBarActivity implements View.OnClickListener {


    int[] draw = {
            R.drawable.color_system,
            R.drawable.color_aqua, R.drawable.color_blueviolet, R.drawable.color_coral,
            R.drawable.color_darkblue, R.drawable.color_darkcyan, R.drawable.color_ghostwhite,
            R.drawable.color_indianred, R.drawable.color_lightcyan, R.drawable.color_lightpink,
            R.drawable.color_orchid, R.drawable.color_slategray,
            R.drawable.color_tan, R.drawable.color_violet, R.drawable.color_yellow
    };
    String[] color = {
            "默认", "浅绿色", "蓝紫罗兰", "珊瑚", "暗蓝色", "暗青色", "幽灵白", "印度红", "淡青色", "浅粉红", "兰花紫", "灰石色", "茶色", "紫罗兰", "纯黄"
    };
    private int[] colors = {
            R.color.system,
            R.color.aqua, R.color.blueviolet, R.color.coral,
            R.color.darkblue, R.color.darkcyan, R.color.ghostwhite,
            R.color.indianred, R.color.lightcyan, R.color.lightpink,
            R.color.orchid, R.color.slategray,
            R.color.tan, R.color.violet, R.color.yellow
    };
    private String[] add = new String[2];
    private ImageView statusBar;
    private Toolbar toolbar;

    private View theme, themeRect, address, opinion, help, about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //   ( (TextView)findViewById(R.id.text)).setText(savedInstanceState.getString("json"));
        init();
    }

    private void init() {
        SharedPreferences preferences = getSharedPreferences("use_address", 0);
        add[0] = "brasAddress: " + preferences.getString("brasAddress", "");
        add[1] = "userIntranetAddress: " + preferences.getString("userIntranetAddress", "");

        statusBar = (ImageView) findViewById(R.id.statusBar);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        ActionBarUtil.initToolBar(this, statusBar, toolbar);
        setTitle(R.string.title_activity_settings);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        theme = findViewById(R.id.theme);
        themeRect = findViewById(R.id.themeRect);
        address = findViewById(R.id.address);
        opinion = findViewById(R.id.opinion);
        help = findViewById(R.id.help);
        about = findViewById(R.id.about);

        theme.setOnClickListener(this);
        address.setOnClickListener(this);
        opinion.setOnClickListener(this);
        help.setOnClickListener(this);
        about.setOnClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.theme:
                //生成动态数组，加入数据
                ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
                for (int i = 0; i < draw.length; i++) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("color", color[i]);
                    map.put("image", draw[i]);
                    arrayList.add(map);
                }

                new AlertDialogPro.Builder(SettingsActivity.this)
                        .setTitle("选择主题")
                        .setAdapter(new SimpleAdapter(
                                SettingsActivity.this,
                                arrayList,
                                R.layout.layout_settings_item,
                                new String[]{"color", "image"},
                                new int[]{R.id.color, R.id.image}
                        ), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                themeRect.setBackgroundColor(getResources().getColor(colors[which]));
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.address:
                new AlertDialogPro.Builder(this).setTitle("地址")
                        .setItems(add, null)
                        .setPositiveButton("确认", null)
                        .show();
                break;
            case R.id.opinion:
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
                //设置文本格式
                emailIntent.setType("text/plain");
                //设置对方邮件地址
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "merryinlove@sina.com");
                //设置标题内容
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "反馈建议");
                //设置邮件文本内容
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "内容....");
                startActivity(Intent.createChooser(emailIntent, "选择Email"));
                break;
            case R.id.help:
                break;
            case R.id.about:
                String etc = "数字中南客户端\n========================\n\n作者\n------" +
                        "-------\n* 子夜鋈歌\n* merryinlove@sina.com\n\n开源地址\n-----------" +
                        "--\n* https://github.com/merryinlove/AbanDon\n\n感谢\n-------------" +
                        "\n* soup-1.7.2.jar \n* floatingactionbutton \n* materialedittext \n* alertdial" +
                        "ogpro \n* snackbar \n* materialloadingprogressbar \n* MaterialTabs \n* circ" +
                        "leimageview \n\n更新内容\n-------------\n* RSA用户密码加密的java实现\n* " +
                        "Material Design\n* 等\n\n-------------\n";
                new AlertDialogPro.Builder(SettingsActivity.this)
                        .setTitle("关于")
                        .setMessage(etc)
                        .setNegativeButton("确定", null)
                        .create().show();
                break;

        }
    }
}
