package centralsoft.uco.edu.centralchat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Stanislav on 10/21/2015.
 */
public class Utils {

    public String getDeviceMacAddress(Context ct) {
        WifiManager wifiManager = (WifiManager) ct.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress();
    }

    public Bitmap getRoundedShape(Bitmap bitmap) {
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        Canvas c = new Canvas(circleBitmap);
        //This draw a circle of Gerycolor which will be the border of image.
        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setAntiAlias(true);
        paint.setShader(shader);
        // This will draw the image.
        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2 - 2, paint);
        return circleBitmap;
    }

    public String getDate() {
        //This gives the current date and time as numeric values formatted as shown below .
        Calendar calendar = Calendar.getInstance();
        String correct = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        String currentDate = sdf.format(calendar.getTime());
        //This gives the current day as a string. Ex: Monday, Tuesday, etc.
        SimpleDateFormat sdf_ = new SimpleDateFormat("EEEE");
        Date date = new Date();
        String dayName = sdf_.format(date);
        correct = ("" + dayName + " " + currentDate + "");
        return correct;
    }

}
