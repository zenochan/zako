/*
 * Copyright (C) 2011 Patrik Akerfeldt
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package name.zeno.android.widget;

import android.support.v4.view.ViewPager;

/**
 * A PageIndicator is responsible to show an visual indicator on the total views
 * number and the current visible view.
 */
public interface PageIndicator<T extends ViewPager> extends ViewPager.OnPageChangeListener
{
  /**
   * 绑定 view pager
   */
  void setViewPager(T view);

  /**
   * 绑定 view pager
   *
   * @param initialPosition 初始位置
   */
  void setViewPager(T view, int initialPosition);

  /**
   * <p>同时设置 indicate 和 view pager 当前选中</p>
   * <p/>
   * <p>This <strong>must</strong> be used if you need to set the page before
   * the views are drawn on screen (e.g., default start page).</p>
   */
  void setCurrentItem(int item);

  /**
   * Notify the indicator that the fragment list has changed.
   */
  void notifyDataSetChanged();
}
