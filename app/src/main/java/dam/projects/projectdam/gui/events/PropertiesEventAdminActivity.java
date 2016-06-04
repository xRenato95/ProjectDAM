package dam.projects.projectdam.gui.events;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import dam.projects.projectdam.R;
import dam.projects.projectdam.gui.IActivity;
import dam.projects.projectdam.objects.EventFinal;
import dam.projects.projectdam.sqlite.DataBase;

public class PropertiesEventAdminActivity extends AppCompatActivity implements IActivity {

    int eventoID;

    EventFinal evento;

    DataBase db;

    /*
       By Diogo
       Last change: 2016-05-22
       Altered to show some properties of an event
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_event_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView eventName = (TextView) findViewById(R.id.textView);
        TextView eventDesc = (TextView) findViewById(R.id.textView11);
        TextView eventInitialDate = (TextView) findViewById(R.id.textView13);
        TextView eventInitialHour = (TextView) findViewById(R.id.textView14);
        TextView eventFinalDate = (TextView) findViewById(R.id.textView15);
        TextView eventFinalHour = (TextView) findViewById(R.id.textView16);

        eventoID = getIntent().getIntExtra("EventID",-1);
        db = new DataBase(getApplicationContext());
        if(eventoID>-1){
            evento = db.getEvent(eventoID);
        }

        eventName.setText(evento.getTitle());
        eventDesc.setText(evento.getDescription());
        eventInitialDate.setText(evento.getDateBegin().toString());
        if(evento.getHourBegin()!=null) {
            eventInitialHour.setText(evento.getHourBegin().toString());
            eventFinalDate.setText(evento.getDateEnd().toString());
            eventFinalHour.setText(evento.getHourEnd().toString());
        }
        else{
            eventInitialHour.setText("");
            eventFinalDate.setText("");
            eventFinalHour.setText("");
        }

        /*String eventNameIntent = getIntent().getStringExtra("EventName");
        String eventDescIntent = getIntent().getStringExtra("EventDesc");

        eventName.setText(eventNameIntent);
        eventDesc.setText(eventDescIntent);

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                chkStudent(query.trim());
                return true;
            }
        });*/
    }

    /*
       By Diogo
       Last change: 2016-05-22
    */
    public void okEvent(View button)
    {
        /*Intent intent = new Intent();
        Helpers.changeActivity(CreateEventActivity.this, MenuActivity.class, false, null, null);*/
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}
