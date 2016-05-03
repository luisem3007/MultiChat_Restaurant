package com.example.jorge.multichat_restaurant;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private EditText editTxt;
    private Button btn, button;
    private ListView list;
    private ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    String fileName = "ejemplo.txt" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setTitle(information.selectedFile);
        editTxt = (EditText) findViewById(R.id.txt);
        btn = (Button) findViewById(R.id.button);
        list = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<String>();
        arrayList.clear();
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_layout, arrayList);
        list.setAdapter(adapter);

        try {
            FileInputStream fileIn= null;
            fileName = information.selectedFile;
            fileIn = openFileInput(fileName);
            InputStreamReader InputRead= new InputStreamReader(fileIn);
            BufferedReader br = new BufferedReader(InputRead);

            String s="";
            arrayList.clear();
            int c = 0 ;
            while ((s = br.readLine()) != null)
            {
                if (c != 0) {
                    arrayList.add(s);
                }
                c++;
            }

            adapter.notifyDataSetChanged();
            InputRead.close();
            //Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FileInputStream fileIn= null;
                                        fileName = information.selectedFile;
                                        fileIn = openFileInput(fileName);
                                        InputStreamReader InputRead= new InputStreamReader(fileIn);
                                        BufferedReader br = new BufferedReader(InputRead);

                                        String s="";
                                        arrayList.clear();
                                        int c = 0 ;
                                        while ((s = br.readLine()) != null)
                                        {
                                            if (c !=0 ) {
                                                arrayList.add(s);
                                            }
                                            c ++;
                                    }

                                    adapter.notifyDataSetChanged();
                                    InputRead.close();
                                    //Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();


        //************************************************************************************Button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sendMessage = editTxt.getText().toString();
                //                Information.arrayList.add("Client says: " + sendMessage);
                //              adapter.notifyDataSetChanged();
                editTxt.setText(" ");

                fileName = information.selectedFile;
                FileOutputStream fileout = null;
                String IP="";
                try {

                    String filePath = getApplicationInfo().dataDir + "/files/" + information.selectedFile;
                    File fileUsuarios = new File(filePath);
                    FileInputStream fileIn = null;
                    fileIn = openFileInput(information.selectedFile);
                    InputStreamReader InputRead = new InputStreamReader(fileIn);
                    BufferedReader br = new BufferedReader(InputRead);

                    IP = br.readLine();
                    fileout = openFileOutput(fileName, Context.MODE_APPEND);
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write("Tú: "+sendMessage + "\n");
                    outputWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                ClientAsyncTask clientAST = new ClientAsyncTask();
                clientAST.execute(new String[]{IP, "5002", sendMessage});

            }
        });
    }

    //********************************************************************************
    class ClientAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                //Create a client socket and define internet address and the port of the server
                //Socket socket = new Socket(params[0],
                 //       Integer.parseInt(params[1]));


                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(params[0], Integer.parseInt(params[1])),1000);
                //Get the input stream of the client socket
                InputStream is = socket.getInputStream();
                //Get the output stream of the client socket
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                //Write data to the output stream of the client socket
                out.println(params[2]);
                //Buffer the data coming from the input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                result = br.readLine();
                result = params[2];

                //Close the client socket
                socket.close();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                information.error = 1;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                information.error = 1;
            } catch (IOException e) {
                e.printStackTrace();
                information.error = 1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            if (information.error==1)
            {
                Toast.makeText(getApplicationContext(), "User is not available!", Toast.LENGTH_SHORT).show();
            }
            information.error = 0;
        }
    }

}
