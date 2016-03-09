package com.botpy.demo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.botpy.demo.R;

import java.util.List;

public class MyAdapter extends ArrayAdapter<String> {

    private static final String TAG = "MyAdapter";

    public interface OnDeleteItemListener{
        void onDeleteItem(String name);
    }

    private OnDeleteItemListener mOnDeleteItemListener;

    public void setOnDeleteItemListener(OnDeleteItemListener onDeleteItemListener){
        this.mOnDeleteItemListener = onDeleteItemListener;
    }

    public MyAdapter(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);  
    }  
  
    @Override  
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;  
        if (convertView == null) {  
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_tlistview, null);
        } else {  
            view = convertView;  
        }  

        final String name = getItem(position);

        TextView textView = (TextView) view.findViewById(R.id.text_view);
        view.findViewById(R.id.clear_data).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d(TAG, "name = " + name);

                if (mOnDeleteItemListener != null) {
                    mOnDeleteItemListener.onDeleteItem(name);
                }
            }
        });

        textView.setText(name);
        return view;  
    }  
  
}  