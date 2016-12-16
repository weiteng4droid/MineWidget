package com.weiteng.image.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;

import com.weiteng.image.R;
import com.weiteng.image.luban.Luban;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * 图片缩放处理的工具类
 * @author weiTeng
 * @since 2015年10月21日16:34:32
 */
public class BitmapUtil {

	private static final String TAG = "BitmapUtil";

	private static final int IMAGE_SIZE = 32 * 1024;	//	微信分享图片大小限制

	private BitmapUtil() {}

	/**
	 * 计算缩放的缩放比例
	 * @param options		选项
	 * @param reqWidth		需要的宽度
	 * @param reqHeight		需要的高度
	 * @return	int 		缩放比例
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}


	/**
	 * 从sd卡上加载图片
	 * @param pathName		图片的路径
	 * @param reqWidth		需要的宽度
	 * @param reqHeight		需要的高度
	 * @return bitmap of scaled from source bitmap
	 */
	public static Bitmap decodeSampledBitmapFromFd(String pathName, int reqWidth, int reqHeight) {
		return decodeSampledBitmapFromFd(pathName, reqWidth, reqHeight, true);
	}

	public static Bitmap decodeSampledBitmapFromFd(String pathName, int reqWidth, int reqHeight, boolean compress){
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inPurgeable = true;
			BitmapFactory.decodeFile(pathName, options);
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
			options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(pathName), null, options);
			if(compress) {
				return compressImage(bitmap);
			}else{
				return bitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "decodeSampledBitmapFromFd " + e.toString());
			return null;
		}
	}

	/**
	 * 压缩图片
	 */
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;

		Log.d(TAG, "压缩前：size = " + baos.size() / 1024 + "kb");

		while (baos.toByteArray().length / 1024 > 1024) {		// 如果大于1Mb继续压缩
			if (options < 50) {
				break;
			}
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;
		}

		Log.d(TAG, "压缩后：size = " + baos.size() / 1024 + "kb");

		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		return BitmapFactory.decodeStream(isBm, null, null);
	}

	/**
	 * bitmap图片装换成字符串
	 */
	@Deprecated
	public static String transformBitmapToString(String path){
		Bitmap bitmap = decodeSampledBitmapFromFd(path, 600, 800);
		String dataString;

		// Transform bitmap to base64 String
		if (bitmap != null) {
			dataString = Base64.encodeToString(BitmapUtil.bitmapToBytes(bitmap), Base64.DEFAULT);
		} else {
			dataString = "";
		}

		// recycle bitmap if needed
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			System.gc();
		}
		return dataString;
	}

	public static byte[] bitmapToBytes(Bitmap bitmap){
		if (bitmap == null) {
			return null;
		}
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
		return os.toByteArray();
	}

	/**
	 * 使用类微信压缩图片技术压缩图片
	 *
     */
	public static String transformBitmap2Base64String(String fileName) {
		byte[] bytes = Luban.get()
				.load(new File(fileName))
				.putGear(Luban.THIRD_GEAR)
				.launch();

		String base64String;
		if (bytes != null && bytes.length > 0) {
			base64String = Base64.encodeToString(bytes, Base64.DEFAULT);
		} else {
			base64String = "";
		}
		return base64String;
	}

	/**
	 * Composite bitmap
	 */
	@NonNull
	public static Bitmap compositeBitmap(Context context, int length, Bitmap destBitmap) {
		Bitmap bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.composite_bg);

		Bitmap[] bitmaps = new Bitmap[length];
		for (int i = 0; i < length; i++) {
			bitmaps[i] = bg;
		}

		int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
		int len = bitmaps.length;
		int gap = 15;
		int subSize = size - (gap * (len + 1));
		int mLeft = 0;
		int mTop = 0;
		int mShadowOffset = 10;

		Bitmap mainBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mainBitmap);
		canvas.drawColor(0xfff9f7fa);

		Rect distRect = null;

		for (int i = 0; i < len; i++) {
			Bitmap bitmap = bitmaps[i];
			if (bitmap != null) {
				canvas.save();

				int left = mLeft + (len - i) * gap;
				int top = mTop + (i + 1) * gap;
				distRect = new Rect(left, top, left + subSize, top + subSize);

				canvas.drawBitmap(bitmap, null, distRect, null);
				canvas.restore();
			}
		}
		if (destBitmap != null) {
			canvas.save();
			distRect.set(distRect.left, distRect.top, distRect.right - mShadowOffset, distRect.bottom - mShadowOffset);
			canvas.drawBitmap(destBitmap, null, distRect, null);
			canvas.restore();
		}
		return mainBitmap;
	}

	/**
	 * Composite bitmap
	 */
	@NonNull
	public static Bitmap compositeBitmap(Context context, Bitmap[] bitmaps) {
		int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
		int len = bitmaps.length;
		int gap = 15;
		int subSize = size - (gap * (len + 1));
		int mLeft = 0;
		int mTop = 0;

		Bitmap mainBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mainBitmap);
		canvas.drawColor(0xfff9f7fa);		// background color

		for (int i = 0; i < len; i++) {
			Bitmap bitmap = bitmaps[i];
			if (bitmap != null) {
				canvas.save();

				int left = mLeft + (len - i) * gap;
				int top = mTop + (i + 1) * gap;
				Rect rect = new Rect(left, top, left + subSize, top + subSize);

				canvas.drawBitmap(bitmap, null, rect, null);
				canvas.restore();
			}
		}
		return mainBitmap;
	}

	/**
	 * Draw a tag bitmap
	 */
	@NonNull
	public static Bitmap drawTagOnBitmap(Context context, int id, String tagText) {
		Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(0xffe4544f);

		Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG | Paint.DITHER_FLAG);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTextSize(
				TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, context.getResources().getDisplayMetrics()));
		mTextPaint.setColor(Color.WHITE);

		float mTextOffsetY = (mTextPaint.descent() + mTextPaint.ascent()) / 2;

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		Bitmap mainBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

		Rect mTagRect = new Rect();
		mTagRect.left = 0;
		mTagRect.top = 0;
		mTagRect.right = (int) (mTagRect.left + height * 0.30f);
		mTagRect.bottom = (int) (mTagRect.top + height * 0.30f);

		Rect tempRect = new Rect();
		mTextPaint.getTextBounds(tagText, 0, tagText.length(), tempRect);
		int tagWidth = tempRect.height() +
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics()) * 2;
		int offset = (int) (tagWidth / 2.0f + 0.5);
		int d = (int) (offset * Math.sin(45));

		Path path = new Path();
		path.moveTo(mTagRect.right - d, mTagRect.top);
		path.lineTo(mTagRect.right + d, mTagRect.top);
		path.lineTo(mTagRect.left, mTagRect.bottom + d);
		path.lineTo(mTagRect.left, mTagRect.bottom - d);
		path.close();

		Canvas canvas = new Canvas(mainBitmap);
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.save();
		canvas.drawPath(path, mPaint);
		canvas.restore();

		canvas.save();
		canvas.translate(mTagRect.centerX(), mTagRect.centerY());
		canvas.rotate(-45);
		canvas.drawText(tagText, 0, 0 - mTextOffsetY, mTextPaint);
		canvas.restore();
		return mainBitmap;
	}

	/**
	 * Handle big image for shared of WeChat
	 *
     */
	public static Bitmap compressImageForShared(Bitmap bitmap) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		int options = 100;
		while (bos.toByteArray().length > IMAGE_SIZE && options != 10) {
			bos.reset();
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, bos);
			options -= 10;
		}
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}

		ByteArrayInputStream bios = new ByteArrayInputStream(bos.toByteArray());
		return BitmapFactory.decodeStream(bios, null, null);
	}
}
