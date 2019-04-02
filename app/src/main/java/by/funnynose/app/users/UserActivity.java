package by.funnynose.app.users;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.funnynose.R;
import by.funnynose.app.User;
import by.funnynose.app.Utilities;
import by.funnynose.app.authentication.AuthenticationActivity;
import by.funnynose.app.constants.Permission;
import by.funnynose.app.network.SocketAPI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {

    private static final String KEY_USER = "user";

    private TextView mUserLogin;
    private TextView mUserPhone;
    private TextView mUserTextStatus;
    private TextView mUserStatus;
    private Button mUserStatusDown;
    private Button mUserStatusUp;
    private Button mUserDeleteAccount;
    private Button mUserButtonExit;
    private ImageView mUserPhoto;

    private UserProfile openedUser;

    public static Intent newIntent(Context context, UserProfile user) {
        Intent intent = new Intent(context, UserActivity.class);
        if (user != null) {
            intent.putExtra(KEY_USER, user);
        }
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actvity_user);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mUserLogin = findViewById(R.id.user_profile_login);
        mUserPhone = findViewById(R.id.user_profile_phone);
        mUserTextStatus = findViewById(R.id.user_profile_text_status);
        mUserStatus = findViewById(R.id.user_profile_status);

        Bundle args = getIntent().getExtras();
        openedUser = (UserProfile) args.getSerializable(KEY_USER);

        //mUserLogin.setText(openedUser.login);
        //mUserPhone.setText(openedUser.phone);

        mUserStatusDown = findViewById(R.id.user_profile_btn_down_status);
        mUserStatusUp = findViewById(R.id.user_profile_btn_up_status);

        mUserDeleteAccount = findViewById(R.id.user_profile_btn_delete_account);
        mUserDeleteAccount.setVisibility(View.GONE);

        mUserButtonExit = findViewById(R.id.user_profile_btn_exit);
        mUserButtonExit.setVisibility(View.GONE);

        mUserPhoto = findViewById(R.id.user_profile_photo);

        //mUserStatusDown.setOnClickListener(v -> { onClickButtonDown(); });
        //mUserStatusUp.setOnClickListener(v -> { onClickButtonUp(); });
        //mUserButtonExit.setOnClickListener(v -> { onClickButtonExit(); });
        //mUserDeleteAccount.setOnClickListener(v -> { onClickButtonDeleteAccount(); });

        /*
        if (Session.currentUser().phone.equals(openedUser.phone)) {
            mUserPhoto.setClickable(true);
            mUserPhoto.setOnClickListener(v -> { onClickPhoto(); });
        }

        if (Session.imageSignatureCache.imageSignatureMap.containsKey(openedUser.phone)) {
            UserImageLoader.setIfExists(mUserPhoto, openedUser.phone);
        }

        if (Session.currentOnline()) {
            UserImageLoader.setNewAndCacheAsync(mUserPhoto, openedUser.phone);
        }
        */

        initUIComponents();
    }

    private void onClickPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    private void onClickButtonDeleteAccount() {
        if (SocketAPI.isOnline()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Удаление аккаунта");
            alertDialog.setMessage("Вы точно хотите удалить этот аккаунт?");

            alertDialog.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    // удаление с сервера
                    finish();
                }
            });
            alertDialog.setNegativeButton("Отмена", null);
            alertDialog.show();
        } else {
            Utilities.showSnackbar(this.getCurrentFocus(), "Нет доступа к интернету", true);
        }
    }

    private void onClickButtonExit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Выход");
        alertDialog.setMessage("Вы точно хотите выйти из аккаунта?");

        alertDialog.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                moveToAuthenticationActivity();
            }
        });
        alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {}
        });

        alertDialog.show();
    }

    private void moveToAuthenticationActivity() {
        User.removeUserAppData(this);
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        //Session.closeSession();
        finish();
    }

    private void onClickButtonDown() {
        if (SocketAPI.isOnline()) {
            /*
            if (Session.currentUser().status > openedUser.status) {
                if (openedUser.status > Permission.LOSER) {
                    Map<String, Object> mapStatus = new HashMap<>();
                    mapStatus.put("status", openedUser.status - 1);
                    dref.updateChildren(mapStatus, (databaseError, databaseReference) -> {
                        if (databaseError == null) {
                            openedUser.status = openedUser.status - 1;
                            setStatus();
                        }
                    });
                } else {
                    Utilities.showSnackbar(this.getCurrentFocus(), "Понизить статус нельзя!", true);
                }
            } else {
                Utilities.showSnackbar(this.getCurrentFocus(), "Понизить статус нельзя!", true);
            }*/
        } else {
            Utilities.showSnackbar(this.getCurrentFocus(), "Нет доступа к интернету", true);
        }
    }

    private void onClickButtonUp() {
        if (SocketAPI.isOnline()) {
            /*
            if (Session.currentUser().status > openedUser.status) {
                if (openedUser.status < Permission.DEVELOPER) {
                    Map<String, Object> mapStatus = new HashMap<>();
                    mapStatus.put("status", openedUser.status + 1);
                    dref.updateChildren(mapStatus, (databaseError, databaseReference) -> {
                        if (databaseError == null) {
                            openedUser.status = openedUser.status + 1;
                            setStatus();
                        }
                    });
                } else {
                    Utilities.showSnackbar(this.getCurrentFocus(), "Понизить статус нельзя!", true);
                }
            } else {
                Utilities.showSnackbar(this.getCurrentFocus(), "Понизить статус нельзя!", true);
            }*/
        } else {
            Utilities.showSnackbar(this.getCurrentFocus(), "Нет доступа к интернету", true);
        }
    }


    private void initUIComponents() {
        mUserStatus.setText(Permission.toString(Permission.LOSER));
        /*
        if (Session.currentUser().status <= Permission.MODERATOR && !Session.currentUser().phone.equals(openedUser.phone)) {
            if (Session.currentUser().status == Permission.LOSER) {
                mUserTextStatus.setVisibility(View.GONE);
                mUserStatus.setVisibility(View.GONE);
            }
            mUserStatusDown.setVisibility(View.GONE);
            mUserStatusUp.setVisibility(View.GONE);
        } else if (Session.currentUser().phone.equals(openedUser.phone)) {
            mUserStatusDown.setVisibility(View.GONE);
            mUserStatusUp.setVisibility(View.GONE);
            mUserButtonExit.setVisibility(View.VISIBLE);
        } else if (Session.currentUser().status >= Permission.ADMIN) {
            mUserDeleteAccount.setVisibility(View.VISIBLE);
        }*/
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}



/*
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK && Session.currentOnline()){
                    Uri selectedImageUri = imageReturnedIntent.getData();
                    GlideApp.with(this)
                            .load(selectedImageUri)
                            .circleCrop()
                            .override(768, 768)
                            .into(new Target<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    Bitmap bitmap = drawableToBitmap(resource);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                    byte[] data = baos.toByteArray();

                                    GlideApp.with(Session.context)
                                            .load(ref)
                                            .placeholder(R.drawable.anon)
                                            .error(R.drawable.anon)
                                            .circleCrop()
                                            .signature(new ObjectKey(String.valueOf(storageMetadata.getUpdatedTimeMillis())))
                                            .into(mUserPhoto);
                                }
                                @Override
                                public void onLoadStarted(@Nullable Drawable placeholder) {}
                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {}
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {}
                                @Override
                                public void getSize(@NonNull SizeReadyCallback cb) {}
                                @Override
                                public void removeCallback(@NonNull SizeReadyCallback cb) {}
                                @Override
                                public void setRequest(@Nullable Request request) {}
                                @Nullable
                                @Override
                                public Request getRequest() {
                                    return null;
                                }
                                @Override
                                public void onStart() {}
                                @Override
                                public void onStop() {}
                                @Override
                                public void onDestroy() {}
                            });
                } else if (resultCode == RESULT_OK){
                    Toast.makeText(Session.context, "Не удалось сменить изображение!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
 */