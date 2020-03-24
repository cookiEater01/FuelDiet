package com.fueldiet.fueldiet.adapter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.fueldiet.fueldiet.object.ReminderObject;
import com.fueldiet.fueldiet.object.VehicleObject;
import com.fueldiet.fueldiet.R;
import com.fueldiet.fueldiet.db.FuelDietDBHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Adapter for Reminder Recycler View
 */
public class ReminderMultipleTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ReminderMultipleTypeAdapter.OnItemClickListener mListener;
    private Context mContext;
    private List<ReminderObject> reminderList;
    private final static int TYPE_ACTIVE = 0;
    private final static int TYPE_DONE = 1;
    private final static int TYPE_DIVIDER = 2;
    private final static int TYPE_REPEAT = 3;

    public ReminderMultipleTypeAdapter(Context context, List<ReminderObject> reminderObjectList) {
        mContext = context;
        reminderList = reminderObjectList;
    }

    public interface OnItemClickListener {
        void onEditClick(int position, int element_id);
        void onDeleteClick(int position, int element_id);
        void onDoneClick(int position, int element_id);
    }

    public void setOnItemClickListener(ReminderMultipleTypeAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Checks what type of reminder it is
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ACTIVE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_template_reminder, parent, false);
            return new ActiveViewHolder(v, mListener);
        } else if (viewType == TYPE_DONE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_template_reminder_done, parent, false);
            return new DoneViewHolder(v, mListener);
        } else if (viewType == TYPE_REPEAT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_template_repeat_reminder, parent, false);
            return new RepeatViewHolder(v, mListener);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_template_type_title, parent, false);
            return new DividerViewHolder(v, mListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (reminderList.get(position).getRepeat() != 0)
            return TYPE_REPEAT;
        else if (reminderList.get(position).getId() < 0)
            return TYPE_DIVIDER;
        else if (reminderList.get(position).isActive())
            return TYPE_ACTIVE;
        else
            return TYPE_DONE;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ACTIVE)
            ((ActiveViewHolder) holder).setActiveDetails(reminderList.get(position), position);
        else if (getItemViewType(position) == TYPE_DONE)
            ((DoneViewHolder) holder).setDoneDetails(reminderList.get(position), position);
        else if (getItemViewType(position) == TYPE_REPEAT)
            ((RepeatViewHolder) holder).setRepeatDetails(reminderList.get(position), position);
        else
            ((DividerViewHolder) holder).setDivider(reminderList.get(position));
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    class ActiveViewHolder extends RecyclerView.ViewHolder {
        TextView when;
        ImageView whenImg;
        ImageView more;
        TextView title;
        TextView desc;

        View divider;
        ImageView descImg;

        ActiveViewHolder(final View itemView, final ReminderMultipleTypeAdapter.OnItemClickListener listener) {
            super(itemView);
            when = itemView.findViewById(R.id.reminder_when_template);
            more = itemView.findViewById(R.id.reminder_more);
            whenImg = itemView.findViewById(R.id.reminder_when_img);
            //whenImg.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.red)));
            title = itemView.findViewById(R.id.reminder_title_template);
            desc = itemView.findViewById(R.id.reminder_desc_template);

            descImg = itemView.findViewById(R.id.reminder_details_img);
            divider = itemView.findViewById(R.id.reminder_break_template);
        }

        void setActiveDetails(ReminderObject ro, int position) {
            Calendar calendar = Calendar.getInstance();
            Integer km = ro.getKm();
            FuelDietDBHelper dbHelper = new FuelDietDBHelper(mContext);

            /* popup menu */
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(mContext, more);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.reminder_card_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.mark_as_done:
                                    mListener.onDoneClick(position, ro.getId());
                                    return true;
                                case R.id.edit:
                                    mListener.onEditClick(position, ro.getId());
                                    return true;
                                case R.id.delete:
                                    mListener.onDeleteClick(position, ro.getId());
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });

            if (km == null) {
                final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                //whenImg.setImageResource(R.drawable.ic_today_black_24dp);
                Date date = ro.getDate();
                when.setText(sdf.format(date));

                if (calendar.getTimeInMillis() >= date.getTime()) {
                    when.setTextColor(mContext.getColor(R.color.red));
                    whenImg.setColorFilter(mContext.getColor(R.color.redDark));
                } else {
                    when.setTextColor(mContext.getColor(R.color.colorPrimary));
                    whenImg.setColorFilter(mContext.getColor(R.color.colorPrimaryDark));
                }

            } else {
                //whenImg.setImageResource(R.drawable.ic_timeline_black_24dp);
                when.setText(ro.getKm()+"km");

                VehicleObject vehicleObject = dbHelper.getVehicle(ro.getCarID());

                int maxOdo = Math.max(vehicleObject.getOdoCostKm(), vehicleObject.getOdoFuelKm());
                maxOdo = Math.max(maxOdo, vehicleObject.getOdoRemindKm());

                if (maxOdo >= ro.getKm()) {
                    when.setTextColor(mContext.getColor(R.color.red));
                    whenImg.setColorFilter(mContext.getColor(R.color.redDark));
                }  else {
                    when.setTextColor(mContext.getColor(R.color.colorPrimary));
                    whenImg.setColorFilter(mContext.getColor(R.color.colorPrimaryDark));
                }
            }

            String titleString = ro.getTitle();
            String descString = ro.getDesc();
            int id = ro.getId();

            if (descString == null || descString.equals("")) {
                desc.setVisibility(View.GONE);
                descImg.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
            } else {
                desc.setVisibility(View.VISIBLE);
                descImg.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                desc.setText(descString);
            }
            title.setText(titleString);
            itemView.setTag(id);
        }
    }

    class DoneViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        ImageView dateImg;
        TextView km;
        TextView title;
        TextView desc;

        View divider;
        ImageView descImg;

        ImageView more;


        DoneViewHolder(final View itemView, final ReminderMultipleTypeAdapter.OnItemClickListener listener) {
            super(itemView);
            date = itemView.findViewById(R.id.reminder_done_date_template);
            dateImg = itemView.findViewById(R.id.reminder_done_calendar_img);
            km = itemView.findViewById(R.id.reminder_done_km_template);
            title = itemView.findViewById(R.id.reminder_done_title_template);
            desc = itemView.findViewById(R.id.reminder_done_desc_template);

            descImg = itemView.findViewById(R.id.reminder_done_details_img);
            divider = itemView.findViewById(R.id.reminder_done_break_template);

            more = itemView.findViewById(R.id.reminder_more);
        }

        void setDoneDetails(ReminderObject ro, int position) {
            final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date dateF = ro.getDate();
            date.setText(sdf.format(dateF));
            if (ro.getKm() == null)
                km.setText("No km yet");
            else
                km.setText(ro.getKm()+" km");

            String titleS = ro.getTitle();
            String descS = ro.getDesc();
            int id = ro.getId();

            if (descS == null || descS.equals("")) {
                desc.setVisibility(View.GONE);
                descImg.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
            } else {
                desc.setVisibility(View.VISIBLE);
                descImg.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                desc.setText(descS);
            }
            title.setText(titleS);
            itemView.setTag(id);
            /* popup menu */
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(mContext, more);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.reminder_card_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.mark_as_done:
                                    mListener.onDoneClick(position, ro.getId());
                                    return true;
                                case R.id.edit:
                                    mListener.onEditClick(position, ro.getId());
                                    return true;
                                case R.id.delete:
                                    mListener.onDeleteClick(position, ro.getId());
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });
        }
    }

    class DividerViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView logo;

        DividerViewHolder(final View itemView, final ReminderMultipleTypeAdapter.OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.type_title);
            logo = itemView.findViewById(R.id.type_image);
        }

        void setDivider(ReminderObject ro) {
            if (ro.getId() == -20) {
                title.setText(R.string.vehicle_reminder_active);
                logo.setImageResource(R.drawable.ic_notifications_none_black_24dp);
            } else {
                title.setText(R.string.vehicle_reminder_prev);
                logo.setImageResource(R.drawable.ic_notifications_off_black_24dp);
            }
        }
    }

    class RepeatViewHolder extends RecyclerView.ViewHolder {

        TextView when;
        ImageView whenImg;
        ImageView more;
        TextView title;
        TextView desc;
        TextView repeat;
        TextView nextRepeat;

        View divider;
        ImageView descImg;

        public RepeatViewHolder(final View itemView, final OnItemClickListener listener) {
            super(itemView);
            when = itemView.findViewById(R.id.reminder_when_template);
            more = itemView.findViewById(R.id.reminder_more);
            whenImg = itemView.findViewById(R.id.reminder_when_img);
            title = itemView.findViewById(R.id.reminder_title_template);
            desc = itemView.findViewById(R.id.reminder_desc_template);
            descImg = itemView.findViewById(R.id.reminder_details_img);
            divider = itemView.findViewById(R.id.reminder_break_template);
            repeat = itemView.findViewById(R.id.reminder_repeat_every);
            nextRepeat = itemView.findViewById(R.id.reminder_next_repeat);
        }

        void setRepeatDetails(ReminderObject ro, int position) {
            Calendar calendar = Calendar.getInstance();
            Integer km = ro.getKm();
            FuelDietDBHelper dbHelper = new FuelDietDBHelper(mContext);

            /* popup menu */
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(mContext, more);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.reminder_card_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.mark_as_done:
                                    mListener.onDoneClick(position, ro.getId());
                                    return true;
                                case R.id.edit:
                                    mListener.onEditClick(position, ro.getId());
                                    return true;
                                case R.id.delete:
                                    mListener.onDeleteClick(position, ro.getId());
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });

            String rpt = mContext.getString(R.string.repeat_every_x);
            String at = mContext.getString(R.string.at);
            if (km == null) {
                final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                final SimpleDateFormat sdfShort = new SimpleDateFormat("dd.MM.");
                int days = ro.getRepeat();
                Date date = ro.getDate();
                Calendar c = Calendar.getInstance();
                c.setTime(date);

                when.setText(sdf.format(date));
                Calendar today = Calendar.getInstance();

                while (c.before(today)) {
                    c.add(Calendar.DAY_OF_MONTH, days);
                }

                repeat.setText(rpt + " " + days + " " + mContext.getString(R.string.days));
                nextRepeat.setText(at + " " + sdfShort.format(c.getTime()));
            } else {
                when.setText(ro.getKm()+"km");
                int dist = ro.getRepeat();
                VehicleObject vehicleObject = dbHelper.getVehicle(ro.getCarID());
                int longest = Math.max(vehicleObject.getOdoCostKm(), vehicleObject.getOdoFuelKm());
                longest = Math.max(longest, vehicleObject.getOdoRemindKm());
                int newDist = ro.getKm();

                while (longest >  newDist) {
                    newDist += dist;
                }
                repeat.setText(rpt + " " + dist + " km");
                nextRepeat.setText(mContext.getString(R.string.at)+ " " + newDist +" km");
            }

            String titleString = ro.getTitle();
            String descString = ro.getDesc();
            int id = ro.getId();

            if (descString == null || descString.equals("")) {
                desc.setVisibility(View.GONE);
                descImg.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
            } else {
                desc.setVisibility(View.VISIBLE);
                descImg.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                desc.setText(descString);
            }
            title.setText(titleString);
            itemView.setTag(id);
        }
    }
}
