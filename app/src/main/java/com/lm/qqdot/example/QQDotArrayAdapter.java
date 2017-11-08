package com.lm.qqdot.example;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.lm.qqdot.QQDot;
import com.lm.qqdot.QQDotListener;

import java.util.List;

/**
 * Created by lm on 2017/11/5.
 */

public class QQDotArrayAdapter extends ArrayAdapter{
    private static final String TAG="qqdot";

    public QQDotArrayAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    public QQDotArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public QQDotArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull Object[] objects) {
        super(context, resource, objects);
    }

    public QQDotArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public QQDotArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    public QQDotArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view= super.getView(position, convertView, parent);
        QQDot qqDot=(QQDot)view.findViewById(R.id.dot);
        String text=(String)getItem(position);
        qqDot.setDotText(text.substring(4,text.length()));
        qqDot.setQQDotListener(new QQDotListener() {
            @Override
            public void onMove(float x, float y) {
                Log.d(TAG,"move:"+x+","+y);
            }

            @Override
            public void onReset() {
                Log.d(TAG,"reset");
            }

            @Override
            public void onDisappear(float x, float y) {
                Log.d(TAG,"disappear:"+x+","+y);
            }

            @Override
            public void onDown(float x, float y) {
                Log.d(TAG,"down:"+x+","+y);
            }
        });

        return view;
    }
}
