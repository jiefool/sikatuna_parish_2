package com.tanginan.www.sikatuna_parish;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class SaveEventFragment extends DialogFragment {
    static Event thisEvent;
    View view;
    DialogFragment frag;
    String fileNameStr;

    public static SaveEventFragment newInstance(Event event) {
        thisEvent = event;
        SaveEventFragment fragment = new SaveEventFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_saveevent, container, false);
        frag = this;


        Button button = view.findViewById(R.id.save_event_to_docs);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Date c = Calendar.getInstance().getTime();
                fileNameStr = "untitled_"+c+".txt";
                EditText inputText = view.findViewById(R.id.filename);
                String inputStr = inputText.getText().toString();

                if (!inputStr.isEmpty()){
                    fileNameStr = inputStr+".txt";
                }

                Boolean isWritable = ((MainActivity)getActivity()).isExternalStorageWritable();


                if (isWritable) {
                    String data = "";

                    data += thisEvent.getName();
                    data += "\n";
                    data += thisEvent.getTimeStart();
                    data += "\n";
                    data += thisEvent.getTimeEnd();
                    data += "\n";
                    data += thisEvent.getDetails();

                    data += "\n";
                    data += "======================================";
                    data += "\n";


                    System.out.println(data);

                    File dir = ((MainActivity)getActivity()).getPublicDocumentStorageDir("/SikatunaParishEvents");
                    File file = new File(dir, fileNameStr);

                    //Write to file
                    try (FileWriter fileWriter = new FileWriter(file, true)) {
                        fileWriter.write(data);
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e) {
                        //Handle exception
                    }

                    ((MainActivity)getActivity()).scanMedia(file.getPath(), ((MainActivity)getActivity()));

                    Context context = ((MainActivity)getActivity()).getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, "Data saved to "+file.toString(), duration);
                    toast.show();

                }



                frag.dismiss();
            }
        });

        return view;
    }
}
