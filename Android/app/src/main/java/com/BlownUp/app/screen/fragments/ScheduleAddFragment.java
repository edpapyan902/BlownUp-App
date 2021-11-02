package com.BlownUp.app.screen.fragments;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.BlownUp.app.MainApplication;
import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.models.Contact;
import com.BlownUp.app.models.Schedule;
import com.BlownUp.app.network.API;
import com.BlownUp.app.utils.Utils;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScheduleAddFragment extends BaseFragment implements View.OnClickListener {

    private View mView;

    private ScrollView scrollView;
    private EditText edtNumber;
    private ImageView btnContact;
    private CalendarView calendarView;
    private TimePicker timePicker;
    private Button btnSchedule;
    private LinearLayout progressLayout;
    private ProgressBar loader;

    private Contact selectedContact = null;
    private Schedule currentSchedule = null;

    private String selectedDate = "";

    //Contact Dialog
    private AlertDialog alertDialog;
    private MaterialAlertDialogBuilder contactDialogBuilder;
    private View contactDialogView;
    private SwipeRefreshLayout dlg_swipeView;
    private LinearLayout dlg_progressLayout;
    private ProgressBar dlg_loader;
    private RecyclerView dlg_rcvContacts;
    private ContactAdapter mAdapter = null;
    private final ArrayList<Contact> contactArrayList = new ArrayList<Contact>();

    private static ScheduleAddFragment instance = null;

    public static ScheduleAddFragment getInstance() {
        instance = new ScheduleAddFragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_schedule_add, container, false);

        initLayout();
        initData();
        getContactData();

        return mView;
    }

    private void initLayout() {
        scrollView = mView.findViewById(R.id.scrollView);
        scrollView.setSmoothScrollingEnabled(true);
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> edtNumber.clearFocus());
        edtNumber = mView.findViewById(R.id.edtNumber);
        btnContact = mView.findViewById(R.id.btnContact);
        calendarView = mView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> selectedDate = year + "-" + Utils.PLUS0(month + 1) + "-" + Utils.PLUS0(dayOfMonth));
        timePicker = mView.findViewById(R.id.timePicker);
        btnSchedule = mView.findViewById(R.id.btnSchedule);
        btnSchedule.setOnClickListener(this);

        edtNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        PushDownAnim.setPushDownAnimTo(btnContact)
                .setScale(PushDownAnim.MODE_STATIC_DP, 2)
                .setOnClickListener(this);

        progressLayout = mView.findViewById(R.id.progressLayout);
        progressLayout.setOnClickListener(this);
        loader = mView.findViewById(R.id.loader);
        Sprite sprite = new FadingCircle();
        loader.setIndeterminateDrawable(sprite);

        //Contact Dialog
        LayoutInflater inflater = mainInstance.getLayoutInflater();
        contactDialogView = inflater.inflate(R.layout.dialog_view_contact, null, false);
        dlg_swipeView = contactDialogView.findViewById(R.id.dlg_swipeView);
        dlg_swipeView.setOnRefreshListener(() -> getContactData());
        dlg_rcvContacts = contactDialogView.findViewById(R.id.dlg_rcvContacts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        dlg_rcvContacts.setLayoutManager(linearLayoutManager);

        dlg_progressLayout = contactDialogView.findViewById(R.id.dlg_progressLayout);
        dlg_progressLayout.setOnClickListener(this);
        dlg_loader = contactDialogView.findViewById(R.id.dlg_loader);
        dlg_loader.setIndeterminateDrawable(sprite);

        contactDialogBuilder = new MaterialAlertDialogBuilder(mainInstance).setView(contactDialogView);
        alertDialog = contactDialogBuilder.create();
    }

    private void initData() {
        selectedDate = Utils.TIMESTAMP2DATE(calendarView.getDate(), "yyyy-MM-dd");
        currentSchedule = mainInstance.getCurrentSchedule();
        if (currentSchedule != null) {
            if (currentSchedule.contact != null) {
                if (Utils.isValidUSPhone(currentSchedule.contact.number))
                    currentSchedule.contact.number = Utils.formatPhoneNumber(currentSchedule.contact.number);
                edtNumber.setText(currentSchedule.contact.number);
                selectedContact = currentSchedule.contact;
            } else {
                edtNumber.setText(currentSchedule.number);
            }
            calendarView.setDate(Utils.Time2Millisecond(currentSchedule.scheduled_at));

            String[] resultDate = currentSchedule.scheduled_at.split(" ");
            selectedDate = resultDate[0];
            String[] resultTime = resultDate[1].split(":");
            int hour = Integer.valueOf(resultTime[0]);
            int minute = Integer.valueOf(resultTime[1]);
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        }
    }

    private void getContactData() {
        contactArrayList.clear();
        String token = MainApplication.getUser(mainInstance).token;
        API.GET(token, Const.CONTACT_GET_URL, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                dlg_swipeView.setRefreshing(false);
                dlg_progressLayout.setVisibility(View.INVISIBLE);
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
                            dlg_rcvContacts.setAdapter(mAdapter);
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
                dlg_swipeView.setRefreshing(false);
                dlg_progressLayout.setVisibility(View.INVISIBLE);
                anError.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnContact:
                showContact();
                break;
            case R.id.btnSchedule:
                if (currentSchedule == null)
                    addSchedule();
                else
                    updateScheduleCall();
                break;
        }
    }

    private void showContact() {
        if (contactArrayList.size() > 0) {
            ViewGroup parent = (ViewGroup) contactDialogView.getParent();
            if (parent != null) {
                parent.removeAllViews();
                alertDialog = contactDialogBuilder.create();
            }
            alertDialog.show();
        } else {
            showToast("Your contacts is empty.");
        }
    }

    private void addSchedule() {
        String number = edtNumber.getText().toString();
        if (!Utils.isValidUSPhone(number)) {
            showToast("Please input valid phone number.");
            return;
        }

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String scheduled_at = selectedDate + " " + Utils.PLUS0(hour) + ":" + Utils.PLUS0(minute) + ":00";
//        if ((Utils.Date2Millisecond(scheduled_at) - System.currentTimeMillis()) < 5 * 60 * 1000) {
//            showToast(mainInstance.getString(R.string.valid_schedule_date));
//            return;
//        }

        int n_id_contact = 0;
        if (selectedContact != null && selectedContact.number.equals(number)) {
            n_id_contact = selectedContact.id;
            number = "";
        }

        JSONObject params = new JSONObject();
        try {
            params.put("n_id_contact", n_id_contact);
            params.put("number", Utils.formatPhoneNumber(number));
            params.put("scheduled_at", scheduled_at);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edtNumber.clearFocus();

        progressLayout.setVisibility(View.VISIBLE);

        String token = MainApplication.getUser(mainInstance).token;
        API.POST(token, Const.SCHEDULE_ADD_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject json_data = response.getJSONObject("data");
                        JSONObject json_schedule = json_data.getJSONObject("schedule");
                        Schedule schedule = (Schedule) Utils.JSON_STR2OBJECT(json_schedule.toString(), Schedule.class);
                        mainInstance.setAlarmSchedule(schedule);

                        edtNumber.setText("");
                        selectedContact = null;
                        currentSchedule = null;
                        mainInstance.setCurrentSchedule(null);

                        mainInstance.startMainFragment(ScheduleListFragment.getInstance(), Const.SCHEDULE_LIST_FRAGMENT);
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

    private void updateScheduleCall() {
        String number = edtNumber.getText().toString();
        if (!Utils.isValidUSPhone(number)) {
            showToast("Please input valid phone number.");
            return;
        }

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String scheduled_at = selectedDate + " " + Utils.PLUS0(hour) + ":" + Utils.PLUS0(minute) + ":00";
//        if ((Utils.Date2Millisecond(scheduled_at) - System.currentTimeMillis()) < 5 * 60 * 1000) {
//            showToast(mainInstance.getString(R.string.valid_schedule_date));
//            return;
//        }

        int n_id_contact = 0;
        if (selectedContact != null && selectedContact.number.equals(number)) {
            n_id_contact = selectedContact.id;
            number = "";
        }

        JSONObject params = new JSONObject();
        try {
            params.put("id", currentSchedule.id);
            params.put("n_id_contact", n_id_contact);
            params.put("number", Utils.formatPhoneNumber(number));
            params.put("scheduled_at", scheduled_at);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edtNumber.clearFocus();

        progressLayout.setVisibility(View.VISIBLE);

        String token = MainApplication.getUser(mainInstance).token;
        API.POST(token, Const.SCHEDULE_UPDATE_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject json_data = response.getJSONObject("data");
                        JSONObject json_schedule = json_data.getJSONObject("schedule");
                        String old_alarm_identify = json_data.getString("old_alarm_identify");
                        Schedule schedule = (Schedule) Utils.JSON_STR2OBJECT(json_schedule.toString(), Schedule.class);
                        mainInstance.updateAlarmSchedule(schedule, old_alarm_identify);

                        edtNumber.setText("");
                        selectedContact = null;
                        currentSchedule = null;
                        mainInstance.setCurrentSchedule(null);

                        mainInstance.startMainFragment(ScheduleListFragment.getInstance(), Const.SCHEDULE_LIST_FRAGMENT);
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

    class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;

        public ContactAdapter(Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_contact_small, parent, false);
            return new ContactAdapter.ItemContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final ContactAdapter.ItemContactViewHolder itemContactViewHolder = ((ContactAdapter.ItemContactViewHolder) holder);
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
                        selectedContact = contact;
                        edtNumber.setText(contact.number);
                        alertDialog.dismiss();
                        break;
                }
            }
        }
    }
}