package com.magtuz.simplebrowser.common;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.WebResourceResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by magtuz on 9/28/2017.
 */

public class PageCache {

    final static String LOG_TAG = PageCache.class.getSimpleName();

    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60L * ONE_SECOND;
    public static final long ONE_HOUR = 60L * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;

    public static class CacheInfo {
        private String url;
        private String fileName;
        private String mimeType;
        private String encoding;
        private long maxAgeMillis;

        private CacheInfo(String url, String fileName,
                          String mimeType, String encoding, long maxAgeMillis) {

            this.url = url;
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.encoding = encoding;
            this.maxAgeMillis = maxAgeMillis;
        }

        public String getUrl() {
            return url;
        }

        public String getFileName() {
            return fileName;
        }

        public String getMimeType() {
            return mimeType;
        }

        public String getEncoding() {
            return encoding;
        }

        public long getMaxAgeMillis() {
            return maxAgeMillis;
        }
    }


    private Map<String, CacheInfo> cacheEntries = new HashMap<>();
    private Activity activity = null;
    private File rootDir = null;


    public PageCache(Activity activity) {
        this.activity = activity;
        this.rootDir = this.activity.getFilesDir();
    }

    public PageCache(Activity activity, File rootDir) {
        this.activity = activity;
        this.rootDir = rootDir;
    }

    public void register(String url, String cacheFileName, String mimeType, String encoding, long maxAgeMillis) {
        CacheInfo info = new CacheInfo(url, cacheFileName.replace("/", ""), mimeType, encoding, maxAgeMillis);
        this.cacheEntries.put(url, info);
    }


    public WebResourceResponse load(final String url) {
        final CacheInfo cacheInfo = this.cacheEntries.get(url);

        if (cacheInfo == null) return null;

        final File cachedFile = new File(this.rootDir.getPath() + File.separator + cacheInfo.fileName);

        if (cachedFile.exists()) {
            long cacheEntryAge = System.currentTimeMillis() - cachedFile.lastModified();
            if (cacheEntryAge > cacheInfo.maxAgeMillis) {
                cachedFile.delete();

                //cached file deleted, call load() again.
                Log.i(LOG_TAG, "Deleting from cache: " + url);
                return load(url);
            }

            //cached file exists and is not too old. Return file.
            Log.i(LOG_TAG, "Loading from cache: " + url);
            try {
                return new WebResourceResponse(cacheInfo.mimeType, cacheInfo.encoding, new FileInputStream(cachedFile));
            } catch (FileNotFoundException e) {
                Log.i(LOG_TAG, "Error loading cached file: " + cachedFile.getPath() + " : "
                        + e.getMessage(), e);
            }

        } else {

            if (NetworkUtil.isOnline(activity)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (downloadAndStore(url, cacheInfo)) {
                                load(url);
                            }
                        } catch (UnknownHostException e) {
                            Log.i(LOG_TAG, e.getMessage(), e);
                        } catch (Exception e) {
                            Log.i(LOG_TAG, "Error reading file over network: " + cachedFile.getPath(), e);
                        }
                    }
                }).start();
            } else {
                return null;
            }


        }

        return null;
    }


    private boolean downloadAndStore(String url, CacheInfo cacheInfo) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
        if (urlConnection.getResponseCode() / 100 == 2) {
            InputStream urlInput = urlConnection.getInputStream();

            FileOutputStream fileOutputStream = this.activity.openFileOutput(cacheInfo.fileName, Context.MODE_PRIVATE);

            int data = urlInput.read();
            while (data != -1) {
                fileOutputStream.write(data);
                data = urlInput.read();
            }

            urlInput.close();
            fileOutputStream.close();
            Log.i(LOG_TAG, "Cache file: " + cacheInfo.fileName + " stored. ");
            return true;
        } else {
            Log.i(LOG_TAG, "ResponseCode: " + urlConnection.getResponseCode());
            return false;
        }
    }
}
