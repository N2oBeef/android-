package com.example.administrator.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/8.
 */
public class MyAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private ArrayList<HashMap<String,Object>> mList;
    private Context mContext;
    private Bitmap directory,file;
    public MyAdapter(Context context,ArrayList<HashMap<String,Object>> List){
        this.mContext = context;
        this.mList = List;
        inflater = LayoutInflater.from(context);
        directory = BitmapFactory.decodeResource(context.getResources(),R.drawable.dir);
        file = BitmapFactory.decodeResource(context.getResources(),R.drawable.file);

        directory = small(directory,0.16f);
        file = small(file,0.1f);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView){

            convertView = inflater.inflate(R.layout.layout_item, null);
            holder = new ViewHolder();
            holder.text = (TextView)convertView.findViewById(R.id.text);
            holder.image = (ImageView)convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        if (holder != null)
        {
            File f = new File(mList.get(position).get("path").toString());

            Bitmap b;
            if (f.isDirectory())
                b = directory;
            else
                b = file;

            if (mList.get(position).get("name").toString().equals("@1")) {
                holder.text.setText("/");
                //Bitmap b =  drawableToBitmap(mContext.getResources().getDrawable((int)mList.get(position).get("Image"), null));
                holder.image.setImageBitmap(b);
            }
            else if (mList.get(position).get("name").toString().equals("@2")) {
                holder.text.setText("..");
                //Bitmap b =  drawableToBitmap(mContext.getResources().getDrawable((int)mList.get(position).get("Image"), null));
                holder.image.setImageBitmap(b);
            }
            else{
                holder.text.setText(mList.get(position).get("path").toString());
                //Bitmap b =  drawableToBitmap(mContext.getResources().getDrawable((int)mList.get(position).get("Image"), null));
                holder.image.setImageBitmap(b);
            }

        }
        return convertView;
    }
    private class ViewHolder{
        private TextView text;
        private ImageView image;
    }

    private Bitmap small(Bitmap map,float num){
        Matrix matrix = new Matrix();
        matrix.postScale(num, num);
        return Bitmap.createBitmap(map,0,0,map.getWidth(),map.getHeight(),matrix,true);
    }

}
