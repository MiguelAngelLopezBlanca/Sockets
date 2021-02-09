package miguelangellopezblanca.psp.sockets;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private boolean run = true;
    private Socket client;
    private DataInputStream flujoE;
    private DataOutputStream flujoS;
    private Thread listeningThread;
    TextView tvChat;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread hebra= new Thread(new Runnable() {
            @Override
            public void run() {
                startClient("10.0.2.2", 5000);
            }
        });
        hebra.start();

        init();

    }

    private void init() {

        tvChat = findViewById(R.id.tvChat);
        EditText etMensaje = findViewById(R.id.etMensaje);

        Button btEnviarMensaje = findViewById(R.id.btEnviarMensaje);


        btEnviarMensaje.setVisibility(View.VISIBLE);
        etMensaje.setEnabled(true);



        btEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textMensaje = etMensaje.getText().toString();
                Thread mensaje = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            flujoS.writeUTF(textMensaje);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                mensaje.start();

                etMensaje.setText("");
            }

        });


    }


    public void startClient(String host, int port){
        try {
            client = new Socket(host, port);
            flujoE = new DataInputStream(client.getInputStream());
            flujoS = new DataOutputStream(client.getOutputStream());
            listeningThread = new Thread(){
                @Override
                public void run() {
                    String text;
                    while(run){
                        try {
                            text = flujoE.readUTF();
                            tvChat.append(text);
                        } catch (IOException ex) {
                            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }

            };
            listeningThread.start();
            //flujoS.close();
            //flujoE.close();

        } catch (IOException ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* METODOS DE CREACION Y EVENTOS DEL MENU */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                requestExitConfirmation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /* METODO PARA CERRAR LA APLICACION */

    private void requestExitConfirmation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.dialog_confirm_exit)
                .setPositiveButton(R.string.string_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.string_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                });
        builder.create();
        builder.show();
    }

}