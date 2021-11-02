package com.BlownUp.app.screen.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BlownUp.app.MainApplication;
import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.models.Help;
import com.BlownUp.app.network.API;
import com.BlownUp.app.utils.Utils;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HelpFragment extends BaseFragment {
    private View mView;

    private LinearLayout progressLayout;
    private ProgressBar loader;
    private RecyclerView rcvHelps;
    private Button btnVisit;
    private HelpAdapter mAdapter = null;

    private final ArrayList<Help> helpDescriptionList = new ArrayList<Help>();
    private final ArrayList<Help> helpVideoList = new ArrayList<Help>();

    private static HelpFragment instance = null;

    public static HelpFragment getInstance() {
        if (instance == null) {
            instance = new HelpFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_help, container, false);

        initLayout();

        progressLayout.setVisibility(View.VISIBLE);
        initData();

        return mView;
    }

    private void initLayout() {
        rcvHelps = mView.findViewById(R.id.rcvHelps);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvHelps.setLayoutManager(linearLayoutManager);

        progressLayout = mView.findViewById(R.id.progressLayout);
        progressLayout.setOnClickListener(v -> {
        });
        loader = mView.findViewById(R.id.loader);
        Sprite sprite = new FadingCircle();
        loader.setIndeterminateDrawable(sprite);

        btnVisit = mView.findViewById(R.id.btnVisit);
        btnVisit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Const.APP_LANDING_URL));
            startActivity(intent);
        });
    }

    private void initData() {
        helpDescriptionList.clear();
        helpVideoList.clear();

        String token = MainApplication.getUser(mainInstance).token;
        API.GET(token, Const.HELP_GET_URL, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject json_data = response.getJSONObject("data");
                        JSONArray json_helps = json_data.getJSONArray("help");

                        if (json_helps.length() > 0) {
                            for (int i = 0; i < json_helps.length(); i++) {
                                Help help = (Help) Utils.JSON_STR2OBJECT(json_helps.get(i).toString(), Help.class);
                                if (help.type == 1)
                                    helpDescriptionList.add(help);
                                else
                                    helpVideoList.add(help);
                            }

                            if (mAdapter == null) {
                                mAdapter = new HelpAdapter(mainInstance);
                            }
                            rcvHelps.setAdapter(mAdapter);
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
                progressLayout.setVisibility(View.INVISIBLE);
                anError.printStackTrace();
            }
        });
    }

    class HelpAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;

        public HelpAdapter(Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_help, parent, false);
            return new ItemHelpViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final ItemHelpViewHolder itemHelpViewHolder = ((ItemHelpViewHolder) holder);
            itemHelpViewHolder.drawItem(helpDescriptionList.get(position));
        }

        @Override
        public int getItemCount() {
            return helpDescriptionList.size();
        }

        class ItemHelpViewHolder extends RecyclerView.ViewHolder {
            private final TextView txtDescription;

            ItemHelpViewHolder(View itemView) {
                super(itemView);

                txtDescription = itemView.findViewById(R.id.txtDescription);
            }

            public void drawItem(final Help help) {
                txtDescription.setText(help.content);
            }
        }
    }
}