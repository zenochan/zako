package name.zeno.android.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import name.zeno.android.listener.Action0;
import name.zeno.android.util.R;

/**
 * Create Date: 16/6/15
 *
 * @author 陈治谋 (513500085@qq.com)
 */
@SuppressWarnings("unused")
public class SimpleActionbar extends ZAppBarLayout implements View.OnClickListener
{
  private Action0 onClickPre;
  private Action0 onClickAction;

  public void setOnClickPre(Action0 onClickPre)
  {
    this.onClickPre = onClickPre;
  }

  public void setOnClickAction(Action0 onClickAction)
  {
    this.onClickAction = onClickAction;
  }

  private ZTextView preTv;
  private TextView  titleTv;
  private ZTextView actionTv;
  private View      appbar;

  public View getAppbar()
  {
    return appbar;
  }

  public SimpleActionbar(Context context)
  {
    this(context, null);
  }

  public SimpleActionbar(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    appbar = LayoutInflater.from(context).inflate(R.layout.view_simple_actionbar, this);
    preTv = (ZTextView) appbar.findViewById(R.id.tv_pre);
    titleTv = (TextView) appbar.findViewById(R.id.tv_title);
    actionTv = (ZTextView) appbar.findViewById(R.id.tv_action);
    preTv.setOnClickListener(this);
    actionTv.setOnClickListener(this);

    initAttrs(context, attrs);
    if (context instanceof Activity) {
      onClickPre = ((Activity) context)::onBackPressed;
    }
  }

  public SimpleActionbar setTitleText(String text)
  {
    titleTv.setText(text);
    return this;
  }

  public SimpleActionbar setActionText(String text)
  {
    actionTv.setText(text);
    return this;
  }

  public SimpleActionbar setActionText(@StringRes int resId)
  {
    actionTv.setText(resId);
    return this;
  }

  public void setPreEnable(boolean enable)
  {
    preTv.setVisibility(enable ? VISIBLE : GONE);
  }

  public void setActionEnable(boolean enable)
  {
    actionTv.setVisibility(enable ? VISIBLE : GONE);
  }

  public SimpleActionbar setActionTextColor(@ColorInt int color)
  {
    actionTv.setTextColor(color);
    return this;
  }

  public SimpleActionbar setActionTextVisibility(int visibility)
  {
    actionTv.setVisibility(visibility);
    return this;
  }

  @Override
  public void onClick(View view)
  {
    int i = view.getId();
    if (i == R.id.tv_pre) {
      if (onClickPre != null) {
        onClickPre.call();
      }
    } else if (i == R.id.tv_action) {
      if (onClickAction != null) {
        onClickAction.call();
      }
    }
  }

  private void initAttrs(Context context, AttributeSet attrs)
  {
    if (context == null || attrs == null) {
      return;
    }

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SimpleActionbar);

    if (ta.hasValue(R.styleable.SimpleActionbar_preTextBackgroundTint)) {
      ColorStateList tint = ta.getColorStateList(R.styleable.SimpleActionbar_preTextBackgroundTint);
      preTv.setSupportCompoundDrawableTintList(tint);
      preTv.setVisibility(VISIBLE);
    }

    if (ta.hasValue(R.styleable.SimpleActionbar_preText)) {
      preTv.setText(ta.getString(R.styleable.SimpleActionbar_preText));
      preTv.setVisibility(VISIBLE);
    }
    if (ta.hasValue(R.styleable.SimpleActionbar_preEnable)) {
      boolean enable = ta.getBoolean(R.styleable.SimpleActionbar_preEnable, false);
      preTv.setVisibility(enable ? VISIBLE : GONE);
    }

    if (ta.hasValue(R.styleable.SimpleActionbar_titleText)) {
      titleTv.setText(ta.getString(R.styleable.SimpleActionbar_titleText));
    } else if (isInEditMode()) {
      titleTv.setText("app:titleText");
    }

    if (ta.hasValue(R.styleable.SimpleActionbar_textColorTitle)) {
      titleTv.setTextColor(ta.getColorStateList(R.styleable.SimpleActionbar_textColorTitle));
    }

    if (ta.hasValue(R.styleable.SimpleActionbar_actionText)) {
      actionTv.setText(ta.getString(R.styleable.SimpleActionbar_actionText));
      actionTv.setVisibility(VISIBLE);
    }

    if (ta.hasValue(R.styleable.SimpleActionbar_textColorAction)) {
      actionTv.setTextColor(ta.getColorStateList(R.styleable.SimpleActionbar_textColorAction));
    }

    if (ta.hasValue(R.styleable.SimpleActionbar_actionDrawable)) {
      Drawable drawable = ta.getDrawable(R.styleable.SimpleActionbar_actionDrawable);
      assert drawable != null;
      drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
      actionTv.setCompoundDrawables(null, null, drawable, null);
    }

    if (ta.hasValue(R.styleable.SimpleActionbar_actionDrawableTint)) {
      ColorStateList tint = ta.getColorStateList(R.styleable.SimpleActionbar_actionDrawableTint);
      actionTv.setSupportCompoundDrawableTintList(tint);
    }

    if (ta.hasValue(R.styleable.SimpleActionbar_actionEnable)) {
      boolean enable = ta.getBoolean(R.styleable.SimpleActionbar_actionEnable, false);
      actionTv.setVisibility(enable ? VISIBLE : GONE);
    }

    boolean whiteBar = ta.getBoolean(R.styleable.SimpleActionbar_whiteBar, false);
    if (whiteBar) {
      setBackgroundColor(Color.WHITE);
    }

    ta.recycle();
  }

  public void setTitleColor(@ColorInt int color)
  {
    titleTv.setTextColor(color);
  }

  public void setPreTint(@ColorInt int color)
  {
    ColorStateList tint = new ColorStateList(new int[][]{{}}, new int[]{color});
    preTv.setSupportCompoundDrawableTintList(tint);
  }
}
