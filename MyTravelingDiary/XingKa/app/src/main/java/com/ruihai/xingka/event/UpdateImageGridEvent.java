package com.ruihai.xingka.event;

import java.util.ArrayList;

/**
 * Created by zecker on 15/12/2.
 */
public class UpdateImageGridEvent {

    private ArrayList<String> selectedImages;

    public UpdateImageGridEvent(ArrayList<String> selectedImages) {
        this.selectedImages = selectedImages;
    }

    public ArrayList<String> getSelectedImages() {
        return selectedImages;
    }
}
