package csu.task;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.github.mrengineer13.snackbar.SnackBar;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import csu.telecom.HomeActivity;
import csu.telecom.LoginActivity;
import csu.telecom.R;
import csu.utils.RSAUtil;

import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * Created by ubuntu on 15-5-13.
 */
public class LoginTask extends AsyncTask<String, Integer, Void> {

    private final static String KNOCK = "http://www.baidu.com"; //这里可以是除了http://61.137.86.87:8080/(电信)之外的所有,一般选百度
    private final static String REFER = "http://61.137.86.87:8080/portalNat444/index.jsp";//切记,作为refer欺骗服务器
    private final static String LOGIN = "http://61.137.86.87:8080/portalNat444/AccessServices/login";//切记

    private String password;
    private String account;
    private String brasAddress;
    private String userIntranetAddress;
    private String result;

    private HttpClient client = new DefaultHttpClient();

    private Context context;
    private boolean remember;
    private SharedPreferences preferences;


    private CircleProgressBar progressBar;

    public LoginTask(Context context, boolean remember) {
        this.context = context;
        progressBar = (CircleProgressBar) ((LoginActivity) context).findViewById(R.id.loginProgress);
        this.remember = remember;
    }


    public boolean getAddress() {
        try {
            //使用HttpURLConnection会有得不到brasAddress与userIntranetAddress的情况
            HttpGet request = new HttpGet(KNOCK);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                //获取登录网页信息网页,主要是brasAddress与userIntranetAddress
                String html = null;
                String temp;
                while ((temp = br.readLine()) != null) {
                    html += temp + "\n";
                    Log.d("Test",temp);
                }
                //使用jsoup获取网页内容
                Document doc = Jsoup.parse(html);
                brasAddress = doc.getElementById("brasAddress").val();
                userIntranetAddress = doc.getElementById("userIntranetAddress").val();
                //关闭连接
                br.close();
                is.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public String loginClient() {
        try {
            HttpPost request = new HttpPost(LOGIN);
            //添加参数 用户名,密码,公网内网地址等
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("accountID", account + "@zndx.inter"));
            //先加密
            String encrypt = new RSAUtil().getEncrypt(password);

            nvps.add(new BasicNameValuePair("password", encrypt));
            nvps.add(new BasicNameValuePair("brasAddress", brasAddress));
            nvps.add(new BasicNameValuePair("userIntranetAddress", userIntranetAddress));

            request.setEntity(new UrlEncodedFormEntity(nvps));
            request.setHeader(new BasicHeader("Referer", REFER));
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                Log.d("@zndx.inter", result);
                return result;//JSONArray
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected Void doInBackground(String... params) {
        //params[0]----->name
        //params[1]----->encrypt
        account = params[0];
        password = params[1];

        if (getAddress()) {
            this.result = loginClient();//结果
            // if (result != null) {
            //返回的是JSON数据,格式如下
                /*  {
                *   "password":"34babc7360866a3ee74f279a3ef8c95da5eca48b........,
                *   "accountID":"0139011203XX@zndx.inter",
                *   "vendor":"huawei",
                *   "time":69,
                *   "resultCode":"1",
                *   "secretKey":"huawei",
                *   "userIntranetAddress":"10.96.114.18",
                *   "brasAddress":"61.187.70.254",
                *   "resultDescribe":"该账号已在线",
                *   "userAgentType":"Apache-HttpClient/UNAVAILABLE (java 1.4)"
                *   }
                * */
            //成功登录的返回json
                /*  {
                *   "userSchoolOctets":"202.59",
                *   "password":"34babc7360866a3ee74.......",
                *   "surplusmoney":"35.13",
                *   "vendor":"huawei",
                *   "usedflow":"20667",
                *   "time":130,
                *   "secretKey":"huawei",
                *   "resultCode":"0",
                *   "account":"0139011203XX",
                *   "totalflow":"40960",
                *   "resultDescribe":"",
                *   "accountID":"013901120XXX@zndx.inter",
                *   "lastupdate":"2015-05-13 06:27:03",
                *   "surplusflow":"20293",
                *   "userIntranetAddress":"10.96.59.127",
                *   "brasAddress":"61.187.70.254",
                *   "username":"013901120325",
                *   "userAgentType":"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"
                *  }
                * */
        }
        // } else {
        //当然是出错...
        //  }
        return null;
    }


    @Override
    protected void onPreExecute() {

        //最开始被调用 显示加载进度条
        progressBar.setVisibility(View.VISIBLE);

        //判断网络状况
        if (!isWifiAvaliable()) {
            //显示网络不可用
            new SnackBar.Builder((LoginActivity) context).withBackgroundColorId(R.color.primary).withMessage("没有合适的WIFI连接...")
                    .withActionMessage("设置").withOnClickListener(new SnackBar.OnMessageClickListener() {
                @Override
                public void onMessageClick(Parcelable parcelable) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    context.startActivity(intent);
                }
            }).show();
            progressBar.setVisibility(View.GONE);
            cancel(true);
        }

        //首先应该判断用户是否在本机已经登录 查看sharePreferences是否存在Address,但是这种判断不是完美的,
        //因为有可能用户登录过,关掉wifi,5分钟后再打开WIFI,会显示已经登录了,肿么破?
        //监听wifi状态,当wifi离线超过5分钟删除?关机怎么办?有问题!!!!!!

        preferences = context.getSharedPreferences("use_address", 0);
        String brasAddress = preferences.getString("brasAddress", "");//判断一个即可
        if (!TextUtils.isEmpty(brasAddress)) {
            showSnackAlert("本机已经登录,不需要再登录\n注意,本条信息可能存在误判....见帮助.");
            progressBar.setVisibility(View.GONE);
            cancel(true);
        }


        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        try {
            JSONObject object = new JSONObject(result+"");
            int resultCode = object.getInt("resultCode");
            String resultDescribe = object.getString("resultDescribe");
            //如果result为空说明,登录失败,超连接时等错误
            if (TextUtils.isEmpty(result)) {
                showSnackAlert("未知原因,登录失败!");
            } else if (0 == resultCode) {
                //showSnackAlert("登录成功!");不需要,切换activity
                //成功登录需要保存把brasAddress与userIntranetAddress保存到本地sharePreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("brasAddress", brasAddress);
                editor.putString("userIntranetAddress", userIntranetAddress);
                editor.apply();

                //成功登录需要判断是否保存账户密码?
                if (remember) {
                    //就不写外面了
                    SharedPreferences preferences = context.getSharedPreferences("usr_info", 0);
                    SharedPreferences.Editor e = preferences.edit();
                    e.putString("username", account);
                    e.putString("password", password);//这里没有加密保存...
                    e.apply();
                }


                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra("json", result);//传递流量信息
                context.startActivity(intent);
            } else {
                showSnackAlert(resultDescribe);
            }
        } catch (JSONException e) {
            showSnackAlert("未知原因,登录失败!");
        }

        //关闭UI进度条
        progressBar.setVisibility(View.GONE);

        super.onPostExecute(aVoid);
    }

    private void showSnackAlert(String mention) {
        new SnackBar.Builder((Activity) context).withBackgroundColorId(R.color.primary).withMessage(mention).show();
    }


    private boolean isWifiAvaliable() {
        ConnectivityManager CM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = CM.getNetworkInfo(TYPE_WIFI);
        return info.isConnected();
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
