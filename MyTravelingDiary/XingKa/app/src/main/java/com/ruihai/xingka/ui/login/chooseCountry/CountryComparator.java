/*      						
 * Copyright 2010 Beijing Xinwei, Inc. All rights reserved.
 * 
 * History:
 * ------------------------------------------------------------------------------
 * Date    	|  Who  		|  What  
 * 2015年3月18日	| duanbokan 	| 	create the file
 */

package com.ruihai.xingka.ui.login.chooseCountry;

import java.util.Comparator;

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

public class CountryComparator implements Comparator<CountrySortModel>
{

	@Override
	public int compare(CountrySortModel o1, CountrySortModel o2)
	{

		if (o1.sortLetters.equals("@") || o2.sortLetters.equals("#"))
		{
			return -1;
		}
		else if (o1.sortLetters.equals("#") || o2.sortLetters.equals("@"))
		{
			return 1;
		}
		else
		{
			return o1.sortLetters.compareTo(o2.sortLetters);
		}
	}

}
