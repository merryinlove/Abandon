package com.csu.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.csu.telecom.R;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by ubuntu on 15-5-14.
 */
public class PublicNet extends Fragment {
    private TextView amount, point, last;
    private ProgressBar used;
    private MaterialEditText all, rest, warn;

    private double usedflow, totalflow;
    private String lastupdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public, null);
        savedInstanceState = getArguments();
        usedflow = savedInstanceState.getDouble("usedflow");
        totalflow = savedInstanceState.getDouble("totalflow");
        lastupdate = savedInstanceState.getString("lastupdate");
        init(view);
        return view;
    }

    private void init(View view) {
        amount = (TextView) view.findViewById(R.id.amount);
        point = (TextView) view.findViewById(R.id.point);
        last = (TextView) view.findViewById(R.id.last);

        used = (ProgressBar) view.findViewById(R.id.used);

        all = (MaterialEditText) view.findViewById(R.id.all);
        rest = (MaterialEditText) view.findViewById(R.id.rest);
        warn = (MaterialEditText) view.findViewById(R.id.warn);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "helveticaneue-light.otf");
        amount.setTypeface(typeface);

        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");

        amount.setText("" + (int) (usedflow / 1024));

        double use = usedflow / 1024 - (int) usedflow / 1024;
        point.setText(df.format(use) + "GB  已用");
        //2015-05-13
        last.setText("最后更新于:" + lastupdate);

        used.setProgress((int) ((usedflow / totalflow) * 100));

        all.setText((int) totalflow + "MB");
        rest.setText((int) (totalflow - usedflow + 1) + "MB");
        warn.setText("流量还行,注意");


    }
}
