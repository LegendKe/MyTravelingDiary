package com.ruihai.xingka.api.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 发布旅拼封装类
 * Created by gebixiaozhang on 16/5/21.
 */
public class TravelTogetherInfo implements Serializable{
    public String title;//标题
    public String beginTime;//行程开始时间
    public String endTime;//行程结束时间
    public int costType;//费用类型
    public int personNum;//旅伴人数
    public String partnerContent;//旅伴要求
    public String content;//旅拼描述
    public String url;
    public double x, y;//经度
    public String address;//地址
    public ArrayList<TravelTogetherImgMoudle> imgModule;//旅拼图片
    public ArrayList<TravelLineMoudle> lineModule;//旅拼路线
public ArrayList<String> imgPath;


  /*
    {
        "beginTime": "2016-05-01",
            "endTime": "2016-05-07",
            "costType": 1,
            "personNum": 15,
            "partnerContent": "对旅伴的要求部分",     --最多30字
        "content": "旅拼描述部分",     --最多1000字
        "url": "http://www.xingka.cc",
            "x": 143.213,
            "y": 23.538,
            "address": "蔚蓝商务港",     ---最多30字
        "imgModule": [    --至少要有一张图片
        {
            "imgSrc": "00000000-0000-0000-0000-000000000000",
                "content": "图片描述部分1"  --最多200字
        },
        {
            "imgSrc": "11111111-1111-1111-1111-111111111111",
                "content": "图片描述部分2"
        }
        ],
        "lineModule": [    --至少要有一个地点
        {
            "x": 11.423,
                "y": 11.12,
                "address": "拉萨市"
        },
        {
            "x": 22.423,
                "y": 22.12,
                "address": "合肥市"
        }
        ]
    }
    */

}
