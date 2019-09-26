package com.example.nfcdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import me.aflak.libraries.callback.FingerprintDialogCallback;
import me.aflak.libraries.dialog.FingerprintDialog;

import static android.nfc.NdefRecord.createMime;

public class MainActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback , FingerprintDialogCallback {
    private NfcAdapter nfcAdapter;
    private EditText amountSend;
    private Button send,add;
    private TextView walletValue;
    private TextView info;
    private Integer amount=100;
    String text ;
    boolean a=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        amountSend=findViewById(R.id.editfield);
        send=findViewById(R.id.send);
        walletValue=findViewById(R.id.value);
        add=findViewById(R.id.add);
        info=findViewById(R.id.info);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
                amount+=Integer.parseInt(amountSend.getText().toString());
                text=amount.toString();
                walletValue.setText(text);
            }
        });

    }
    public void send(View v){
        closeKeyBoard();
        if(FingerprintDialog.isAvailable(this)) {
            FingerprintDialog.initialize(this)
                    .title("VERIFY")
                    .message("Confirm Finger Print to Continue")
                    .callback(this)
                    .show();
        }

    }
    @Override
    public void onAuthenticationSucceeded() {
        sendPrompt();
    }

    @Override
    public void onAuthenticationCancel() {
        info.setText("Authentication Failed");
    }
    public void sendPrompt(){
        text= amountSend.getText().toString();
        if(amount-Integer.parseInt(text)<0)
            Toast.makeText(this, "No Enough Amount", Toast.LENGTH_LONG).show();
        else {
            if (nfcAdapter == null) {
                Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            // Register callback
            nfcAdapter.setNdefPushMessageCallback(this, this);
        }
    }
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        amount=amount-Integer.parseInt(text);
        text=amount.toString();
        walletValue.setText(text);
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMime (
                        "application/vnd.com.example.android.beam", text.getBytes())
                });
        return msg;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    private void processIntent(Intent intent) {

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);

        NdefMessage msg = (NdefMessage) rawMsgs[0];

        text= new String(msg.getRecords()[0].getPayload());

       amount=Integer.parseInt(text)+amount;

       text=amount.toString();
       walletValue.setText(text);

    }
    private void closeKeyBoard(){
        View view=this.getCurrentFocus();

        InputMethodManager im=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(),0);
    }



}
