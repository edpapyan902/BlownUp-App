package com.BlownUp.app.screen.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.BlownUp.app.models.Schedule;
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
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScheduleListFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private View mView;

    private SwipeRefreshLayout swipeView;
    private LinearLayout progressLayout;
    private ProgressBar loader;
    private RecyclerView rcvSchedules;
    private FloatingActionButton btnAdd;

    private ScheduleAdapter mAdapter = null;

    private final ArrayList<Schedule> scheduleArrayList = new ArrayList<Schedule>();

    private static ScheduleListFragment instance = null;

    public static ScheduleListFragment getInstance() {
        if (instance == null) {
            instance = new ScheduleListFragment();
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
        mView = inflater.inflate(R.layout.fragment_schedule_list, container, false);

        initLayout();

        progressLayout.setVisibility(View.VISIBLE);
        initData();

        return mView;
    }

    private void initLayout() {
        rcvSchedules = mView.findViewById(R.id.rcvSchedules);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvSchedules.setLayoutManager(linearLayoutManager);

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
        scheduleArrayList.clear();

        String token = MainApplication.getUser(mainInstance).token;
        API.GET(token, Const.SCHEDULE_GET_URL, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                swipeView.setRefreshing(false);
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject json_data = response.getJSONObject("data");
                        JSONArray json_schedules = json_data.getJSONArray("schedules");

                        if (json_schedules.length() > 0) {
                            for (int i = 0; i < json_schedules.length(); i++) {
                                Schedule schedule = (Schedule) Utils.JSON_STR2OBJECT(json_schedules.get(i).toString(), Schedule.class);
                                scheduleArrayList.add(schedule);
                            }

                            if (mAdapter == null) {
                                mAdapter = new ScheduleAdapter(mainInstance);
                            }
                            rcvSchedules.setAdapter(mAdapter);

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
                mainInstance.setCurrentSchedule(null);
                mainInstance.startMainFragment(ScheduleAddFragment.getInstance(), Const.SCHEDULE_ADD_FRAGMENT);
                break;
        }
    }

    @Override
    public void onRefresh() {
        initData();
    }

    class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;

        public ScheduleAdapter(Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
            return new ItemScheduleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final ItemScheduleViewHolder itemScheduleViewHolder = ((ItemScheduleViewHolder) holder);
            itemScheduleViewHolder.drawItem(scheduleArrayList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return scheduleArrayList.size();
        }

        class ItemScheduleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView txtName, txtNumber, txtDate, txtTime;
            private final View itemView, itemSchedule, contactLayout;
            private final ImageView imgAvatar;
            private final Button btnDelete;
            private final ProgressBar loader;
            private int position;
            private Schedule schedule;

            ItemScheduleViewHolder(View itemView) {
                super(itemView);

                this.itemView = itemView;

                txtName = itemView.findViewById(R.id.txtName);
                txtNumber = itemView.findViewById(R.id.txtNumber);
                txtDate = itemView.findViewById(R.id.txtDate);
                txtTime = itemView.findViewById(R.id.txtTime);
                imgAvatar = itemView.findViewById(R.id.imgAvatar);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                contactLayout = itemView.findViewById(R.id.contactLayout);
                itemSchedule = itemView.findViewById(R.id.itemSchedule);
                itemSchedule.setOnClickListener(this);

                loader = itemView.findViewById(R.id.loader);
                Sprite sprite = new ThreeBounce();
                loader.setIndeterminateDrawable(sprite);

                PushDownAnim.setPushDownAnimTo(btnDelete)
                        .setScale(PushDownAnim.MODE_STATIC_DP, 2)
                        .setOnClickListener(this);

                Utils.makeMarqueeText(txtName);
                Utils.makeMarqueeText(txtNumber);
            }

            public void drawItem(final Schedule schedule, int position) {
                this.schedule = schedule;
                this.position = position;

                txtDate.setText(Utils.Date2StrDate(schedule.scheduled_at));
                txtTime.setText(Utils.Date2StrTime(schedule.scheduled_at));

                if (schedule.contact == null) {
                    contactLayout.setVisibility(View.GONE);
                    txtNumber.setText(schedule.number);
                } else {
                    contactLayout.setVisibility(View.VISIBLE);

                    txtNumber.setText(schedule.contact.number);
                    txtName.setText(schedule.contact.name);

                    loader.setVisibility(View.VISIBLE);

                    Picasso.get().load(Const.BASE_URL + schedule.contact.avatar).into(imgAvatar, new Callback() {
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
                }

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
                    case R.id.itemSchedule:
                        mainInstance.setCurrentSchedule(schedule);
                        mainInstance.startMainFragment(ScheduleAddFragment.getInstance(), Const.SCHEDULE_ADD_FRAGMENT);
                        break;
                    case R.id.btnDelete:
                        deleteSchedule();
                        break;
                }
            }

            private void deleteSchedule() {
                String token = MainApplication.getUser(mainInstance).token;

                JSONObject json_param = new JSONObject();
                try {
                    json_param.put("id", schedule.id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                API.POST(token, Const.SCHEDULE_DELETE_URL, json_param, new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                progressLayout.setVisibility(View.VISIBLE);
                                mainInstance.cancelAlarmSchedule(schedule);
                                initData();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
            }
        }
    }
}