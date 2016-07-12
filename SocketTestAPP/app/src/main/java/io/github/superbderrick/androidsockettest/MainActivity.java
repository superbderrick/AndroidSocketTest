package io.github.superbderrick.androidsockettest;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;



public class MainActivity extends AppCompatActivity {

    private EditText mAddressEditText;
    private EditText mPortEditText;
    private Button mConnectButton;
    private Button mClearButton;
    private TextView mResultTextView;
    private Spinner mTypeSpinner;

    public static final Handler mHandler = new Handler();
    public static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init()
    {
        mAddressEditText = (EditText)findViewById(R.id.address_edittext);
        mPortEditText = (EditText)findViewById(R.id.port_edittext);
        mConnectButton = (Button)findViewById(R.id.connect_button);
        mClearButton = (Button)findViewById(R.id.clear_button);
        mResultTextView = (TextView)findViewById(R.id.response_textview);
        mTypeSpinner = (Spinner)findViewById(R.id.sockettype_spinner);

        mConnectButton.setOnClickListener(ConnectButtonListener);
        mClearButton.setOnClickListener(ClearButtonListener);
    }

    View.OnClickListener ConnectButtonListener = new View.OnClickListener() {

        public void onClick(View arg0) {
            if(mAddressEditText != null && mPortEditText != null)
            {
                NetworkTask myClientTask = new NetworkTask(
                        mAddressEditText.getText().toString(),
                        Integer.parseInt(mPortEditText.getText().toString())
                );

                myClientTask.execute();
            }

        }
    };

    View.OnClickListener ClearButtonListener = new View.OnClickListener() {

        public void onClick(View arg0) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mResultTextView.setText(" ");
                }
            });

        }
    };



    public class NetworkTask extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response;

        NetworkTask(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                Socket socket = new Socket(dstAddress, dstPort);
                InputStream inputStream = socket.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                        1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                socket.close();
                response = byteArrayOutputStream.toString("UTF-8");

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mResultTextView.setText(response);
                }
            });

            //super.onPostExecute(result);
        }

    }

}

