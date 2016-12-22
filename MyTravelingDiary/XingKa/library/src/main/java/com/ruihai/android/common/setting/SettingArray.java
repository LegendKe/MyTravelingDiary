package com.ruihai.android.common.setting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SettingArray extends SettingBean implements Serializable {

	private List<Setting> settingArray;

	private int index;
	
	public SettingArray() {
		settingArray = new ArrayList<Setting>();
	}

	public List<Setting> getSettingArray() {
		return settingArray;
	}

	public void setSettingArray(List<Setting> settingArray) {
		this.settingArray = settingArray;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
