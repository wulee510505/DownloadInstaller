package com.zenglb.framework.updateinstaller;

import android.Manifest;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.zenglb.downloadinstaller.DownloadInstaller;
import com.zenglb.downloadinstaller.DownloadProgressCallBack;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * app 内部升级
 *
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    //URL 下载有时间效益.自己替换可以正常下载的地址
    private String apkDownLoadUrl= "https://dldir1.qq.com/weixin/android/weixin7020android1780_arm64.apk";


    //这是一个无效的下载网址，你可以改为你自己的下载地址
    private String apkDownLoadUrl2 = "https://api.developer.xiaomi.com/autoupdate/updateself/download/fc6b8eba5351dedf2a79dab71c9bb299edcd1fb85AppStore_06af5546730ca4df0191ab263a2ae82b/com.engineer.map_1038.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 测试升级安装
         */
        findViewById(R.id.update).setOnClickListener(v -> showUpdateDialog("1.升级后App 会自动攒钱\n2.还可以做白日梦", true, apkDownLoadUrl));

        /**
         * 测试升级安装2,无效下载链接
         */
        findViewById(R.id.update2).setOnClickListener(v -> showUpdateDialog("1.升级后App 会自动攒钱\n2.还可以做白日梦", false, apkDownLoadUrl2));

        methodRequiresPermission();

    }


    /**
     * 显示下载的对话框,是否要强制的升级还是正常的升级
     *
     * @param UpdateMsg     升级信息
     * @param isForceUpdate 是否是强制升级
     * @param downloadUrl   APK 下载URL
     */
    private void showUpdateDialog(String UpdateMsg, boolean isForceUpdate, String downloadUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View updateView = inflater.inflate(R.layout.update_layout, null);
        NumberProgressBar progressBar = updateView.findViewById(R.id.tips_progress);
        TextView updateMsg = updateView.findViewById(R.id.update_mess_txt);
        updateMsg.setText(UpdateMsg);
        builder.setTitle("发现新版本");
        String negativeBtnStr = "以后再说";

        if (isForceUpdate) {
            builder.setTitle("强制升级");
            negativeBtnStr = "退出应用";
        }

        builder.setView(updateView);
        builder.setNegativeButton(negativeBtnStr, null);
        builder.setPositiveButton(R.string.apk_update_yes, null);

        AlertDialog downloadDialog = builder.create();
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.setCancelable(false);
        downloadDialog.show();

        downloadDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (isForceUpdate) {
                progressBar.setVisibility(View.VISIBLE);

                //新加 isForceGrantUnKnowSource 参数

                //如果是企业内部应用升级，肯定是要这个权限，其他情况不要太流氓，TOAST 提示
                //这里演示要强制安装
                new DownloadInstaller(this, downloadUrl, true, new DownloadProgressCallBack() {
                    @Override
                    public void downloadProgress(int progress) {
                        runOnUiThread(() -> progressBar.setProgress(progress));
                        if (progress==100){
                            downloadDialog.dismiss();
                        }
                    }

                    @Override
                    public void downloadException(Exception e) {
                    }

                    @Override
                    public void onInstallStart() {
                        downloadDialog.dismiss();
                    }
                }).start();

                //升级按钮变灰色
                downloadDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GRAY);

            } else {
                new DownloadInstaller(this, downloadUrl).start();
                downloadDialog.dismiss();
            }
        });

        downloadDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
            if (isForceUpdate) {
                downloadDialog.dismiss();
                MainActivity.this.finish();
            } else {
                downloadDialog.dismiss();
            }
        });

    }


    /**
     * 请求权限,创建目录的权限
     */
    private void methodRequiresPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(MainActivity.this, "App 升级需要储存权限", 10086, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
    }


}
