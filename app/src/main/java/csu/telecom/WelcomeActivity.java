package csu.telecom;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.skyfishjy.library.RippleBackground;

import csu.utils.ActionBarUtil;


public class WelcomeActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageView statusBar;
    private View content;
    private TextView head;
    private FloatingActionButton fab;
    private RippleBackground rippleBackground;
//    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //is first run?
//        preferences = getSharedPreferences("run_info", 0);
//        boolean first = preferences.getBoolean("first", true);
//        if (!first) {
//            finish();
//            startActivity(new Intent(this, LoginActivity.class));
//
//        }
//        preferences.edit().putBoolean("first", false).apply();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {

        statusBar = (ImageView) findViewById(R.id.statusBar);
        content = findViewById(R.id.toolBar);
        head = (TextView) findViewById(R.id.head);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        rippleBackground = (RippleBackground) findViewById(R.id.animation);
        rippleBackground.startRippleAnimation();

        ActionBarUtil.initToolBar(this, statusBar, content);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "helveticaneue-light.otf");
        head.setTypeface(typeface);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        rippleBackground.stopRippleAnimation();
        startActivity(intent);

    }

    @Override
    protected void onRestart() {

        rippleBackground.startRippleAnimation();

        super.onRestart();
    }
}
