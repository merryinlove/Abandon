package csu.task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.github.mrengineer13.snackbar.SnackBar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import csu.telecom.LoginActivity;
import csu.telecom.R;

/**
 * Created by ubuntu on 15-5-14.
 */
public class LogoutTask extends AsyncTask<Void, Void, Void> {

    private final static String URL = "http://61.137.86.87:8080/portalNat444/AccessServices/logout";
    private final static String REFER = "http://61.137.86.87:8080/portalNat444/main2.jsp";


    private HttpClient client = new DefaultHttpClient();

    private SharedPreferences preferences;
    private Context context;

    private String brasAddress;
    private String userIntranetAddress;

    private String result;


    public LogoutTask(Context context) {
        this.context = context;
    }

    private String logout() {
        try {
            HttpPost post = new HttpPost(URL);
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("brasAddress", brasAddress));
            nvps.add(new BasicNameValuePair("userIntranetAddress", userIntranetAddress));

            post.setEntity(new UrlEncodedFormEntity(nvps));
            post.setHeader(new BasicHeader("Referer", REFER));
            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected Void doInBackground(Void... params) {
        result = logout();
        return null;
    }

    @Override
    protected void onPreExecute() {
        preferences = context.getSharedPreferences("use_address", 0);
        brasAddress = preferences.getString("brasAddress", "");
        userIntranetAddress = preferences.getString("userIntranetAddress", "");
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        String alert = "";
        boolean exit = false;
        int resultCode = 0;
        if (TextUtils.isEmpty(result))
            alert = "登出出错..";
        else
            try {
                JSONObject object = new JSONObject(result);
                resultCode = object.getInt("resultCode");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        switch (resultCode) {
            case 0:
                alert = ("下线成功");
                removeAddress();
                exit = true;
                break;
            case 1:
                alert = ("服务器拒绝请求");
                break;
            case 2:
                alert = ("下线请求执行失败");
                break;
            case 3:
                alert = ("您已经下线");
                exit = true;
                removeAddress();
                break;
            case 4:
                alert = ("服务器响应超时");
                break;
            case 5:
                alert = ("后台网络连接异常");
                break;
            case 6:
                alert = ("服务脚本执行异常");
                break;
            case 7:
                alert = ("无法获取您的网络地址");
                removeAddress();
                exit = true;
                break;
            case 8:
                alert = ("无法获取您接入点设备地址");
                removeAddress();
                break;
            default:
                alert = ("未知错误");
                break;
        }


        if (exit) {

            if (context instanceof LoginActivity)
                new SnackBar.Builder((Activity) context).withBackgroundColorId(R.color.sb__button_text_color_red).withMessage(alert).show();
            else {
                new SnackBar.Builder((Activity) context).withBackgroundColorId(R.color.sb__button_text_color_red).withMessage(alert).withActionMessage("即将返回登陆").show();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ((Activity) context).finish();
                    }
                }, 2000);
            }
        } else
            new SnackBar.Builder((Activity) context).withBackgroundColorId(R.color.sb__button_text_color_red).withMessage(alert).show();
        super.onPostExecute(aVoid);
    }

    private void removeAddress() {
        SharedPreferences preferences = context.getSharedPreferences("use_address", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();
    }

}
