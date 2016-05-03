package com.example.jorge.multichat_restaurant;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private Button bLogin, bBorrar;
    private EditText editTxtUsuario, editTxtContraseña;
    String ServerIP = "192.168.100.8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Chat");

        bLogin = (Button) findViewById(R.id.bLogin);
        bBorrar = (Button) findViewById(R.id.bBorrar);
        editTxtUsuario = (EditText) findViewById(R.id.editTextUsuario);
        editTxtContraseña = (EditText) findViewById(R.id.editTextContraseña);

        bBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTxtUsuario.setText("");
                editTxtContraseña.setText("");

            }
        });


        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sendMessage = "Login_";
                sendMessage = sendMessage + editTxtUsuario.getText().toString();
                sendMessage = sendMessage + "_" + editTxtContraseña.getText().toString();

                ClientAsyncTask clientASTR = new ClientAsyncTask();
                clientASTR.execute(new String[]{"192.168.43.179", "5003", sendMessage});

            }
        });

        // *********************************************************************Start threading [Hearing (Server)]
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Create a server socket object and bind it to a port
                    ServerSocket socServer = new ServerSocket(5003);
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

    }

    //********************************************************************************
    class ClientAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                //Create a client socket and define internet address and the port of the server
               // Socket socket = new Socket(params[0],
               //         Integer.parseInt(params[1]));

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
                //Close the client socket
                socket.close();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                information.errorServer = 1;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                information.errorServer = 1;
            } catch (IOException e) {
                e.printStackTrace();
                information.errorServer = 1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            if (information.errorServer == 1)
            {
                Toast.makeText(getApplicationContext(), "Lo sentimos el servicio no esta disponible por el momento", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Ejercite su mente en lo que reestablecemos la conexion", Toast.LENGTH_SHORT).show();
                startGame();
            }
            information.errorServer = 0;


        }
    }

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
                //result = mySocket.getInetAddress().toString();
                //result = result + "_"+br.readLine();

                result = br.readLine();
                //ServerIP = result;
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
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

            if (s.equals("Login Accepted"))
            {
                startChat();
            }else {



            }
        }

        //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();


    }

    public void startChat(){
        Intent i;
        i = new Intent(this, salaChat.class);
        startActivity(i);
    }


    public void startGame(){
        Intent i;
        i = new Intent(this, juego.class);
        startActivity(i);
    }
}




