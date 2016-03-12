package com.majun.earthquake;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;

import gson.Quake;

/**
 * Created by ws02 on 2016/1/21.
 */
public class AlertDialogFragment extends DialogFragment {
    private static String QUAKE_DETAIL = "QUAKE_DETAIL";
    public static AlertDialogFragment alertDialogFragment;

    public static AlertDialogFragment newinstance(Context context, Quake quake) {
        Log.i("majun", "instance");
        alertDialogFragment = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateString = sdf.format(quake.getDate());
        String s = dateString + ":" + quake.getDetails() + "\r\n" + quake.getLink();
        bundle.putString(QUAKE_DETAIL, s);
        alertDialogFragment.setArguments(bundle);
        return alertDialogFragment;
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.i("majun", "oncreate");
//        View view = inflater.inflate(R.layout.dialog_view, container);
//        TextView text = (TextView) view.findViewById(R.id.dialog_text_view);
//        text.setText("This is the text in my dialog");
//        Button button = (Button) view.findViewById(R.id.dialog_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialogFragment.dismiss();
//            }
//        });
//        return view;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String s = getArguments().getString(QUAKE_DETAIL);
        builder.setTitle("quake detail");
        builder.setMessage(s);
        return builder.create();
    }
}
