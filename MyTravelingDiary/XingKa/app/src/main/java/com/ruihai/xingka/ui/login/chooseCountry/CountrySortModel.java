/*      						
 * Copyright 2010 Beijing Xinwei, Inc. All rights reserved.
 * 
 * History:
 * ------------------------------------------------------------------------------
 * Date    	|  Who  		|  What  
 * 2015年3月21日	| duanbokan 	| 	create the file
 */

package com.ruihai.xingka.ui.login.chooseCountry;

/**
 *
 * 类简要描述
 *
 * <p>
 * 类详细描述
 * </p>
 *
 * @author duanbokan
 *
 */

public class CountrySortModel extends CountryModel

{
	// 显示数据拼音的首字母
	public String sortLetters;

	public CountrySortToken sortToken = new CountrySortToken();

	public CountrySortModel(String name, String number, String countrySortKey)
	{
		super(name, number, countrySortKey);
	}

}
