/*
 * Copyright (C) 2014 Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amaze.filemanager.asynchronous.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.widget.Toast;

import com.amaze.filemanager.R;
import com.amaze.filemanager.activities.MainActivity;
import com.amaze.filemanager.database.CryptHandler;
import com.amaze.filemanager.exceptions.RootNotPermittedException;
import com.amaze.filemanager.filesystem.HybridFileParcelable;
import com.amaze.filemanager.fragments.ZipExplorerFragment;
import com.amaze.filemanager.utils.DataUtils;
import com.amaze.filemanager.utils.OTGUtil;
import com.amaze.filemanager.utils.OpenMode;
import com.amaze.filemanager.utils.cloud.CloudUtil;
import com.amaze.filemanager.utils.files.CryptUtil;
import com.cloudrail.si.interfaces.CloudStorage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DeleteTask extends AsyncTask<Void, String, Boolean> {

    private WeakReference<Context> context;
    private ArrayList<HybridFileParcelable> files;
    private boolean rootMode;
    private ZipExplorerFragment zipExplorerFragment;
    private DataUtils dataUtils = DataUtils.getInstance();

    public DeleteTask(Context context, ArrayList<HybridFileParcelable> files) {
        this.context = new WeakReference<>(context);
        this.files = files;
        rootMode = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("rootmode", false);
    }

    public DeleteTask(Context context, ZipExplorerFragment zipExplorerFragment, ArrayList<HybridFileParcelable> files) {
        this(context, files);
        this.zipExplorerFragment = zipExplorerFragment;
    }

    protected Boolean doInBackground(Void p[]) {
        final Context context = this.context.get();
        if (context == null) return null;

        boolean succeded = true;
        if (files.size() == 0) return true;

        if (files.get(0).isOtgFile()) {
            for (HybridFileParcelable a : files) {
                DocumentFile documentFile = OTGUtil.getDocumentFile(a.getPath(), context, false);
                succeded = documentFile.delete();
            }
        } else if (files.get(0).isDropBoxFile()) {
            CloudStorage cloudStorageDropbox = dataUtils.getAccount(OpenMode.DROPBOX);
            for (HybridFileParcelable baseFile : files) {
                cloudStorageDropbox.delete(CloudUtil.stripPath(OpenMode.DROPBOX, baseFile.getPath()));
            }
        } else if (files.get(0).isBoxFile()) {
            CloudStorage cloudStorageBox = dataUtils.getAccount(OpenMode.BOX);
            for (HybridFileParcelable baseFile : files) {
                cloudStorageBox.delete(CloudUtil.stripPath(OpenMode.BOX, baseFile.getPath()));
            }
        } else if (files.get(0).isGoogleDriveFile()) {
            CloudStorage cloudStorageGdrive = dataUtils.getAccount(OpenMode.GDRIVE);
            for (HybridFileParcelable baseFile : files) {
                cloudStorageGdrive.delete(CloudUtil.stripPath(OpenMode.GDRIVE, baseFile.getPath()));
            }
        } else if (files.get(0).isOneDriveFile()) {
            CloudStorage cloudStorageOnedrive = dataUtils.getAccount(OpenMode.ONEDRIVE);
            for (HybridFileParcelable baseFile : files) {
                cloudStorageOnedrive.delete(CloudUtil.stripPath(OpenMode.ONEDRIVE, baseFile.getPath()));
            }
        } else {
            for (HybridFileParcelable a : files) {
                try {
                    (a).delete(context, rootMode);
                } catch (RootNotPermittedException e) {
                    e.printStackTrace();
                    succeded = false;
                }
            }
        }

        // delete file from media database
        if (!files.get(0).isSmb()) {
            for (HybridFileParcelable f : files) {
                delete(context, f.getPath());
            }
        }

        // delete file entry from encrypted database
        for (HybridFileParcelable file : files) {
            if (file.getName().endsWith(CryptUtil.CRYPT_EXTENSION)) {
                CryptHandler handler = new CryptHandler(context);
                handler.clear(file.getPath());
            }
        }

        return succeded;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        final Context context = this.context.get();
        if(context == null) return;

        Toast.makeText(context, values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostExecute(Boolean succeded) {
        final Context context = this.context.get();
        if(context == null) return;

        Intent intent = new Intent(MainActivity.KEY_INTENT_LOAD_LIST);
        String path = files.get(0).getParent(context);
        intent.putExtra(MainActivity.KEY_INTENT_LOAD_LIST_FILE, path);
        context.sendBroadcast(intent);

        if (!succeded) {
            Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
        } else if (zipExplorerFragment ==null) {
            Toast.makeText(context, context.getResources().getString(R.string.done), Toast.LENGTH_SHORT).show();
        }

        if (zipExplorerFragment !=null) {
            zipExplorerFragment.files.clear();
        }
    }

    private void delete(@NonNull final Context context, final String file) {
        // Delete the entry from the media database. This will actually delete media files.
        context.getContentResolver().delete(MediaStore.Files.getContentUri("external"),
                MediaStore.MediaColumns.DATA + "=?",  new String[] {file});
    }
}



