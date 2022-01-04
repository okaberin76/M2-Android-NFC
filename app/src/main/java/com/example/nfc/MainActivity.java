package com.example.nfc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    NfcAdapter adapter;
    PendingIntent mPendingIntent;

    private final List<Etudiant> etudiantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyDatabaseHelper db = new MyDatabaseHelper(this);
        db.createDefaultEtudiantsIfNeed();

        List<Etudiant> list=  db.getAllEtudiants();
        this.etudiantList.addAll(list);

        for(Etudiant elem: etudiantList) {
            System.out.println ("etudiant : " + elem.getPrenom() + elem.getNom() + elem.getUid());
        }

        Intent intent;
        intent = this.getIntent();

        NfcManager manager = (NfcManager) this.getSystemService(Context.NFC_SERVICE);
        adapter = manager.getDefaultAdapter();

        if (adapter != null) {
            if (adapter.isEnabled()) {
                if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                    Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                }
            }

            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                    getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getTagInfo(intent);
    }

    private void getTagInfo(Intent intent) {
        byte[] uid = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

        String s = new BigInteger(uid).toString(16);
        afficherMessage(s);
    }


    public void afficherMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}