package com.csu.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.csu.telecom.HomeActivity;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
        String result = logout();
        if (!TextUtils.isEmpty(result)) {
            new SnackBar.Builder((HomeActivity) context).withMessage(result).show();
        }
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
        super.onPostExecute(aVoid);
    }
}
