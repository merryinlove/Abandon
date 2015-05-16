package csu.telecom;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.github.mrengineer13.snackbar.SnackBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import csu.fragment.CampusNet;
import csu.fragment.PublicNet;
import csu.fragment.RestAmount;
import csu.task.LogoutTask;
import csu.utils.ActionBarUtil;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class HomeActivity extends ActionBarActivity implements MaterialTabListener {

    PublicNet publicNet = new PublicNet();
    CampusNet campusNet = new CampusNet();
    RestAmount restAmount = new RestAmount();
    private ImageView statusBar;
    private Toolbar toolbar;
    private String result;
    private MaterialTabHost tabHost;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private Fragment[] fragments = new Fragment[]{
            publicNet,
            campusNet,
            restAmount
    };

    private double surplusflow;
    private double userSchoolOctets;
    private double usedflow;
    private double totalflow;

    private double surplusmoney;
    private String lastupdate;
    private boolean toHome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //   ( (TextView)findViewById(R.id.text)).setText(savedInstanceState.getString("json"));
        init();


        Bundle bundle = new Bundle();
        bundle.putDouble("userSchoolOctets", userSchoolOctets);
        bundle.putDouble("usedflow", usedflow);
        bundle.putDouble("totalflow", totalflow);
        bundle.putDouble("surplusflow", surplusflow);
        bundle.putDouble("surplusmoney", surplusmoney);
        bundle.putString("lastupdate", lastupdate);
        publicNet.setArguments(bundle);
        campusNet.setArguments(bundle);
        restAmount.setArguments(bundle);


    }

    private void init() {

        Intent intent = getIntent();
        result = intent.getStringExtra("json");
        //just for test
        /*result = "{\n" +
                "\"userSchoolOctets\":\"202.59\",\n" +
                "\"password\":\"34babc7360866a3ee74\",\n" +
                "\"surplusmoney\":\"35.13\",\n" +
                "\"vendor\":\"huawei\",\n" +
                "\"usedflow\":\"20667\",\n" +
                "\"time\":130,\n" +
                "\"secretKey\":\"huawei\",\n" +
                "\"resultCode\":\"0\",\n" +
                "\"account\":\"013901120325\",\n" +
                "\"totalflow\":\"40960\",\n" +
                "\"resultDescribe\":\"\",\n" +
                "\"accountID\":\"013901120325@zndx.inter\",\n" +
                "\"lastupdate\":\"2015-05-13 06:27:03\",\n" +
                "\"surplusflow\":\"20293\",\n" +
                "\"userIntranetAddress\":\"10.96.59.127\",\n" +
                "\"brasAddress\":\"61.187.70.254\",\n" +
                "\"username\":\"013901120325\",\n" +
                "\"userAgentType\":\"Mozilla/4.0\"\n" +
                "}\n";*/

        try {
            JSONObject json = new JSONObject(result);
            userSchoolOctets = json.getDouble("userSchoolOctets");
            usedflow = json.getDouble("usedflow");
            totalflow = json.getDouble("totalflow");
            surplusflow = json.getDouble("surplusflow");
            surplusmoney = json.getDouble("surplusmoney");
            lastupdate = json.getString("lastupdate");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        statusBar = (ImageView) findViewById(R.id.statusBar);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);

        pager = (ViewPager) this.findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });

        //insert all tabs
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(tabHost.newTab().setText(adapter.getPageTitle(i)).setTabListener(this));
        }

        ActionBarUtil.initToolBar(this, statusBar, toolbar);
        setTitle("流量信息");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!toHome) {
                toHome = true;
                new SnackBar.Builder(HomeActivity.this).withMessage("再按一次返回桌面").withBackgroundColorId(R.color.sb__button_text_color_red).show();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        toHome = false;
                    }
                }, 2000);
            } else {
                Intent MyIntent = new Intent(Intent.ACTION_MAIN);
                MyIntent.addCategory(Intent.CATEGORY_HOME);
                startActivity(MyIntent);
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            new LogoutTask(this).execute();
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        pager.setCurrentItem(materialTab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {
    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private String[] title = new String[]{"公网流量", "内网流量", "剩余金额"};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }
     /*  {
                *   "userSchoolOctets":"202.59",
                *   "password":"34babc7360866a3ee74.......",
                *   "surplusmoney":"35.13",
                *   "vendor":"huawei",
                *   "usedflow":"20667",
                *   "time":130,
                *   "secretKey":"huawei",
                *   "resultCode":"0",
                *   "account":"0139011203XX",
                *   "totalflow":"40960",
                *   "resultDescribe":"",
                *   "accountID":"013901120XXX@zndx.inter",
                *   "lastupdate":"2015-05-13 06:27:03",
                *   "surplusflow":"20293",
                *   "userIntranetAddress":"10.96.59.127",
                *   "brasAddress":"61.187.70.254",
                *   "username":"013901120325",
                *   "userAgentType":"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"
                *  }
                * */


}
