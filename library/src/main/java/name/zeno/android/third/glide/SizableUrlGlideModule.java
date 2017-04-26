package name.zeno.android.third.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

public class SizableUrlGlideModule implements GlideModule
{
  @Override public void applyOptions(Context context, GlideBuilder builder) {
    builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    // nothing to do here
  }

  @Override public void registerComponents(Context context, Glide glide) {
    glide.register(SizableUrlModel.class, InputStream.class, new SizableUrlModelFactory());
  }
}