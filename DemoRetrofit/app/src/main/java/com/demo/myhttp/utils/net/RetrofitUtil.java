package com.demo.myhttp.utils.net;

import android.text.TextUtils;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class RetrofitUtil {
    private static RetrofitUtil mInstance;

    private RetrofitUtil() {
        // nothing
    }

    public static synchronized RetrofitUtil getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitUtil();
        }

        return mInstance;
    }


    public Retrofit.Builder getRetrofit(String baseUrl) {
        Retrofit.Builder builder = new Retrofit.Builder();
        if (baseUrl != null) {
            builder.baseUrl(baseUrl);
        }
        return builder;
    }

    /**
     * 配置证书
     */
    private SSLUtils.SSLParams setSslConfig(/*OkHttpClient.Builder builder*/) {
        SSLUtils.SSLParams sslParams = null;

        InputStream[] certificates = null;
        InputStream bksFile = null;
        String password = null;
        if (null == certificates) {
            //信任所有证书,不安全有风险
            sslParams = SSLUtils.getSslSocketFactory();
        } else {
            if (null != bksFile && !TextUtils.isEmpty(password)) {
                //使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
                sslParams = SSLUtils.getSslSocketFactory(bksFile, password, certificates);
            } else {
                //使用预埋证书，校验服务端证书（自签名证书）
                sslParams = SSLUtils.getSslSocketFactory(certificates);
            }
        }

        //builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);

        return sslParams;
    }

    public OkHttpClient createOkHttps() {
        SSLUtils.SSLParams sslParams = setSslConfig();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return builder.readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier(SSLUtils.UnSafeHostnameVerifier)
                //.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel
                // (HttpLoggingInterceptor.Level.BODY))
                .addNetworkInterceptor(new MyHttpLoggingInterceptor().setLevel(MyHttpLoggingInterceptor.Level.BODY))
                .build();
                //添加公共请求头
//                .setHeaders(new BuildHeadersListener() {
//                    @Override
//                    public Map<String, String> buildHeaders() {
//                        HashMap<String, String> hashMap = new HashMap<>();
//                        hashMap.put("appVersion", BuildConfig.VERSION_NAME);
//                        hashMap.put("client", "android");
//                        hashMap.put("token", "your_token");
//                        hashMap.put("other_header", URLEncoder.encode("中文需要转码"));
//                        return hashMap;
//                    }
//                })
                //添加自定义拦截器
                //.setAddInterceptor()
                //开启缓存策略(默认false)
                //1、在有网络的时候，先去读缓存，缓存时间到了，再去访问网络获取数据；
                //2、在没有网络的时候，去读缓存中的数据。
//                .setCache(true)
//                .setHasNetCacheTime(10)//默认有网络时候缓存60秒
                //全局持久话cookie,保存到内存（new MemoryCookieStore()）或者保存到本地（new SPCookieStore(this)）
                //不设置的话，默认不对cookie做处理
//                .setCookieType(new SPCookieStore(this))
                //可以添加自己的拦截器(比如使用自己熟悉三方的缓存库等等)
                //.setAddInterceptor(null)
                //全局ssl证书认证
                //1、信任所有证书,不安全有风险（默认信任所有证书）
                //.setSslSocketFactory()
                //2、使用预埋证书，校验服务端证书（自签名证书）
                //.setSslSocketFactory(cerInputStream)
                //3、使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
                //.setSslSocketFactory(bksInputStream,"123456",cerInputStream)
                //设置Hostname校验规则，默认实现返回true，需要时候传入相应校验规则即可
                //.setHostnameVerifier(null)
                //全局超时配置
//                .setReadTimeout(10)
                //全局超时配置
//                .setWriteTimeout(10)
                //全局超时配置
//                .setConnectTimeout(10)
                //全局是否打开请求log日志
//                .setDebug(BuildConfig.DEBUG)
//                .build();

//        return okHttpClient;
    }

    public OkHttpClient createOkHttp() {
        SSLUtils.SSLParams sslParams = setSslConfig();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return builder.readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                //.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel
                // (HttpLoggingInterceptor.Level.BODY))
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }
}
