package dam.projects.projectdam.gui.marks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dam.projects.projectdam.R;
import dam.projects.projectdam.objects.Course;
import dam.projects.projectdam.objects.Grade;
import dam.projects.projectdam.objects.WeekDay;

/**
 * Created by Diogo on 29/05/2016 : 23:43.
 */
public class ExpandListAdapterActivity extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<Course> course;

    private static final String nullString = "Nenhum";

    public ExpandListAdapterActivity(Context context, ArrayList<Course> course) {
        this.context = context;
        this.course = course;
    }

    /**
     By Diogo
     Last change: 2016-05-30
     Altered to get marks from a course
     @return an ArrayList of items
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<Grade> chList = course.get(groupPosition).getItems();
        return chList.get(childPosition);
    }

    /**
     By Diogo
     Last change: 2016-05-30
     Altered to get marks from a course
     @return position of an grade
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /*
     By Renato
     Last change: 2016-05-30
     Altered to implement the list marks on grades
     Added interface improvements
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Grade child = (Grade) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_list_marks_content, null);
        }

        TextView tv1 = (TextView) convertView.findViewById(R.id.mark_epoca);
        TextView tv2 = (TextView) convertView.findViewById(R.id.mark_type_avaliation);
        TextView tv3 = (TextView) convertView.findViewById(R.id.mark_grade);

        tv1.setText(child.getPvepName());
        tv2.setText(child.getEvaluationName());
        tv3.setText(child.getGrade());

        return convertView;
    }

    /**
     By Diogo
     Last change: 2016-05-30
     Altered to get the size of a courses list
     @return size of courses list
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<Grade> chList = course.get(groupPosition).getItems();
        return chList.size();
    }

    /**
     By Diogo
     Last change: 2016-05-30
     Altered to get a course
     @return course
     */
    @Override
    public Object getGroup(int groupPosition) {
        return course.get(groupPosition);
    }

    /**
     By Diogo
     Last change: 2016-05-30
     Altered to get the size of the courses
     @return size of the courses
     */
    @Override
    public int getGroupCount() {
        return course.size();
    }

    /**
     By Diogo
     Last change: 2016-05-30
     Altered to get the position of the course
     @return position of the course
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /*
     By Diogo
     Last change: 2016-05-30
     Altered to implement the courses list
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Course group = (Course) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.courses_list, null);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.course);
        tv.setText(group.getCourse());

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
