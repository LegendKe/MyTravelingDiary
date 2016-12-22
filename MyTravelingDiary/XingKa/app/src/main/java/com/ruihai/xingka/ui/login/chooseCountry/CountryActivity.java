package com.ruihai.xingka.ui.login.chooseCountry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.login.RegisterPhoneNumActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 *
 * Created by lqfang on 16/3/3.
 *
 */

public class CountryActivity extends BaseActivity {

	@BindView(R.id.tv_title)
	TextView mTitle;

	String TAG = "CountryActivity";

	private List<CountrySortModel> mAllCountryList;

	private EditText country_edt_search;

	private ListView country_lv_countryList;

	private ImageView country_iv_clearText;

	private CountrySortAdapter adapter;

//	private SideBar sideBar;

	private TextView dialog;

	private CountryComparator pinyinComparator;

	private GetCountryNameSort countryChangeUtil;

	private CharacterParserUtil characterParserUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coogame_country);
		ButterKnife.bind(this);

		mTitle.setText("选择国家");

		initView();

		setListener();

		getCountryList();

	}

	/**
	 * 初始化界面
	 */
	private void initView()
	{
		country_edt_search = (EditText) findViewById(R.id.country_et_search);
		country_lv_countryList = (ListView) findViewById(R.id.country_lv_list);
		country_iv_clearText = (ImageView) findViewById(R.id.country_iv_cleartext);

		dialog = (TextView) findViewById(R.id.country_dialog);
//		sideBar = (SideBar) findViewById(R.id.country_sidebar);
//		sideBar.setTextView(dialog);

		mAllCountryList = new ArrayList<CountrySortModel>();
		pinyinComparator = new CountryComparator();
		countryChangeUtil = new GetCountryNameSort();
		characterParserUtil = new CharacterParserUtil();

		// 将联系人进行排序，按照A~Z的顺序
		Collections.sort(mAllCountryList, pinyinComparator);
		adapter = new CountrySortAdapter(this, mAllCountryList);
		country_lv_countryList.setAdapter(adapter);

	}

	/****
	 * 添加监听
	 */
	private void setListener()
	{
		country_iv_clearText.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				country_edt_search.setText("");
				Collections.sort(mAllCountryList, pinyinComparator);
				adapter.updateListView(mAllCountryList);
			}
		});

		country_edt_search.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{

			}

			@Override
			public void afterTextChanged(Editable s)
			{
				String searchContent = country_edt_search.getText().toString();
				if (searchContent.equals(""))
				{
					country_iv_clearText.setVisibility(View.INVISIBLE);
				}
				else
				{
					country_iv_clearText.setVisibility(View.VISIBLE);
				}

				if (searchContent.length() > 0)
				{
					// 按照输入内容进行匹配
					ArrayList<CountrySortModel> fileterList = (ArrayList<CountrySortModel>) countryChangeUtil
							.search(searchContent, mAllCountryList);

					adapter.updateListView(fileterList);
				}
				else
				{
					adapter.updateListView(mAllCountryList);
				}
				country_lv_countryList.setSelection(0);
			}
		});

//		// 右侧sideBar监听
//		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener()
//		{
//
//			@Override
//			public void onTouchingLetterChanged(String s)
//			{
//				// 该字母首次出现的位置
//				int position = adapter.getPositionForSection(s.charAt(0));
//				if (position != -1)
//				{
//					country_lv_countryList.setSelection(position);
//				}
//			}
//		});

		country_lv_countryList.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3)
			{
				String countryName = null;
				String countryNumber = null;
				String searchContent = country_edt_search.getText().toString();

				if (searchContent.length() > 0)
				{
					// 按照输入内容进行匹配
					ArrayList<CountrySortModel> fileterList = (ArrayList<CountrySortModel>) countryChangeUtil
							.search(searchContent, mAllCountryList);
					countryName = fileterList.get(position).countryName;
					countryNumber = fileterList.get(position).countryNumber;
				}
				else
				{
					// 点击后返回
					countryName = mAllCountryList.get(position).countryName;
					countryNumber = mAllCountryList.get(position).countryNumber;
				}

				Intent intent = new Intent();
				//intent.setClass(CountryActivity.this, RegisterPhoneNumActivity.class);
				intent.putExtra("countryName", countryName);
				intent.putExtra("countryNumber", countryNumber);
				setResult(RESULT_OK, intent);
				Log.e(TAG, "countryName: + " + countryName + "countryNumber: " + countryNumber);
				finish();

			}
		});

	}

	/**
	 * 获取国家列表
	 */
	private void getCountryList()
	{
		String[] countryList = getResources().getStringArray(R.array.country_code_list_ch);

		for (int i = 0, length = countryList.length; i < length; i++)
		{
			String[] country = countryList[i].split("\\*");

			String countryName = country[0];
			String countryNumber = country[1];
			String countrySortKey = characterParserUtil.getSelling(countryName);
			CountrySortModel countrySortModel = new CountrySortModel(countryName, countryNumber,
					countrySortKey);
			String sortLetter = countryChangeUtil.getSortLetterBySortKey(countrySortKey);
			if (sortLetter == null)
			{
				sortLetter = countryChangeUtil.getSortLetterBySortKey(countryName);
			}

			countrySortModel.sortLetters = sortLetter;
			mAllCountryList.add(countrySortModel);
		}

		Collections.sort(mAllCountryList, pinyinComparator);
		adapter.updateListView(mAllCountryList);
		Log.e(TAG, "changdu" + mAllCountryList.size());
	}

	@OnClick(R.id.iv_back)
	void onBack(){
		finish();
	}
}
