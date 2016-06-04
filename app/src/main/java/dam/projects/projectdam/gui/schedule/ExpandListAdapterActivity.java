package dam.projects.projectdam.gui.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dam.projects.projectdam.R;
import dam.projects.projectdam.helpers.HelpersDate;
import dam.projects.projectdam.objects.ScheduleDay;
import dam.projects.projectdam.objects.WeekDay;

/**
 * Created by Diogo on 20/05/2016 : 23:38.
 */
public class ExpandListAdapterActivity extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<WeekDay> weekDay;

    private static final String nullString = "Nenhum";

    public ExpandListAdapterActivity(Context context, ArrayList<WeekDay> weekDay) {
        this.context = context;
        this.weekDay = weekDay;
    }

    /**
       By Diogo
       Last change: 2016-05-21
       Altered to get schedule tasks from a week day
       @return an ArrayList of items
    */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ScheduleDay> chList = weekDay.get(groupPosition).getItems();
        return chList.get(childPosition);
    }

    /**
     By Diogo
     Last change: 2016-05-21
     Altered to get schedule tasks from a week day
     @return position of an schedule task
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /*
     By Renato
     Last change: 2016-05-29
     Altered to implement the list schedule on schedule tasks
     Added interface improvements
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ScheduleDay child = (ScheduleDay) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_list_schedule_content, null);
        }

        TextView tv1 = (TextView) convertView.findViewById(R.id.schedule_initial);
        TextView tv2 = (TextView) convertView.findViewById(R.id.schedule_final);
        TextView tv3 = (TextView) convertView.findViewById(R.id.schedule_desc);
        TextView tv4 = (TextView) convertView.findViewById(R.id.schedule_room);
        TextView tv5 = (TextView) convertView.findViewById(R.id.schedule_type_class);

        tv1.setText(HelpersDate.hourToString(child.getTimeStart(), HelpersDate.HOUR_FORMAT_SMALL));
        tv2.setText(HelpersDate.hourToString(child.getTimeEnd(), HelpersDate.HOUR_FORMAT_SMALL));
        tv3.setText(child.getCourseName());
        tv4.setText(child.getRoomName().equals(nullString) ? "---" : child.getRoomName());
        tv5.setText(child.getCourseTypeS());

        return convertView;
    }

    /**
     By Diogo
     Last change: 2016-05-21
     Altered to get the size of a schedule tasks list
     @return size of schedule tasks list
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ScheduleDay> chList = weekDay.get(groupPosition).getItems();
        return chList.size();
    }

    /**
     By Diogo
     Last change: 2016-05-21
     Altered to get a week day
     @return week day
     */
    @Override
    public Object getGroup(int groupPosition) {
        return weekDay.get(groupPosition);
    }

    /**
     By Diogo
     Last change: 2016-05-21
     Altered to get the size of the week days
     @return size of the week days
     */
    @Override
    public int getGroupCount() {
        return weekDay.size();
    }

    /**
     By Diogo
     Last change: 2016-05-21
     Altered to get the position of a week day
     @return position of the week day
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /*
     By Diogo
     Last change: 2016-05-21
     Altered to implement the week days list
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        WeekDay group = (WeekDay) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.week_days_list, null);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.week_day);
        tv.setText(group.getWeekDay());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
