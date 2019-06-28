package com.github.malib.budgetmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.malib.budgetmanagementapp.Modal.MyData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth userAuth;

    DatabaseReference mDatabase;
    //    TextView  mTextView , mTextView2, mTextView3;
    FloatingActionButton mFloatingActionButton;
    RecyclerView mRecyclerView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.app_name);
        setSupportActionBar(myToolbar);

        userAuth = FirebaseAuth.getInstance();
        user = userAuth.getCurrentUser();
        String userID = user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("AllData").child(userID);


// mTextView = findViewById(R.id.textView4);
// mTextView2 = findViewById(R.id.textView5);
// mTextView3 = findViewById(R.id.textView6);
//
//
//
// mTextView.setText(user.getUid());
// mTextView3.setText(user.getEmail());


        mFloatingActionButton = findViewById(R.id.fab_add);
        mRecyclerView = findViewById(R.id.recyclerview);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setReverseLayout(true);


        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "floating action", Toast.LENGTH_SHORT).show();
                addData();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.userhomeoptions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.reset:

                FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //  Log.d(TAG, "Email sent.");
                                    Toast.makeText(HomeActivity.this, "Email Sent! please check mail", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                startActivity(new Intent(getApplicationContext(), PasswordReset.class).putExtra("email", user.getEmail()));
                HomeActivity.super.finish();
                break;

            case R.id.signout:
                FirebaseAuth
                        .getInstance()
                        .signOut();
                Toast.makeText(this, "Sign Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                HomeActivity.super.finish();
                break;

            default:


        }

        return super.onOptionsItemSelected(item);
    }

    public void addData() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.inputlayout, null);
        mydialog.setView(myView);
        final AlertDialog dialog = mydialog.create();

        dialog.show();


        final EditText title = myView.findViewById(R.id.title);
        final EditText description = myView.findViewById(R.id.description);
        final EditText budget = myView.findViewById(R.id.budget);
        Button save = myView.findViewById(R.id.submit_button);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mytitle = title.getText().toString().trim();
                String mydescription = description.getText().toString().trim();
                String mybudget = budget.getText().toString().trim();
                String mydate = DateFormat.getDateInstance().format(new Date());
                String id = mDatabase.push().getKey();

                MyData data = new MyData(mytitle, mydescription, mybudget, mydate, id);
                //  mReference.child(id).setValue(data);
                mDatabase.child(id).setValue(data);
                Toast.makeText(HomeActivity.this, "Data inserted", Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = mDatabase
                .limitToLast(50);
        FirebaseRecyclerOptions<MyData> options =
                new FirebaseRecyclerOptions.Builder<MyData>()
                        .setQuery(query, MyData.class)
                        .build();


        FirebaseRecyclerAdapter<MyData, MyViewHolder> adapter = new FirebaseRecyclerAdapter<MyData, MyViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i, @NonNull MyData myData) {
                myViewHolder.setTitle(myData.getTitle());
                myViewHolder.setDescription(myData.getDescription());
                myViewHolder.setBudget(myData.getBudget());
                myViewHolder.setDate(myData.getDate());
//
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = getLayoutInflater().inflate(R.layout.mydataitem, parent, false);
                return new MyViewHolder(view);
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

//
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView mtitle = mView.findViewById(R.id.title_item);
            mtitle.setText(title);

        }

        public void setDescription(String description) {
            TextView mdescription = mView.findViewById(R.id.description_item);
            mdescription.setText(description);

        }

        public void setBudget(String budget) {
            TextView mbudget = mView.findViewById(R.id.budget_item);
            mbudget.setText(budget);

        }

        public void setDate(String date) {
            TextView mdate = mView.findViewById(R.id.date_item);
            mdate.setText(date);
        }


    }

}


