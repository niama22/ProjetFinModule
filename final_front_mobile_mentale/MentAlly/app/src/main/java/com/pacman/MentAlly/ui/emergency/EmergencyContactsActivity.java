package com.pacman.MentAlly.ui.emergency;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.pacman.MentAlly.R;
import com.pacman.MentAlly.ui.emergency.api.RetrofitClient;
import com.pacman.MentAlly.ui.emergency.model.EmergencyContact;
import com.pacman.MentAlly.ui.home.MainActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyContactsActivity extends MainActivity {
    private Button addButton;
    private Button deleteListButton;
    private ListView contactListView;
    private ContactListAdapter contactListAdapter;
    private List<EmergencyContact> contactList;
    private Long currentUserId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        FrameLayout contentFrameLayout = findViewById(R.id.frag_container);
        getLayoutInflater().inflate(R.layout.activity_emergency_contacts, contentFrameLayout);

        // Get userId from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MentAllyPrefs", MODE_PRIVATE);
        currentUserId = prefs.getLong("userId", -1L);

        if (currentUserId == -1L) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();
        setupListeners();
        loadContacts();
    }

    private void initializeViews() {
        addButton = findViewById(R.id.addButton);
        deleteListButton = findViewById(R.id.delete_list_btn);
        contactListView = findViewById(R.id.contactList);
        progressBar = findViewById(R.id.progressBar);

        contactList = new ArrayList<>();
        contactListAdapter = new ContactListAdapter();
        contactListView.setAdapter(contactListAdapter);
    }

    private void setupListeners() {
        contactListView.setOnItemClickListener((parent, view, position, id) -> {
            showContactDetailsDialog(contactList.get(position));
        });

        addButton.setOnClickListener(v -> showAddContactDialog());

        deleteListButton.setOnClickListener(v -> showDeleteAllConfirmationDialog());
    }

    private void loadContacts() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance()
                .getApi()
                .getEmergencyContacts(currentUserId)
                .enqueue(new Callback<List<EmergencyContact>>() {
                    @Override
                    public void onResponse(Call<List<EmergencyContact>> call, Response<List<EmergencyContact>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            contactList.clear();
                            contactList.addAll(response.body());
                            contactListAdapter.notifyDataSetChanged();
                        } else {
                            showError("Error loading contacts");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<EmergencyContact>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void showAddContactDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.add_new_contact_dialog, null);
        EditText nameInput = dialogView.findViewById(R.id.contact_name);
        EditText phoneInput = dialogView.findViewById(R.id.phone_number);
        EditText emailInput = dialogView.findViewById(R.id.email);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Emergency Contact")
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                if (validateInputs(nameInput, phoneInput, emailInput)) {
                    // This is where the line should be added
                    EmergencyContact contact = new EmergencyContact(
                            currentUserId,
                            nameInput.getText().toString(),
                            phoneInput.getText().toString(),
                            emailInput.getText().toString()
                    );
                    addContact(contact);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void addContact(EmergencyContact contact) {
        // Add debug logging
        Log.d("EmergencyContact", "Adding contact: " +
                "userId=" + contact.getUserId() +
                ", name=" + contact.getName() +
                ", phone=" + contact.getPhoneNumber() +
                ", email=" + contact.getEmail());

        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance()
                .getApi()
                .createEmergencyContact(contact)
                .enqueue(new Callback<EmergencyContact>() {
                    @Override
                    public void onResponse(Call<EmergencyContact> call, Response<EmergencyContact> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            // Add debug logging for response
                            Log.d("EmergencyContact", "Contact added successfully: " +
                                    response.body().getId());
                            contactList.add(response.body());
                            contactListAdapter.notifyDataSetChanged();
                            showSuccess("Contact added successfully");
                        } else {
                            // Add error logging
                            Log.e("EmergencyContact", "Error adding contact: " +
                                    response.code() + " " + response.message());
                            showError("Error adding contact");
                        }
                    }

                    @Override
                    public void onFailure(Call<EmergencyContact> call, Throwable t) {
                        Log.e("EmergencyContact", "Network error: ", t);
                        progressBar.setVisibility(View.GONE);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void showContactDetailsDialog(EmergencyContact contact) {
        View dialogView = getLayoutInflater().inflate(R.layout.info_contact_dialog, null);
        TextView nameText = dialogView.findViewById(R.id.contact_name);
        TextView phoneText = dialogView.findViewById(R.id.phone_number);
        TextView emailText = dialogView.findViewById(R.id.email);

        nameText.setText(contact.getName());
        phoneText.setText(contact.getPhoneNumber());
        emailText.setText(contact.getEmail());

        new AlertDialog.Builder(this)
                .setTitle("Contact Details")
                .setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Delete", (dialog, which) -> {
                    showDeleteConfirmationDialog(contact);
                })
                .show();
    }

    private void showDeleteConfirmationDialog(EmergencyContact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete this contact?")
                .setPositiveButton("Yes", (dialog, which) -> deleteContact(contact))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteContact(EmergencyContact contact) {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance()
                .getApi()
                .deleteEmergencyContact(currentUserId, contact.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            contactList.remove(contact);
                            contactListAdapter.notifyDataSetChanged();
                            showSuccess("Contact deleted successfully");
                        } else {
                            showError("Error deleting contact");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void showDeleteAllConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete All Contacts")
                .setMessage("Are you sure you want to delete all contacts? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteAllContacts())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteAllContacts() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance()
                .getApi()
                .deleteAllEmergencyContacts(currentUserId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            contactList.clear();
                            contactListAdapter.notifyDataSetChanged();
                            showSuccess("All contacts deleted successfully");
                        } else {
                            showError("Error deleting contacts");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private boolean validateInputs(EditText nameInput, EditText phoneInput, EditText emailInput) {
        boolean isValid = true;
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Name is required");
            isValid = false;
        }

        if (phone.isEmpty() || !isPhoneNumberValid(phone)) {
            phoneInput.setError("Valid phone number is required");
            isValid = false;
        }

        if (email.isEmpty() || !isEmailValid(email)) {
            emailInput.setError("Valid email is required");
            isValid = false;
        }

        return isValid;
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.matches("^[0-9]{8,}$");
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private class ContactListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public EmergencyContact getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(EmergencyContactsActivity.this)
                        .inflate(R.layout.contact, parent, false);

                holder = new ViewHolder();
                holder.nameText = convertView.findViewById(R.id.contact_name);
                holder.phoneText = convertView.findViewById(R.id.phone_number);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            EmergencyContact contact = getItem(position);
            holder.nameText.setText(contact.getName());
            holder.phoneText.setText(contact.getPhoneNumber());

            return convertView;
        }

        private class ViewHolder {
            TextView nameText;
            TextView phoneText;
        }
    }
}