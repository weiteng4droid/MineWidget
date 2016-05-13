package com.botpy.demo.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by weiTeng on 2016/4/28.
 */
public class CacheManager {

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private static String hashKeyForDiskCache(String key) {
        String cacheKey;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            cacheKey = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static DiskLruCache openDiskLruCache(Context context, String uniqueName) {
        try {
            File cacheDir = getDiskCacheDir(context, uniqueName);
            if (cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            return DiskLruCache.open(cacheDir, getAppVersion(context), 1, 8 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void cacheBitmap(Context context, String key, Bitmap bitmap) {
        try {
            DiskLruCache diskLruCache = openDiskLruCache(context, "bitmap");
            if (diskLruCache != null) {
                DiskLruCache.Editor editor = diskLruCache.edit(hashKeyForDiskCache(key));
                OutputStream out = editor.newOutputStream(0);
                boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
                if (success) {
                    editor.commit();
                } else {
                    editor.abort();
                }
                diskLruCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapForKey(Context context, String key) {
        try {
            DiskLruCache diskLruCache = openDiskLruCache(context, "bitmap");
            if (diskLruCache != null) {
                DiskLruCache.Snapshot snapshot = diskLruCache.get(hashKeyForDiskCache(key));
                if (snapshot != null) {
                    return BitmapFactory.decodeStream(snapshot.getInputStream(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> void cacheModel(Context context, String key, T model) {
        try {
            DiskLruCache diskLruCache = openDiskLruCache(context, "model");
            if (diskLruCache != null) {
                DiskLruCache.Editor editor = diskLruCache.edit(hashKeyForDiskCache(key));
                ObjectOutputStream oos = new ObjectOutputStream(editor.newOutputStream(0));
                oos.writeObject(model);
                oos.flush();
                editor.commit();
                diskLruCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T getModelForKey(Context context, String key) {
        try {
            DiskLruCache diskLruCache = openDiskLruCache(context, "model");
            if (diskLruCache != null) {
                DiskLruCache.Snapshot snapshot = diskLruCache.get(hashKeyForDiskCache(key));
                if (snapshot != null) {
                    ObjectInputStream ois = new ObjectInputStream(snapshot.getInputStream(0));
                    return (T) ois.readObject();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> void cacheModels(Context context, String key, List<T> models) {
        try {
            DiskLruCache diskLruCache = openDiskLruCache(context, "models");
            if (diskLruCache != null) {
                DiskLruCache.Editor editor = diskLruCache.edit(hashKeyForDiskCache(key));
                ObjectOutputStream oos = new ObjectOutputStream(editor.newOutputStream(0));
                oos.writeObject(models);
                oos.flush();
                editor.commit();
                diskLruCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> List<T> getModelsForKey(Context context, String key) {
        try {
            DiskLruCache diskLruCache = openDiskLruCache(context, "models");
            if (diskLruCache != null) {
                DiskLruCache.Snapshot snapshot = diskLruCache.get(hashKeyForDiskCache(key));
                if (snapshot != null) {
                    ObjectInputStream ois = new ObjectInputStream(snapshot.getInputStream(0));
                    return (List<T>) ois.readObject();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
