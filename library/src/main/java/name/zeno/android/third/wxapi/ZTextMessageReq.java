package name.zeno.android.third.wxapi;

import android.os.Parcel;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;

import io.reactivex.Observable;

/**
 * 发送文本消息
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/4/27
 */
public class ZTextMessageReq extends AbsReq
{
  private String text;
  @WXScene
  private int scene = WXScene.SESSION;

  public String getText()
  {
    return text;
  }

  public void setText(String text)
  {
    this.text = text;
  }

  public int getScene()
  {
    return scene;
  }

  public void setScene(@WXScene int scene)
  {
    this.scene = scene;
  }

  @Override public Observable<BaseReq> build()
  {
    WXTextObject   textObject = new WXTextObject(text);
    WXMediaMessage msg        = new WXMediaMessage(textObject);
    msg.description = text;

    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = "Zeno"; //transaction 字段用于唯一标识一个请求
    req.message = msg;
    req.scene = scene;

    return Observable.just(req);
  }

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel dest, int flags) {dest.writeString(this.text);}

  protected ZTextMessageReq(Parcel in) {this.text = in.readString();}

  public static final Creator<ZTextMessageReq> CREATOR = new Creator<ZTextMessageReq>()
  {
    @Override
    public ZTextMessageReq createFromParcel(Parcel source) {return new ZTextMessageReq(source);}

    @Override public ZTextMessageReq[] newArray(int size) {return new ZTextMessageReq[size];}
  };
}
