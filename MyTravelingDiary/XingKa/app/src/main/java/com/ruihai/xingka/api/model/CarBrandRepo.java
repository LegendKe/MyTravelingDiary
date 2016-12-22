package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;
import com.orhanobut.hawk.Hawk;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zecker on 15/8/24.
 */
public class CarBrandRepo extends XKRepo {

    @SerializedName("carBrandInfo")
    private List<CarBrand> carBrandList;

    public List<CarBrand> getCarBrandList() {
        return carBrandList;
    }

    public void setCarBrandList(List<CarBrand> carBrandList) {
        this.carBrandList = carBrandList;
    }

    public class CarBrand implements Serializable {
        @SerializedName("carId")
        private int id;
        @SerializedName("carName")
        private String name;
        @SerializedName("carFristWord")
        private String firstWord;
        @SerializedName("carImg")
        private String image;
        @SerializedName("carSummary")
        private String summary;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFirstWord() {
            return firstWord;
        }

        public void setFirstWord(String firstWord) {
            this.firstWord = firstWord;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
    }

    // Cache
    public static int getCacheCarBrandVersion() {
        return Hawk.get("carBrandVersion", 0);
    }

    public static void setCacheCarBrandVersion(int version) {
        Hawk.put("carBrandVersion", version);
    }

    public static List<CarBrand> getCacheCarBrands() {
        return Hawk.get("carBrandList");
    }

    public static void setCacheCarBrands(List<CarBrand> carBrands) {
        Hawk.put("carBrandList", carBrands);
    }
}
