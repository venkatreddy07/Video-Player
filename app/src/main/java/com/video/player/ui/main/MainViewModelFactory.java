package com.video.player.ui.main;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.video.player.databinding.ActivityMainBinding;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Context context;
    private MainActivity activity;

    private ActivityMainBinding binding;

    public MainViewModelFactory(MainActivity activity, ActivityMainBinding binding) {
        this.activity = activity;
        this.binding = binding;
    }


   /* public MainViewModelFactory(Context context, ActivityMainBinding binding) {
        this.context = context;
        this.binding = binding;
    }*/

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MainViewModel(activity, binding);
    }
}
