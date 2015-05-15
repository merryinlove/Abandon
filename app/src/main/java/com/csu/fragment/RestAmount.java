package com.csu.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro;
import com.csu.telecom.R;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by ubuntu on 15-5-14.
 */
public class RestAmount extends Fragment {

    private TextView amount, point, last;
    private MaterialEditText all, rest, warn;
    private double surplusmoney;

    private String lastupdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_amount, null);
        savedInstanceState = getArguments();
        surplusmoney = savedInstanceState.getDouble("surplusmoney");
        lastupdate = savedInstanceState.getString("lastupdate");
        init(view);
        return view;
    }

    private void init(View view) {
        amount = (TextView) view.findViewById(R.id.amount);
        point = (TextView) view.findViewById(R.id.point);
        last = (TextView) view.findViewById(R.id.last);


        all = (MaterialEditText) view.findViewById(R.id.all);
        rest = (MaterialEditText) view.findViewById(R.id.rest);
        warn = (MaterialEditText) view.findViewById(R.id.warn);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "helveticaneue-light.otf");
        amount.setTypeface(typeface);

        amount.setText("" + (int) (surplusmoney));

        double use = surplusmoney - (int) surplusmoney;
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        point.setText(df.format(use) + "元  剩余");
        //2015-05-13
        last.setText("最后更新于:" + lastupdate);

        //used.setProgress((int) ((surplusmoney / surplusflow) * 100));
        //all.setText((int) surplusflow + "MB");
        rest.setText(surplusmoney + "元");
        warn.setText("点我跳转到充值页面");

        warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialogPro.Builder(getActivity())
                        .setTitle("来自开发者的提醒")
                        .setMessage("本程序只是提供一个跳转到缴费网站的接口," +
                                "本应用不会有任何损害您利益的行为.\n" +
                                "但是由于本应用开源因此可能导致第三方的恶意修改,编译,发布," +
                                "请确保本应用是来自正规渠道,否则请取消使用本功能.")
                        .setPositiveButton("确定调转跳转", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse("http://hn.189.cn/hnselfservice/topup/topup!topupIndex.action?isCorp=0");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }
}
