package com.pacman.MentAlly.ui.emergency;


import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.emergency.api.RetrofitClient;
import com.pacman.MentAlly.ui.emergency.model.EmergencyContact;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyDialogFragment extends DialogFragment {
    private static final int REQUEST_CALL_PERMISSION = 1;
    private static final int REQUEST_SMS_PERMISSION = 2;
    private static final String EMERGENCY_NUMBER = "911";
    private Long currentUserId;
    private List<EmergencyContact> contacts = new ArrayList<>();
    private int currentContactIndex = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        currentUserId = prefs.getLong("userId", -1L);

        return new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.emergency_title)
                .setMessage(R.string.emergency_message)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    if (currentUserId != -1L) {
                        loadContactsAndInitiateEmergency();
                    } else {
                        Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.no, (dialog, id) -> dismiss())
                .create();
    }

    private void loadContactsAndInitiateEmergency() {
        RetrofitClient.getInstance()
                .getApi()
                .getEmergencyContacts(currentUserId)
                .enqueue(new Callback<List<EmergencyContact>>() {
                    @Override
                    public void onResponse(Call<List<EmergencyContact>> call, Response<List<EmergencyContact>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            contacts = response.body();
                            initiateEmergencyProtocol();
                        } else {
                            Toast.makeText(getContext(),
                                    "Failed to load emergency contacts",
                                    Toast.LENGTH_SHORT).show();
                            initiateEmergencyCall();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<EmergencyContact>> call, Throwable t) {
                        Toast.makeText(getContext(),
                                "Network error: proceeding with emergency call",
                                Toast.LENGTH_SHORT).show();
                        initiateEmergencyCall();
                    }
                });
    }

    private void initiateEmergencyProtocol() {
        // First call emergency services
        initiateEmergencyCall();

        // Then start sending SMS to emergency contacts
        if (!contacts.isEmpty()) {
            checkSMSPermissionAndSend();
        }
    }

    private void initiateEmergencyCall() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestCallPermission();
        } else {
            startEmergencyCall();
        }
    }

    private void requestCallPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.CALL_PHONE},
                REQUEST_CALL_PERMISSION);
    }

    private void startEmergencyCall() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + EMERGENCY_NUMBER));
        startActivity(callIntent);
    }

    private void checkSMSPermissionAndSend() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestSMSPermission();
        } else {
            sendEmergencySMSToNextContact();
        }
    }

    private void requestSMSPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.SEND_SMS},
                REQUEST_SMS_PERMISSION);
    }

    private void sendEmergencySMSToNextContact() {
        if (currentContactIndex < contacts.size()) {
            EmergencyContact contact = contacts.get(currentContactIndex);
            sendEmergencySMS(contact.getPhoneNumber());
            currentContactIndex++;

            // Schedule next SMS with a slight delay to prevent flooding
            if (currentContactIndex < contacts.size()) {
                requireView().postDelayed(this::sendEmergencySMSToNextContact, 1000);
            }
        }
    }

    private void sendEmergencySMS(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body",
                    "EMERGENCY ALERT: I am currently experiencing an emergency and have contacted emergency services. " +
                            "This message was sent automatically by the MentAlly app.");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "Failed to send SMS to " + phoneNumber,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_CALL_PERMISSION:
                    startEmergencyCall();
                    break;
                case REQUEST_SMS_PERMISSION:
                    sendEmergencySMSToNextContact();
                    break;
            }
        } else {
            String message = requestCode == REQUEST_CALL_PERMISSION ?
                    "Cannot make emergency call without permission" :
                    "Cannot send emergency SMS without permission";
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // Reset the contact index when dialog is dismissed
        currentContactIndex = 0;
    }
}
