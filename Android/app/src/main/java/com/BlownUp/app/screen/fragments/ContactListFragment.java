package com.BlownUp.app.screen.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactListFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private View mView;

    private SwipeRefreshLayout swipeView;
    private LinearLayout progressLayout;
    private ProgressBar loader;
    private RecyclerView rcvContacts;
    private ContactAdapter mAdapter = null;
    private FloatingActionButton btnAdd;

    private final ArrayList<Contact> contactArrayList = new ArrayList<Contact>();

    private static ContactListFragment instance = null;

    public static ContactListFragment getInstance() {
        if (instance == null) {
            instance = new ContactListFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_contact_list, container, false);

        initLayout();

        progressLayout.setVisibility(View.VISIBLE);
        initData();

        return mView;
    }

    private void initLayout() {
        rcvContacts = mView.findViewById(R.id.rcvContacts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvContacts.setLayoutManager(linearLayoutManager);

        progressLayout = mView.findViewById(R.id.progressLayout);
        progressLayout.setOnClickListener(this);
        loader = mView.findViewById(R.id.loader);
        Sprite sprite = new FadingCircle();
        loader.setIndeterminateDrawable(sprite);

        btnAdd = mView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        swipeView = mView.findViewById(R.id.swipeView);
        swipeView.setOnRefreshListener(this);
    }

    private void initData() {
        contactArrayList.clear();
        String token = MainApplication.getUser(mainInstance).token;
        API.GET(token, Const.CONTACT_GET_URL, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                swipeView.setRefreshing(false);
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject json_data = response.getJSONObject("data");
                        JSONArray json_contacts = json_data.getJSONArray("contacts");

                        if (json_contacts.length() > 0) {
                            for (int i = 0; i < json_contacts.length(); i++) {
                                Contact contact = (Contact) Utils.JSON_STR2OBJECT(json_contacts.get(i).toString(), Contact.class);
                                contactArrayList.add(contact);
                            }

                            if (mAdapter == null) {
                                mAdapter = new ContactAdapter(mainInstance);
                            }
                            rcvContacts.setAdapter(mAdapter);
                        } else {
                            if (mAdapter != null)
                                mAdapter.notifyDataSetChanged();
                        }
                    }

                    String message = response.getString("message");
                    if (!TextUtils.isEmpty(message))
                        showToast(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                swipeView.setRefreshing(false);
                progressLayout.setVisibility(View.INVISIBLE);
                anError.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                mainInstance.setCurrentContact(null);
                mainInstance.startMainFragment(ContactAddFragment.getInstance(), Const.CONTACT_ADD_FRAGMENT);
                break;
        }
    }

    @Override
    public void onRefresh() {
        initData();
    }

    class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;

        public ContactAdapter(Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
            return new ItemContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final ItemContactViewHolder itemContactViewHolder = ((ItemContactViewHolder) holder);
            itemContactViewHolder.drawItem(contactArrayList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return contactArrayList.size();
        }

        class ItemContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView txtName, txtNumber;
            private final View itemView, itemContact;
            private final ImageView imgAvatar;
            private final ProgressBar loader;
            private int position;
            private Contact contact;

            ItemContactViewHolder(View itemView) {
                super(itemView);

                this.itemView = itemView;

                txtName = itemView.findViewById(R.id.txtName);
                txtNumber = itemView.findViewById(R.id.txtNumber);
                imgAvatar = itemView.findViewById(R.id.imgAvatar);
                itemContact = itemView.findViewById(R.id.itemContact);
                itemContact.setOnClickListener(this);

                loader = itemView.findViewById(R.id.loader);
                Sprite sprite = new ThreeBounce();
                loader.setIndeterminateDrawable(sprite);

                Utils.makeMarqueeText(txtName);
                Utils.makeMarqueeText(txtNumber);
            }

            public void drawItem(final Contact contact, int position) {
                this.contact = contact;
                this.position = position;

                txtName.setText(contact.name);
                txtNumber.setText(contact.number);

                loader.setVisibility(View.VISIBLE);

                Picasso.get().load(Const.BASE_URL + contact.avatar).into(imgAvatar, new Callback() {
                    @Override
                    public void onSuccess() {
                        loader.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        loader.setVisibility(View.INVISIBLE);
                        imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                        e.printStackTrace();
                    }
                });

                setAnimation();
            }

            private void setAnimation() {
                this.itemView.setAlpha(0.0f);
                this.itemView.animate().alpha(1.0f)
                        .setDuration(500)
                        .setStartDelay(this.position * 50)
                        .start();
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.itemContact:
                        mainInstance.setCurrentContact(contact);
                        mainInstance.startMainFragment(ContactAddFragment.getInstance(), Const.CONTACT_ADD_FRAGMENT);
                        break;
                }
            }
        }
    }
}