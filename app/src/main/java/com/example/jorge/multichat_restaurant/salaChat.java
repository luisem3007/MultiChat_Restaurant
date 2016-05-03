package com.example.jorge.multichat_restaurant;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class salaChat extends AppCompatActivity {
    private EditText editTxt;
    private Button btn, button;
    private ListView list;
    private ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_chat);
        setTitle("Sala Chat");
        list = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_layout, arrayList);
        list.setAdapter(adapter);

// *********************************************************************Fill names of the chat users
        try {
            String files = "";
            String path = getApplicationInfo().dataDir + "/files/";
            File f = new File(path);
            File file[] = f.listFiles();
            arrayList.clear();
            for (File i : file) {

                files = files + i.getName() + "\n";
                arrayList.add(i.getName());
            }

            adapter.notifyDataSetChanged();
            //editTxt.setText("");
            //display file saved message
        } catch (Exception e) {
            e.printStackTrace();
        }
        // *********************************************************************End Fill names of the chat users


        // *********************************************************************Start threading [Hearing (Server)]
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Create a server socket object and bind it to a port
                    ServerSocket socServer = new ServerSocket(5002);
                    //Create server side client socket reference
                    Socket socClient = null;
                    //Infinite loop will listen for client requests to connect
                    while (true) {
                        //Accept the client connection and hand over communication to server side client socket
                        socClient = socServer.accept();
                        //For each client new instance of AsyncTask will be created
                        ServerAsyncTask serverAsyncTask = new ServerAsyncTask();
                        //Start the AsyncTask execution
                        //Accepted client socket object will pass as the parameter
                        serverAsyncTask.execute(new Socket[]{socClient});
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();




        // *********************************************************************End Start Hearing (Server)

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String item = ((TextView)view).getText().toString();
                // Toast.makeText(getApplicationContext(), "Position: "+position, Toast.LENGTH_SHORT).show();
                String IP = ((TextView)view).getText().toString();
                information.selectedFile = IP;
                IP=IP.replaceAll("_","\\.");
              //  Toast.makeText(getApplicationContext(), IP, Toast.LENGTH_SHORT).show();
                startChat();
            }
        });
    }

    public void startChat()
    {
        Intent i;
        i = new Intent(this, Main2Activity.class);
        startActivity(i);
    }

    /**
     * AsyncTask which handles the commiunication with clients
     */
    // *********************************************************************[Hearing (Server)] Class

    class ServerAsyncTask extends AsyncTask<Socket, Void, String> {
        //Background task which serve for the client
        @Override
        protected String doInBackground(Socket... params) {
            String result = null;
            //Get the accepted socket object
            Socket mySocket = params[0];
            try {
                //Get the data input stream comming from the client
                InputStream is = mySocket.getInputStream();
                //Get the output stream to the client
                PrintWriter out = new PrintWriter(
                        mySocket.getOutputStream(), true);

                //Write data to the data output stream
                // Buffer the data input stream
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                //Read the contents of the data buffer
                result = mySocket.getInetAddress().toString();
                result = result + "_" + br.readLine();
                //Close the client connection
                mySocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //After finishing the execution of background task data will be write the text view
            //arrayList.add("Restaurant says: " + s);
            //Store information on respected file and update the arraylist......

            String[] parts = s.split("_");
            String IP = parts[0];
            //IP=IP.replaceAll("\\.", "_");
            IP = IP.replaceAll("/", "");
            //Toast.makeText(getBaseContext(), IP, Toast.LENGTH_SHORT).show();
            //fileName = IP;

            fileName = IP;
            String message = parts[1];

            /*
            try {

                String filePath = getApplicationInfo().dataDir + "/files/" + "UsuariosInformacion";
                File fileUsuarios = new File(filePath);

                if (fileUsuarios.exists()) {
                    //Toast.makeText(getApplicationContext(), "Exists", Toast.LENGTH_SHORT).show();
                    try {
                        FileInputStream fileIn = null;
                        fileIn = openFileInput(fileName);
                        InputStreamReader InputRead = new InputStreamReader(fileIn);
                        BufferedReader br = new BufferedReader(InputRead);
                        String s1 = "";

                        while ((s1 = br.readLine()) != null) {
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_SHORT).show();

                            String[] parts1 = s1.split("_");
                            //Toast.makeText(getApplicationContext(), "IPB"+parts1[1], Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getApplicationContext(), "IPC"+IP, Toast.LENGTH_SHORT).show();

                            if (IP.equals(parts1[1])) {
                                Toast.makeText(getApplicationContext(), "Nombre: " + parts1[0], Toast.LENGTH_SHORT).show();
                                fileName = parts1[0];


                            } else {
                                //Toast.makeText(getApplicationContext(), "Conversation doesnt exist" + parts1[1], Toast.LENGTH_SHORT).show();


                            }
                        }

                        //adapter.notifyDataSetChanged();
                        InputRead.close();
                        //Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    fileName = "UsuariosInformacion";
                    //Toast.makeText(getApplicationContext(), "Dont exists", Toast.LENGTH_SHORT).show();
                    FileOutputStream fileUser = openFileOutput(fileName, Context.MODE_APPEND);
                    OutputStreamWriter outputWriterUser = new OutputStreamWriter(fileUser);
                    outputWriterUser.write("Cliente-1_" + IP + "\n");
                    outputWriterUser.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
*/
            //fileName = s;
            try {

                //Checks if conversation exists
                int numeroCliente= 1 ;
                int foundIPFlag = 0, flagOut = 0 ;
                String fileNameClient = "Cliente-"+numeroCliente;
                String filePath = getApplicationInfo().dataDir + "/files/" + "Cliente-"+numeroCliente;
                File fileUsuarios = new File(filePath);

                while (foundIPFlag ==0 && flagOut == 0) {
                    if (fileUsuarios.exists()) {
            //            Toast.makeText(getApplicationContext(), "Exists", Toast.LENGTH_SHORT).show();

                        FileInputStream fileIn = null;
                        fileIn = openFileInput(fileNameClient);
                        InputStreamReader InputRead = new InputStreamReader(fileIn);
                        BufferedReader br = new BufferedReader(InputRead);

                        s = br.readLine();
              //          Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
             //           Toast.makeText(getApplicationContext(),"IP" + IP, Toast.LENGTH_SHORT).show();

                        if (s.equals(IP)) {
                            foundIPFlag = 1;
               //             Toast.makeText(getApplicationContext(),"Entro IP Igual", Toast.LENGTH_SHORT).show();
                        } else {
                            numeroCliente++;
                            fileNameClient = "Cliente-" + numeroCliente;
                            filePath = getApplicationInfo().dataDir + "/files/" + "Cliente-" + numeroCliente;
                            fileUsuarios = new File(filePath);
                        }
                    } else {
                        flagOut = 1;
                    }
                }
                information.selectedFile = "Cliente-"+numeroCliente;
                //Dont exists or exists
                //if (!fileUsuarios.exists()){
                if (flagOut ==1)
                {
                    FileOutputStream fileout = openFileOutput("Cliente-"+numeroCliente, Context.MODE_APPEND);
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write(IP + "\n");
                    outputWriter.write("Cliente: "+message + "\n");
                    outputWriter.close();
                }else
                {
                FileOutputStream fileout = openFileOutput("Cliente-"+numeroCliente, Context.MODE_APPEND);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                outputWriter.write("Cliente: " + message + "\n");
                outputWriter.close();

            }
                String files = "";
                String path = getApplicationInfo().dataDir + "/files/";
                File f = new File(path);
                File file[] = f.listFiles();
                arrayList.clear();


                for (File i : file) {
                    files = files + i.getName() + "\n";
                    arrayList.add(i.getName());
                }


                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // *********************************************************************End[Hearing (Server)] Class
    }
}