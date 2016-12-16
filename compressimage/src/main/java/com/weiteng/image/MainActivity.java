package com.weiteng.image;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.weiteng.image.helper.PhotoHelper;
import com.weiteng.image.luban.Luban;
import com.weiteng.image.util.CommonUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements PhotoHelper.OnImageObtainListener {

    private PhotoHelper mPhotoHelper;

    private String mImagePath;

    private ProgressBar mProgressBar;
    private ImageView mAfterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.display_company_pb);
        ImageView mPreImageView = (ImageView) findViewById(R.id.pre_compress);
        mAfterImageView = (ImageView) findViewById(R.id.after_compress);

        mPhotoHelper = new PhotoHelper(this);
        mPhotoHelper.setObtainListener(this);
        mPhotoHelper.setTargetImageView(mPreImageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPhotoHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_select:
                mPhotoHelper.selectPhoto();
                break;

            case R.id.action_compress:
                compress();
                break;

            case R.id.action_settings:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onImageObtain(PhotoHelper.ThumbBitmap thumbBitmap) {
        mImagePath = thumbBitmap.getPath();
    }

    private void compress() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String name = compressImage(mImagePath);
                subscriber.onNext(name);
                subscriber.onCompleted();
            }
        })
        .subscribeOn(Schedulers.io())
        .doOnSubscribe(new Action0() {
            @Override
            public void call() {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(new Action0() {
            @Override
            public void call() {
                mProgressBar.setVisibility(View.GONE);
            }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<String>() {
            @Override
            public void call(String path) {
                ImageLoader.getInstance().displayImage(CommonUtil.getUriFromPath(path), mAfterImageView);
            }
        });
    }

    private String compressImage(String pathName) {
        byte[] bytes = Luban.get()
                .load(new File(pathName))
                .putGear(Luban.THIRD_GEAR)
                .launch();

        String fileName = UUID.randomUUID().toString() + ".jpg";
        File file = new File(mPhotoHelper.getDir(), fileName);
        try {
            OutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
