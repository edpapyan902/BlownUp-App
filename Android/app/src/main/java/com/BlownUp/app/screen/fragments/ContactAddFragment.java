package com.BlownUp.app.screen.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.BlownUp.app.MainApplication;
import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.models.Contact;
import com.BlownUp.app.network.API;
import com.BlownUp.app.utils.Utils;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ContactAddFragment extends BaseFragment implements View.OnClickListener {

    private final static int REQ_CODE_SELECT_IMAGE = 1011;
    private final static int REQ_CODE_SELECT_CONTACT = 1012;

    private View mView;

    private ScrollView scrollView;
    private TextView txtTitle;
    private ImageView imgAvatar, imgContact;
    private EditText edtName, edtNumber;
    private Button btnSave, btnDelete;
    private LinearLayout progressLayout;
    private ProgressBar loader;

    private Contact currentContact = null;

    private String avatarBase64 = "";

    private static ContactAddFragment instance = null;

    public static ContactAddFragment getInstance() {
        instance = new ContactAddFragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_contact_add, container, false);

        initLayout();
        initData();

        return mView;
    }

    private void initLayout() {
        scrollView = mView.findViewById(R.id.scrollView);
        scrollView.setSmoothScrollingEnabled(true);
        imgAvatar = mView.findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(this);
        imgContact = mView.findViewById(R.id.imgContact);
        edtName = mView.findViewById(R.id.edtName);
        edtNumber = mView.findViewById(R.id.edtNumber);
        btnDelete = mView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
        btnSave = mView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        txtTitle = mView.findViewById(R.id.txtTitle);

        edtNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        PushDownAnim.setPushDownAnimTo(imgContact)
                .setScale(PushDownAnim.MODE_STATIC_DP, 3)
                .setOnClickListener(this);

        progressLayout = mView.findViewById(R.id.progressLayout);
        progressLayout.setOnClickListener(this);
        loader = mView.findViewById(R.id.loader);
        Sprite sprite = new FadingCircle();
        loader.setIndeterminateDrawable(sprite);
    }

    private void initData() {
        avatarBase64 = "";
        currentContact = mainInstance.getCurrentContact();
        if (currentContact != null) {
            btnDelete.setVisibility(View.VISIBLE);
            btnSave.setText(R.string.update_contact);
            txtTitle.setText(R.string.update_a_contact);
            edtName.setText(currentContact.name);
            edtNumber.setText(currentContact.number);
            Picasso.get().load(Const.BASE_URL + currentContact.avatar).into(imgAvatar, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgAvatar:
                pickImage();
                break;
            case R.id.btnSave:
                if (currentContact == null)
                    save();
                else
                    update();
                break;
            case R.id.btnDelete:
                delete();
                break;
            case R.id.imgContact:
                pickContact();
                break;
        }
    }

    private void save() {
        String name = edtName.getText().toString();
        String number = edtNumber.getText().toString();

        if (!Utils.isValidUSPhone(number)) {
            showToast("Please input valid phone number.");
            return;
        }

        if (TextUtils.isEmpty(name)) {
            showToast("Please input name.");
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put("name", name);
            params.put("number", Utils.formatPhoneNumber(number));
            params.put("avatar", avatarBase64);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edtName.clearFocus();
        edtNumber.clearFocus();

        progressLayout.setVisibility(View.VISIBLE);

        String token = MainApplication.getUser(mainInstance).token;
        API.POST(token, Const.CONTACT_ADD_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        edtName.setText("");
                        edtNumber.setText("");
                        imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                        avatarBase64 = "";
                    }

                    String message = response.getString("message");
                    if (!TextUtils.isEmpty(message))
                        showToast(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError error) {
                progressLayout.setVisibility(View.INVISIBLE);
                error.printStackTrace();
            }
        });
    }

    private void update() {
        String name = edtName.getText().toString();
        String number = edtNumber.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showToast("Please input name");
            return;
        }
        if (TextUtils.isEmpty(number)) {
            showToast("Please input number");
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put("id", currentContact.id);
            params.put("name", name);
            params.put("number", number);
            params.put("avatar", avatarBase64);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edtName.clearFocus();
        edtNumber.clearFocus();

        progressLayout.setVisibility(View.VISIBLE);

        String token = MainApplication.getUser(mainInstance).token;
        API.POST(token, Const.CONTACT_UPDATE_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        edtName.setText("");
                        edtNumber.setText("");
                        imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                        avatarBase64 = "";
                        txtTitle.setText(R.string.add_a_contact);
                        btnSave.setText(R.string.save_contact);
                        btnDelete.setVisibility(View.GONE);

                        currentContact = null;
                        mainInstance.setCurrentContact(null);
                    }

                    String message = response.getString("message");
                    if (!TextUtils.isEmpty(message))
                        showToast(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError error) {
                progressLayout.setVisibility(View.INVISIBLE);
                error.printStackTrace();
            }
        });
    }

    private void delete() {
        JSONObject params = new JSONObject();
        try {
            params.put("id", currentContact.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressLayout.setVisibility(View.VISIBLE);

        String token = MainApplication.getUser(mainInstance).token;
        API.POST(token, Const.CONTACT_DELETE_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        edtName.setText("");
                        edtNumber.setText("");
                        imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                        avatarBase64 = "";
                        txtTitle.setText(R.string.add_a_contact);
                        btnSave.setText(R.string.save_contact);
                        btnDelete.setVisibility(View.GONE);

                        currentContact = null;
                        mainInstance.setCurrentContact(null);
                    }

                    String message = response.getString("message");
                    if (!TextUtils.isEmpty(message))
                        showToast(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError error) {
                progressLayout.setVisibility(View.INVISIBLE);
                error.printStackTrace();
            }
        });
    }

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_CONTACT);
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), REQ_CODE_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE_SELECT_IMAGE && intent.getData() != null) {
            try {
                Uri uri = intent.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mainInstance.getContentResolver(), uri);
                imgAvatar.setImageBitmap(bitmap);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] byteArray = outputStream.toByteArray();

                avatarBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE_SELECT_CONTACT && intent.getData() != null) {
            Uri contactData = intent.getData();
            Cursor c = mainInstance.getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));
                String photo = c.getString(c.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));
                getContactPhoto(photo);

                edtName.setText(name);
                if (hasPhone.equalsIgnoreCase("1")) {
                    String phone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (Utils.isValidUSPhone(phone))
                        edtNumber.setText(Utils.formatPhoneNumber(phone));
                }
            }
        }
    }

    private void getContactPhoto(String photo) {
        if (TextUtils.isEmpty(photo))
            return;

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mainInstance.getContentResolver(), Uri.parse(photo));
            imgAvatar.setImageBitmap(bitmap);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            avatarBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}