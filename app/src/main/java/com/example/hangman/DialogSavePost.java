package com.example.hangman;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.hangman.databinding.NewPostSaveDialogBinding;

public class DialogSavePost extends Dialog implements View.OnClickListener {

    private NewPostSaveDialogBinding binding;
    private DialogSaveDataOrClose dialogSaveDataOrClose;


    public interface DialogSaveDataOrClose{
        void saveNewPostData();
        void closeDialog();
    }

    public void setDialogSaveDataOrClose(DialogSaveDataOrClose dialogSaveDataOrClose) {
        this.dialogSaveDataOrClose = dialogSaveDataOrClose;
    }

    public DialogSavePost(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.new_post_save_dialog, null, false);

        binding.setClicks(this);
        this.setCancelable(false);

        setContentView(binding.getRoot());

        int dialogWidth = (int) (Resources.getSystem().getDisplayMetrics().widthPixels);
        int dialogHeight = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.4f);

        this.getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.dialog_save_btn:
//                dialogSaveDataOrClose.saveNewPostData();
//                this.dismiss();
//                break;
            case R.id.dialog_exit_btn:
                dialogSaveDataOrClose.closeDialog();
                this.dismiss();
                break;
//            case R.id.post_save_dialog_close:
//                this.dismiss();
//                break;
        }
    }
}