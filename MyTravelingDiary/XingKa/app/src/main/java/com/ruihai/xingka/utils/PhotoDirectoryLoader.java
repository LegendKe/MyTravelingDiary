package com.ruihai.xingka.utils;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.support.v4.content.CursorLoader;

import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

public class PhotoDirectoryLoader extends CursorLoader {

  final String[] IMAGE_PROJECTION = {
      Media._ID,
      Media.DATA,
      Media.BUCKET_ID,
      Media.BUCKET_DISPLAY_NAME,
      Media.DATE_ADDED
  };

  public PhotoDirectoryLoader(Context context) {
    super(context);

    setProjection(IMAGE_PROJECTION);
    setUri(Media.EXTERNAL_CONTENT_URI);
    setSortOrder(Media.DATE_ADDED + " DESC");

    setSelection(MIME_TYPE + "=? or " + MIME_TYPE + "=? or " + MIME_TYPE + "=?");
    setSelectionArgs(new String[] { "image/jpeg", "image/png", "image/gif" });
  }


  private PhotoDirectoryLoader(Context context, Uri uri, String[] projection, String selection,
      String[] selectionArgs, String sortOrder) {
    super(context, uri, projection, selection, selectionArgs, sortOrder);
  }


}
