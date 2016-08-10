package com.example.administrator.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.listview)
    ListView mListView;

    private ArrayList<HashMap<String,Object>> list = null;
    private HashMap<String,Object> map = null;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        FilesInPath("/");

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = list.get(position).get("path").toString();

                File file = new File(path);
                //处理目录以及文件
                if (file.canRead() && file.exists()) {
                    if (file.isDirectory()) {
                        FilesInPath(path);
                    } else {
                        fileHandle(file);
                    }
                } else { //没有权限的情况
                    Resources res = getResources();
                    new AlertDialog.Builder(MainActivity.this).setTitle("Message")
                            .setMessage(res.getString(R.string.no_permission))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }

            }
        });
    }

    //遍历目录并显示到listview
    private void FilesInPath(String path) {
        list = new ArrayList<HashMap<String,Object>>();
        File file = new File(path);

        //判断是否为根目录
        if (!path.equals("/")){
            initdata("/", "@1");
            initdata(file.getParent(), "@2");
        }

        File[] files = file.listFiles();

        if (files != null) {
            for (File f : files) {
                Log.e(f.getPath().toString(), f.getPath().toString() + f.getName().toString());

                if (f.isDirectory())
                    initdata(f.getPath().toString(), "");
                else
                    initdata(f.getPath().toString(), "");
            }
        }

        adapter = new MyAdapter(this, list);
        mListView.setAdapter(adapter);
    }

    private void initdata(String path, String pathflag) {
            map = new HashMap<String,Object>();       //为避免产生空指针异常，有几列就创建几个map对象
            map.put("path", path);
            map.put("name", pathflag);
            list.add(map);
    }

    private void fileHandle(final File file){
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    openFile(file);
                }else if (which == 1){
                    LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                    View view = factory.inflate(R.layout.rename_dialog, null);
                    final EditText editText = (EditText)view.findViewById(R.id.editText);
                    editText.setText(file.getName());

                    DialogInterface.OnClickListener listener2 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            String modifyName = editText.getText().toString();
                            final String fpath = file.getParentFile().getPath();
                            final File newFile = new File(fpath + "/" + modifyName);
                            if (newFile.exists()){
                                //排除没有修改情况
                                if (!modifyName.equals(file.getName())){
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("注意!")
                                            .setMessage("文件名已存在，是否覆盖？")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (file.renameTo(newFile)){
                                                        FilesInPath(fpath);
                                                        displayToast("重命名成功！");
                                                    }
                                                    else{
                                                        displayToast("重命名失败！");
                                                    }
                                                }
                                            })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                }
                            }
                            else{
                                if (file.renameTo(newFile)){
                                    FilesInPath(fpath);
                                    displayToast("重命名成功！");
                                }
                                else{
                                    displayToast("重命名失败！");
                                }
                            }
                        }
                    };
                    AlertDialog renameDialog = new AlertDialog.Builder(MainActivity.this).create();
                    renameDialog.setView(view);
                    renameDialog.setButton("确定", listener2);
                    renameDialog.setButton2("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });
                    renameDialog.show();

                }else if (which == 2){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("注意!")
                            .setMessage("确定要删除此文件吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(file.delete()){
                                        //更新文件列表
                                        FilesInPath(file.getParent());
                                        displayToast("删除成功！");
                                    }
                                    else{
                                        displayToast("删除失败！");
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
            }
        };

        String[] menu = {"打开文件","重命名","删除文件"};
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("请选择要进行的操作!")
                .setItems(menu, listener)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void openFile(File file){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);

        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        startActivity(intent);
    }

    private String getMIMEType(File file){
        String type = "";
        String name = file.getName();
        //文件扩展名
        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")){
            type = "audio";
        }
        else if(end.equals("mp4") || end.equals("3gp")) {
            type = "video";
        }
        else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg") || end.equals("bmp") || end.equals("gif")){
            type = "image";
        }
        else {
            //如果无法直接打开，跳出列表由用户选择
            type = "*";
        }
        type += "/*";
        return type;
    }
    private void displayToast(String show) {
        Toast.makeText(MainActivity.this, show, Toast.LENGTH_SHORT).show();
    }
}
