package com.ruihai.xingka.ui.caption.datasource;

import android.net.Uri;

import com.ruihai.xingka.api.model.BannerInfoRepo;
import com.ruihai.xingka.entity.BannerItem;
import com.ruihai.xingka.utils.QiniuHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gjzhang on 15/12/12.
 */
public class BannerDataProvider {
    private List<BannerInfoRepo.BannerInfo> bannerInfoList;

    public BannerDataProvider(List<BannerInfoRepo.BannerInfo> bannerInfoList) {
        this.bannerInfoList = bannerInfoList;
    }

    public ArrayList<BannerItem> getList() {
        ArrayList<BannerItem> list = new ArrayList<>();
        for (int i = 0; i < bannerInfoList.size(); i++) {
            BannerItem item = new BannerItem();
            item.imgUrl = QiniuHelper.getOriginalWithKey(bannerInfoList.get(i).getImg());
            item.title = bannerInfoList.get(i).getTitle();
            list.add(item);
        }
        return list;
    }

}
