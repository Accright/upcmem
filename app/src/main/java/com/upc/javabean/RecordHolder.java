package com.upc.javabean;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.upc.software.upcmem.R;

import org.w3c.dom.Text;

/**
 * Created by 稻dao草re人n on 2017/1/17.
 */

public class RecordHolder {
    public ImageView methodPic;
    public TextView number;
    public TextView kind;
    public TextView location;
    public TextView timeText;
    public TextView coin;
    public ImageView dayornight;
    public TextView methodText;
    public RecordHolder(View view) {
        methodPic = (ImageView) view.findViewById(R.id.methodimg);
        number = (TextView) view.findViewById(R.id.listdetailnum);
        kind = (TextView) view.findViewById(R.id.listdetailkind);
        location = (TextView) view.findViewById(R.id.listdetailloc);
        timeText = (TextView)view.findViewById(R.id.timetext);
        coin = (TextView)view.findViewById(R.id.coin);
        dayornight = (ImageView)view.findViewById(R.id.dayornight);
        methodText = (TextView)view.findViewById(R.id.methodtext);
        view.setTag(this);
    }
}
