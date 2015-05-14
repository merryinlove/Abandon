package com.csu.telecom;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro;
import com.csu.task.LoginTask;
import com.csu.utils.ActionBarUtil;
import com.csu.utils.RSAUtil;
import com.github.mrengineer13.snackbar.SnackBar;
import com.material.widget.PaperButton;
import com.rengwuxian.materialedittext.MaterialEditText;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageView statusBar;
    private Toolbar toolbar;
    private PaperButton more, login;
    private boolean[] multChoice;
    private SharedPreferences preferences;
    private SharedPreferences usr;
    private SharedPreferences.Editor editor;
    private MaterialEditText username, password;
    private String storedUsername, storedPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        intitSharePreference();
        init();
    }

    private void intitSharePreference() {
        multChoice = new boolean[2];
        preferences = getSharedPreferences("setting_info", 0);
        multChoice[0] = preferences.getBoolean("remember", true);
        multChoice[1] = preferences.getBoolean("autologin", false);

        usr = getSharedPreferences("usr_info", 0);
        storedUsername = usr.getString("username", "");
        storedPassword = usr.getString("password", "");

    }

    private void init() {

        username = (MaterialEditText) findViewById(R.id.username);
        password = (MaterialEditText) findViewById(R.id.password);

        username.setText(storedUsername);
        password.setText(storedPassword);


        statusBar = (ImageView) findViewById(R.id.statusBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        more = (PaperButton) findViewById(R.id.more);
        login = (PaperButton) findViewById(R.id.login);
        more.setOnClickListener(this);
        login.setOnClickListener(this);

        ActionBarUtil.initToolBar(this, statusBar, toolbar);
        setTitle("登录");
        setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_settings) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more:
                new AlertDialogPro.Builder(LoginActivity.this).
                        setTitle("更多...").
                        setMultiChoiceItems(new String[]{"记住密码", "自动登录"}, multChoice, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                multChoice[which] = isChecked;
                            }
                        }).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor = preferences.edit();
                                editor.putBoolean("remember", multChoice[0]);
                                editor.putBoolean("autologin", multChoice[1]);
                                editor.apply();
                            }
                        }).
                        setNegativeButton("取消", null).
                        show();
                break;
            case R.id.login:
                String name = username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pass))
                    new SnackBar.Builder(this).withMessage("账户或密码不能为空...").withBackgroundColorId(R.color.primary).show();
                else {

                    //保存更多选项到本地sharePreferences
                    editor = preferences.edit();
                    editor.putBoolean("remember", multChoice[0]);
                    editor.putBoolean("autologin", multChoice[1]);
                    editor.apply();

                    LoginTask task = new LoginTask(LoginActivity.this, multChoice[0]); //为毛传multChoice[0]---->记住密码,如果成功登录则记住密码,否则不记住
                    task.execute(name, pass);
                }
                break;

        }
    }

}
