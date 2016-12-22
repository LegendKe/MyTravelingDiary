package com.ruihai.xingka.event;

import com.ruihai.xingka.widget.SwitchButton;

/**
 *
 * Created by mac on 16/7/25.
 */
public interface OnSwitchButtonClickListener {

    /***
     *
     * @param position 所选时间的位置
     * @param switchButton  按钮状态
     * @return enable check
     */
    void onClick(SwitchButton switchButton, int position);
}
