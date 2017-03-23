package com.upc.javabean;

import android.view.View;
import android.widget.TextView;

import com.upc.software.upcmem.R;

/**
 * Created by 稻dao草re人n on 2017/2/8.
 */

public class PocketHolder {
    public TextView pocketName;
    public TextView pocketNum;
    public TextView pocketCoin;

    public PocketHolder(View view) {
        pocketCoin = (TextView) view.findViewById(R.id.pocketcoin);
        pocketName = (TextView) view.findViewById(R.id.pocketname);
        pocketNum = (TextView) view.findViewById(R.id.pocketnumber);
        view.setTag(this);
    }
}
