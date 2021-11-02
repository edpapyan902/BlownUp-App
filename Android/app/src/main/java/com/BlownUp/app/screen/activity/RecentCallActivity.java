package com.BlownUp.app.screen.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.BlownUp.app.BaseActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecentCallActivity extends BaseActivity implements View.OnClickListener {

    private SwipeRefreshLayout swipeView;
    private ProgressBar loader;
    private RecyclerView rcvRecentCalls;
    private Button btnScheduleCall, btnMyCallSchedule, btnMyAccount;
    private ScheduleAdapter mAdapter = null;
    private final ArrayList<Schedule> mSchedules = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_call);

        initLayout();

        loader.setVisibility(View.VISIBLE);
        initData();
    }

    private void initLayout() {
        rcvRecentCalls = findViewById(R.id.rcvRecentCalls);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvRecentCalls.setLayoutManager(linearLayoutManager);

        loader = findViewById(R.id.loader);
        Sprite sprite = new FadingCircle();
        loader.setIndeterminateDrawable(sprite);

        swipeView = findViewById(R.id.swipeView);
        swipeView.setOnRefreshListener(() -> initData());

        btnScheduleCall = findViewById(R.id.btnScheduleCall);
        btnScheduleCall.setOnClickListener(this);
        btnMyCallSchedule = findViewById(R.id.btnMyCallSchedule);
        btnMyCallSchedule.setOnClickListener(this);
        btnMyAccount = findViewById(R.id.btnMyAccount);
        btnMyAccount.setOnClickListener(this);
    }

    private void initData() {
        mSchedules.clear();

        String token = MainApplication.getUser(this).token;
        API.GET(token, Const.SCHEDULE_GET_URL, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                swipeView.setRefreshing(false);
                loader.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject json_data = response.getJSONObject("data");
                        JSONArray json_schedules = json_data.getJSONArray("schedules");

                        for (int j = 0; j < json_schedules.length(); j++) {
                            Schedule schedule = (Schedule) Utils.JSON_STR2OBJECT(json_schedules.get(j).toString(), Schedule.class);
                            mSchedules.add(schedule);
                        }

                        if (mAdapter == null) {
                            mAdapter = new ScheduleAdapter(RecentCallActivity.this);
                        }
                        rcvRecentCalls.setAdapter(mAdapter);
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
                loader.setVisibility(View.INVISIBLE);
                anError.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnScheduleCall:
                goMain(0);
                break;
            case R.id.btnMyCallSchedule:
                goMain(1);
                break;
            case R.id.btnMyAccount:
                goMain(2);
                break;
        }
    }

    private void goMain(int type) {
        Intent intent = new Intent(RecentCallActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.INIT_FRAGMENT_TYPE, type);
        startActivity(intent);
        finish();
    }

    class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;

        public ScheduleAdapter(Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recent_calls, parent, false);
            return new ScheduleAdapter.ItemScheduleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final ScheduleAdapter.ItemScheduleViewHolder itemScheduleViewHolder = ((ScheduleAdapter.ItemScheduleViewHolder) holder);
            itemScheduleViewHolder.drawItem(mSchedules.get(position));
        }

        @Override
        public int getItemCount() {
            return mSchedules.size();
        }

        class ItemScheduleViewHolder extends RecyclerView.ViewHolder {
            private final TextView txtNumber, txtDate, txtTime;

            ItemScheduleViewHolder(View itemView) {
                super(itemView);

                txtNumber = itemView.findViewById(R.id.txtNumber);
                txtDate = itemView.findViewById(R.id.txtDate);
                txtTime = itemView.findViewById(R.id.txtTime);

                Utils.makeMarqueeText(txtNumber);
            }

            public void drawItem(final Schedule schedule) {
                txtDate.setText(Utils.Date2StrDate(schedule.scheduled_at));
                txtTime.setText(Utils.Date2StrTime(schedule.scheduled_at));

                if (schedule.contact == null) {
                    txtNumber.setText(schedule.number);
                } else {
                    txtNumber.setText(schedule.contact.number);
                }
            }
        }
    }
}