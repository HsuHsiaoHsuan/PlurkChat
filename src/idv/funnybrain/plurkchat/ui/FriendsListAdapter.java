package idv.funnybrain.plurkchat.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import idv.funnybrain.plurkchat.R;
import idv.funnybrain.plurkchat.data.Friend;
import idv.funnybrain.plurkchat.utils.DiskLruCache;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Freeman on 2014/4/7.
 */
public class FriendsListAdapter extends BaseAdapter {
    private static final boolean D = true;
    private static final String TAG = "FriendsListAdapter";

    private final LayoutInflater inflater;
    private List<Friend> friends;

    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache diskLruCache;
    private final Object diskLruCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    private static final int DISK_CACHE_INDEX = 0;
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    public FriendsListAdapter(LayoutInflater inflater, List<Friend> friends) {
        this.inflater = inflater;
        this.friends = friends;

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };


//        File cacheDir = getDiskCacheDir(inflater.getContext(), DISK_CACHE_SUBDIR);
//        new InitDiskCacheTask().execute(cacheDir);
    }

    @Override
    public int getCount() {
        return this.friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(friends.get(position).getId());
    }

    static class ViewHolder {
        public TextView tv_id;
        public ImageView iv_image;
        public TextView tv_title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            rowView = this.inflater.inflate(R.layout.friend_cell, null);
            ViewHolder holder = new ViewHolder();
            holder.tv_id = (TextView) rowView.findViewById(R.id.uid);
            holder.iv_image = (ImageView) rowView.findViewById(R.id.image);
            holder.tv_title = (TextView) rowView.findViewById(R.id.title);
            rowView.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        Friend f = this.friends.get(position);

        holder.tv_id.setText(f.getId());
        String showTitle = f.getDisplay_name().equals("") ? f.getFull_name() : f.getDisplay_name();
        holder.tv_title.setText(showTitle);

        String imgURL = "http://www.plurk.com/static/default_big.gif";
        if(f.getHas_profile_image()>0) {
            String avatar = f.getAvatar();
            if(avatar.equals("null")) {
                imgURL = "http://avatars.plurk.com/" + f.getId() + "-big.jpg";
            } else {
                if(avatar.equals("0")) { avatar = ""; }
                imgURL = "http://avatars.plurk.com/" + f.getId() + "-big" + avatar + ".jpg";
            }
        }

        loadBitmap(imgURL, holder.iv_image);

        return rowView;
    }

    // ---- for loading image START ----
    private void loadBitmap(String url, ImageView imageView) {
        final String imgKey = url;
        final Bitmap bitmap = getBitmapFromMemCache(imgKey);

        if(bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {

            Bitmap tmp = BitmapFactory.decodeResource(inflater.getContext().getResources(), R.drawable.default_plurk_avatar);
            if (cancelPotentialWork(url, imageView)) {
                final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                final AsyncDrawable asyncDrawable
                        = new AsyncDrawable(inflater.getContext().getResources(), tmp, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(url);
            }
        }
    }

    public static boolean cancelPotentialWork(String url, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.url;
            // If bitmapData is not yet set or it differs from the new data
            if (!bitmapData.equals(url)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewWeakReference;
        private String url = "";

        public BitmapWorkerTask(ImageView imageView) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            final String imgKey = params[0];

            // Check disk cache in background thread
//            Bitmap bitmap = getBitmapFromDiskCache(imgKey);
//            if(bitmap == null) {
//                bitmap = decodeSampledBitmapFromURL(imgKey, 100, 100);
//            }
//            addBitmapToCache(imgKey, bitmap);

            final Bitmap bitmap = decodeSampledBitmapFromURL(imgKey, 100, 100);
            addBitmapToMemoryCache(imgKey, bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(isCancelled()) {
                bitmap = null;
            }
            if(imageViewWeakReference != null && bitmap != null) {
                final ImageView imageView = imageViewWeakReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if(this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
            super.onPostExecute(bitmap);
        }
    }

    private static Bitmap decodeSampledBitmapFromURL(String url, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 15000); // Socket will open for 15 secs.
        HttpConnectionParams.setSoTimeout(httpParams, 15000); // Connection will live for 15 secs to wait response.
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            byte[] result = EntityUtils.toByteArray(httpResponse.getEntity());
            BitmapFactory.decodeByteArray(result, 0, result.length, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeByteArray(result, 0, result.length, options);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO should have a default image
        // FIXME should have a default image
        return null;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    // ---- for loading image END ----

    // ---- for memory caching image START ----
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
    // ---- for memory caching image END ----
}