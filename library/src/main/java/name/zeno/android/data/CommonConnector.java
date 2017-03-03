package name.zeno.android.data;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import name.zeno.android.data.service.CommonService;
import name.zeno.android.third.rxjava.RxUtils;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.fj.FastJsonConverterFactory;

/**
 * Create Date: 16/5/27
 *
 * @author 陈治谋 (513500085@qq.com)
 */
public class CommonConnector
{
  private static final String TAG = "Connector";

  private static CommonConnector instance;

  @SuppressWarnings("FieldCanBeLocal")
  private       Retrofit      retrofit;
  private final CommonService commonService;

  public static CommonConnector instance()
  {
    if (instance == null) {
      synchronized (CommonConnector.class) {
        if (instance == null) {
          instance = new CommonConnector();
        }
      }
    }
    return instance;
  }


  public static Call<ResponseBody> download(String apkUrl)
  {
    return instance().commonService.download(apkUrl);
  }

  public static Observable<String> get(String url)
  {
    return instance().commonService.get(url).compose(RxUtils.applySchedulers());
  }


  private CommonConnector()
  {
    OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .build();

    retrofit = new Retrofit.Builder()
        .baseUrl("http://www.baidu.com")
        .addConverterFactory(FastJsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build();

    commonService = retrofit.create(CommonService.class);
  }

}
