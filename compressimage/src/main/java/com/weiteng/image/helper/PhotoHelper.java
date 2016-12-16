package com.weiteng.image.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.weiteng.image.R;
import com.weiteng.image.util.CommonUtil;
import com.weiteng.image.util.ProviderPathUtil;
import com.weiteng.image.util.ToastUtil;

import java.io.File;
import java.util.UUID;

/**
 * 拍照帮助类
 *
 * @author weiTeng
 * @since 2015-12-23 14:06:52
 * @version v1.5.5
 */
public class PhotoHelper {

    public static final int IMAGE_REQUEST_CODE = 0;
    public static final int SELECT_PIC_KITKAT = 3;
    public static final int CAMERA_REQUEST_CODE = 1;

    private String mImageName;
    private File mDir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "compress");
    private Activity mActivity;
    private Fragment mFragment;
    private DisplayImageOptions mImageOptions;

    private ImageView mTargetImageView;

    public interface OnImageObtainListener{
        void onImageObtain(ThumbBitmap thumbBitmap);
    }

    public PhotoHelper(Activity activity){
        this.mActivity = activity;
    }

    /**
     * 如果应用场景是frgment需要传入fragment构造
     */
    public PhotoHelper(Activity activity, Fragment fragment){
        this.mActivity = activity;
        this.mFragment = fragment;
    }

    private OnImageObtainListener mObtainListener;

    public void setObtainListener(OnImageObtainListener obtainListener) {
        mObtainListener = obtainListener;
    }

    public void setTargetImageView(ImageView imageView){
        this.mTargetImageView = imageView;

        mImageOptions = new DisplayImageOptions
                .Builder()
                .showImageOnLoading(R.drawable.photo_default_icon)
                .showImageOnFail(R.drawable.photo_default_icon)
                .cacheInMemory(true)
                .build();
    }

    /**
     * 相册选择照片
     */
    public void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(mFragment != null) {
                mFragment.startActivityForResult(intent, SELECT_PIC_KITKAT);
            } else {
                mActivity.startActivityForResult(intent, SELECT_PIC_KITKAT);
            }
        } else {
            if(mFragment != null) {
                mFragment.startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }else{
                mActivity.startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        }
    }

    /**
     * 使用相机拍照
     */
    public synchronized void takePhoto() {
        if(mDir != null && !mDir.exists()){
            mDir.mkdir();
        }
        mImageName = String.format("%s.jpg", UUID.randomUUID().toString());
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (CommonUtil.hasSDCard()) {
            Uri uri = Uri.fromFile(new File(mDir, mImageName));
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        if(mFragment != null) {
            mFragment.startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
        }else{
            mActivity.startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    handleImage(data);
                    break;
                case SELECT_PIC_KITKAT:
                    handleImage(data);
                    break;
                case CAMERA_REQUEST_CODE:
                    if (CommonUtil.hasSDCard()) {
                        if(!TextUtils.isEmpty(mImageName)) {
                            File tempFile = new File(mDir, mImageName);
                            if(tempFile != null) {
                                handleImage(tempFile.getPath());
                                notifyMedia(tempFile);
                            }else{
                                ToastUtil.show(mActivity, "图片未找到");
                            }
                        }
                    } else {
                        ToastUtil.show(mActivity, "未找到存储卡，无法存储照片");
                    }
                    break;
            }
        }
    }

    private void notifyMedia(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        if (mActivity != null) {
            mActivity.sendBroadcast(intent);
        } else {
            mFragment.getActivity().sendBroadcast(intent);
        }
    }

    private void handleImage(Intent data) {
        String path = ProviderPathUtil.getPath(mActivity, data.getData());
        handleImage(path);
    }

    private void handleImage(String path){
        handleImageByRx(path);
    }

    private void handleImageByRx(String path) {
        if (TextUtils.isEmpty(path)) {
            ToastUtil.show(mActivity, "该图片不存在");
            return;
        }

        ImageLoader.getInstance()
                .displayImage(CommonUtil.getUriFromPath(path),
                        mTargetImageView,
                        mImageOptions,
                        new LocalPhotoLoadingListener(path));
    }

    private class LocalPhotoLoadingListener implements ImageLoadingListener {

        String path;

        LocalPhotoLoadingListener(String path) {
            this.path = path;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            ThumbBitmap thumbBitmap = new ThumbBitmap(path, loadedImage);
            if (mObtainListener != null) {
                mObtainListener.onImageObtain(thumbBitmap);
            }
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            ToastUtil.show(mActivity, "该路径下的图片无法加载");
        }

        @Override
        public void onLoadingStarted(String s, View view) {

        }

        @Override
        public void onLoadingCancelled(String s, View view) {

        }
    }

    public File getDir() {
        if (mDir != null && !mDir.exists()) {
            mDir.mkdir();
        }
        return mDir;
    }

    public class ThumbBitmap {

        Bitmap bmp;
        String path;
        int index;

        ThumbBitmap(String path, Bitmap bmp){
            this.path = path;
            this.bmp = bmp;
        }

        public String getPath() {
            return path;
        }

        public Bitmap getBmp() {
            return bmp;
        }

        public int getIndex(){ return index; }

        @Override
        public String toString() {
            return "ThumbBitmap{" +
                    "bmp=" + bmp +
                    ", path='" + path + '\'' +
                    ", index=" + index +
                    '}';
        }
    }
}
