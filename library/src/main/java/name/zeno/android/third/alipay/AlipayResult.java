package name.zeno.android.third.alipay;import android.text.TextUtils;import com.alipay.sdk.app.i;import java.util.Map;import lombok.Getter;public class AlipayResult{  /**   * @see com.alipay.sdk.app.i alipay 错误码 enum   * a(9000, "处理成功"),   * b(4000, "系统繁忙，请稍后再试"),   * c(6001, "用户取消"),   * d(6002, "网络连接异常"),   * e(4001, "参数错误"),   * f(5000, "重复请求"),   * g(8000, "支付结果确认中");   */  @Getter private int    resultStatus;  @Getter private String result;  @Getter private String memo;  public AlipayResult(String rawResult)  {    if (TextUtils.isEmpty(rawResult))      return;    String[] resultParams = rawResult.split(";");    for (String resultParam : resultParams) {      if (resultParam.startsWith("resultStatus")) {        resultStatus = Integer.valueOf(gatValue(resultParam, "resultStatus"));        i.a(resultStatus);      }      if (resultParam.startsWith("result")) {        result = gatValue(resultParam, "result");      }      if (resultParam.startsWith("memo")) {        memo = gatValue(resultParam, "memo");      }    }  }  public AlipayResult(Map<String, String> rawResult)  {    if (rawResult == null) {      return;    }    this.resultStatus = Integer.valueOf(rawResult.get("resultStatus"));    this.result = rawResult.get("result");    this.memo = rawResult.get("memo");  }  @Override  public String toString()  {    return "resultStatus={" + resultStatus + "};memo={" + memo        + "};result={" + result + "}";  }  private String gatValue(String content, String key)  {    String prefix = key + "={";    return content.substring(content.indexOf(prefix) + prefix.length(),        content.lastIndexOf("}"));  }}