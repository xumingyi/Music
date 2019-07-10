package com.duan.musicoco.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;

import com.duan.musicoco.R;
import com.duan.musicoco.app.manager.MediaManager;
import com.duan.musicoco.cache.BitmapCache;
import com.duan.musicoco.db.DBMusicocoController;
import com.duan.musicoco.image.BitmapBuilder;
import com.duan.musicoco.util.StringUtils;
import com.duan.musicoco.util.Utils;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

/**
 * Created by DuanJiaNing on 2017/6/21.
 */

public class Init {

    public static boolean xiaomiStatisticalervicesInitSuccess = false;

    public static Bitmap initAlbumVisualizerImageCache(Activity activity) {

        BitmapCache cache = new BitmapCache(activity, BitmapCache.CACHE_ALBUM_VISUALIZER_IMAGE);

        String key = StringUtils.stringToMd5(BitmapCache.DEFAULT_PIC_KEY);
        Bitmap result = cache.get(key);
        if (result == null) {
            DisplayMetrics metrics = Utils.getMetrics(activity);
            int r = metrics.widthPixels * 2 / 3;

            BitmapBuilder builder = new BitmapBuilder(activity);
            builder.resizeForDefault(r, r, R.drawable.default_album);
            builder.toRoundBitmap();
            builder.addOuterCircle(0, 10, Color.parseColor("#df3b43"))
                    .addOuterCircle(7, 1, Color.WHITE);

            cache.initDefaultBitmap(builder.getBitmap());
            return builder.getBitmap();
        }
        return result;
    }

    public static void initMusicocoDB(Context context, MediaManager mediaManager) {
        DBMusicocoController db = new DBMusicocoController(context, true);

        db.truncate(DBMusicocoController.TABLE_SONG);
        db.truncate(DBMusicocoController.TABLE_SHEET);

        db.addSongInfo(mediaManager.getSongList(context));

        db.addSheet(context.getString(R.string.default_sheet), context.getString(R.string.default_sheet_des), 0);

        db.close();
    }

    /**
     * 小米应用数据统计服务
     *
     * @see <a href="https://dev.mi.com/doc/p=3995/index.html"/> 说明文档
     */
    public static void initXiaomiStatisticalervices(Context context) {

        // 小米统计服务初始化
        try {
//            String CHANNEL = "xiaomi"; // 小米商店
            String CHANNEL = "alibaba"; // 阿里豌豆荚
            String APPID = Utils.getApplicationMetaData(context, "XIAOMI_APPID");
            String APPKEY = Utils.getApplicationMetaData(context, "XIAOMI_APPKEY");

            if (APPID != null && APPKEY != null) {
                MiStatInterface.initialize(context, APPID, APPKEY, CHANNEL);
                MiStatInterface.enableLog();
                MiStatInterface.enableExceptionCatcher(true);

                xiaomiStatisticalervicesInitSuccess = true;
            } else {
                xiaomiStatisticalervicesInitSuccess = false;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            xiaomiStatisticalervicesInitSuccess = false;
        }

    }
}
