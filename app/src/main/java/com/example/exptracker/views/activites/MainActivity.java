package com.example.exptracker.views.activites;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exptracker.R;
import com.example.exptracker.adapters.TransactionsAdapter;
import com.example.exptracker.databinding.ActivityMainBinding;
import com.example.exptracker.models.Transaction;
import com.example.exptracker.utils.Constants;
import com.example.exptracker.utils.Helper;
import com.example.exptracker.viewmodels.MainViewModel;
import com.example.exptracker.views.fragments.AddTransactionFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    Calendar calendar;
    /*
    0 = Daily
    1 = Monthly
    2 = Calendar
    3 = Summary
    4 = Notes
     */


    public MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setTitle("Transactions");


        Constants.setCategories();

        calendar = Calendar.getInstance();
        updateDate();

        binding.nextDateBtn.setOnClickListener(c-> {
            if(Constants.SELECTED_TAB == Constants.DAILY) {
                calendar.add(Calendar.DATE, 1);
            } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, 1);
            }
            updateDate();
        });
        
        binding.prevBtn.setOnClickListener(c-> {
            if(Constants.SELECTED_TAB == Constants.DAILY) {
                calendar.add(Calendar.DATE, -1);
            } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
                calendar.add(Calendar.MONTH, -1);
            }
            updateDate();
        });


        binding.floatingActionButton.setOnClickListener(c -> {
            new AddTransactionFragment().show(getSupportFragmentManager(), null);
        });


        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("Monthly")) {
                    Constants.SELECTED_TAB = 1;
                    updateDate();
                } else if(tab.getText().equals("Daily")) {
                    Constants.SELECTED_TAB = 0;
                    updateDate();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });





        binding.transactionList.setLayoutManager(new LinearLayoutManager(this));

        viewModel.transactions.observe(this, new Observer<RealmResults<Transaction>>() {
            @Override
            public void onChanged(RealmResults<Transaction> transactions) {
                TransactionsAdapter transactionsAdapter = new TransactionsAdapter(MainActivity.this, transactions);
                binding.transactionList.setAdapter(transactionsAdapter);
                if(transactions.size() > 0) {
                    binding.emptyState.setVisibility(View.GONE);
                } else {
                    binding.emptyState.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.totalIncome.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                binding.incomeLbl.setText(String.valueOf(aDouble));
            }
        });

        viewModel.totalExpense.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                binding.expenseLbl.setText(String.valueOf(aDouble));
            }
        });

        viewModel.totalAmount.observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                binding.totalLbl.setText(String.valueOf(aDouble));
            }
        });
        viewModel.getTransactions(calendar);



    }

    public void getTransactions() {
        viewModel.getTransactions(calendar);
    }



    void updateDate() {
        if(Constants.SELECTED_TAB == Constants.DAILY) {
            binding.textView.setText(Helper.formatDate(calendar.getTime()));
        } else if(Constants.SELECTED_TAB == Constants.MONTHLY) {
            binding.textView.setText(Helper.formatDateByMonth(calendar.getTime()));
        }
        viewModel.getTransactions(calendar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}