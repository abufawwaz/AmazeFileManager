package com.amaze.filemanager.filesystem.compressed.showcontents.helpers;

import android.content.Context;

import com.amaze.filemanager.adapters.data.CompressedObjectParcelable;
import com.amaze.filemanager.asynchronous.asynctasks.compress.ZipHelperTask;
import com.amaze.filemanager.filesystem.compressed.showcontents.Decompressor;

import java.util.ArrayList;
import com.amaze.filemanager.utils.OnAsyncTaskFinished;

/**
 * @author Emmanuel
 *         on 20/11/2017, at 17:19.
 */

public class ZipHelper extends Decompressor {

    public ZipHelper(Context context) {
        super(context);
    }

    @Override
    public ZipHelperTask changePath(String path, boolean addGoBackItem,
                           OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish) {
        return new ZipHelperTask(context, filePath, path, addGoBackItem, onFinish);
    }

}
