package com.example.hangman;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.hangman.databinding.FragmentGameBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.example.hangman.HomePage.soundOff;
import static com.example.hangman.HomePage.soundOn;
import static com.example.hangman.MainActivity.newToken;

public class Game extends Fragment implements View.OnClickListener {
    private final Handler mHandler = new Handler();
    public static int Score = 0;
    private static MediaPlayer audioPlayer;
    TextView audioTime;
    private Dialog dialog;
    ImageView imageView;
    private LinearLayout dialogLayout;
    private Word word = null;
    private PopupWindow pw;
    private FragmentGameBinding binding;
    private DialogSavePost dialogSavePost;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    public static MediaPlayer mediaPlayer;
    MyTimerTask myTimerTask;
    Timer timer;

    int duration;
    private ImageView[] bodyParts;
    private static Map<String, String[]> wordsByCategory = new HashMap<>();
    private TextView[] charsWords;

    private String currWord;
    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10;
    private Button btn2_1, btn2_2, btn2_3, btn2_4, btn2_5, btn2_6, btn2_7, btn2_8, btn2_9, btn2_10;
    private Button btn3_1, btn3_2, btn3_3, btn3_4, btn3_5, btn3_6, btn3_7, btn3_8, btn3_9, btn3_10;
    private Button btn4_1, btn4_2, btn4_3, btn4_4, btn4_5, btn4_6, btn4_7, btn4_8, btn4_9;
    private static int lives;
    private int currPart;
    private static String alreadyExists = "";
    private static String str = "";
    private static int usId;
    AnimatorSet set;
    AnimatorSet set2;
    private ArrayList<Button> buttons = new ArrayList<>();
    public int wordSize = 0;
    int repeatedCount = 0;
    private MediaPlayer mediaPlayerGame;
    public boolean isClicked = false;

    public Game() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGameBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        mDBHelper = new DatabaseHelper(getActivity());

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        binding.setClicks(this);

        dialogLayout = binding.getRoot().findViewById(R.id.child);


        initializationWords(binding.getRoot());
        lives = 6;
        bodyParts = new ImageView[lives];
        bodyParts[0] = binding.headOf;
        bodyParts[1] = binding.bodyOf;
        bodyParts[2] = binding.leftHand;
        bodyParts[3] = binding.rightHand;
        bodyParts[4] = binding.leftFoot;
        bodyParts[5] = binding.rightFoot;


        String text = getArguments().getString("category");
        ArrayList<String> allWords = new ArrayList<>();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            newToken = task.getResult().getToken();
                            Cursor cursor1 = mDb.rawQuery("SELECT * FROM words INNER JOIN categories on " +
                                    "categories.id=words.category_id where categories.name=" + "'" + text + "'", null);
                            cursor1.moveToFirst();

                            while (!cursor1.isAfterLast()) {
                                allWords.add(cursor1.getString(0));
                                cursor1.moveToNext();
                            }
                            cursor1.close();

                            wordSize = allWords.size();

                        }
                    }
                });

        playGame();
        return view;
    }


    public void initializationWords(View view) {

        btn1 = view.findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        buttons.add(btn1);

        btn2 = view.findViewById(R.id.btn2);
        btn2.setOnClickListener(this);
        buttons.add(btn2);

        btn3 = view.findViewById(R.id.btn3);
        btn3.setOnClickListener(this);
        buttons.add(btn3);

        btn4 = view.findViewById(R.id.btn4);
        btn4.setOnClickListener(this);
        buttons.add(btn4);

        btn5 = view.findViewById(R.id.btn5);
        btn5.setOnClickListener(this);
        buttons.add(btn5);

        btn6 = view.findViewById(R.id.btn6);
        btn6.setOnClickListener(this);
        buttons.add(btn6);

        btn7 = view.findViewById(R.id.btn7);
        btn7.setOnClickListener(this);
        buttons.add(btn7);

        btn8 = view.findViewById(R.id.btn8);
        btn8.setOnClickListener(this);
        buttons.add(btn8);

        btn9 = view.findViewById(R.id.btn9);
        btn9.setOnClickListener(this);
        buttons.add(btn9);

        btn10 = view.findViewById(R.id.btn10);
        btn10.setOnClickListener(this);
        buttons.add(btn10);

        btn2_1 = view.findViewById(R.id.btn2_1);
        btn2_1.setOnClickListener(this);
        buttons.add(btn2_1);

        btn2_2 = view.findViewById(R.id.btn2_2);
        btn2_2.setOnClickListener(this);
        buttons.add(btn2_2);

        btn2_3 = view.findViewById(R.id.btn2_3);
        btn2_3.setOnClickListener(this);
        buttons.add(btn2_3);

        btn2_4 = view.findViewById(R.id.btn2_4);
        btn2_4.setOnClickListener(this);
        buttons.add(btn2_4);

        btn2_5 = view.findViewById(R.id.btn2_5);
        btn2_5.setOnClickListener(this);
        buttons.add(btn2_5);


        btn2_6 = view.findViewById(R.id.btn2_6);
        btn2_6.setOnClickListener(this);
        buttons.add(btn2_6);


        btn2_7 = view.findViewById(R.id.btn2_7);
        btn2_7.setOnClickListener(this);
        buttons.add(btn2_7);

        btn2_8 = view.findViewById(R.id.btn2_8);
        btn2_8.setOnClickListener(this);
        buttons.add(btn2_8);


        btn2_9 = view.findViewById(R.id.btn2_9);
        btn2_9.setOnClickListener(this);
        buttons.add(btn2_9);


        btn2_10 = view.findViewById(R.id.btn2_10);
        btn2_10.setOnClickListener(this);
        buttons.add(btn2_10);

        btn3_1 = view.findViewById(R.id.btn3_1);
        btn3_1.setOnClickListener(this);
        buttons.add(btn3_1);

        btn3_2 = view.findViewById(R.id.btn3_2);
        btn3_2.setOnClickListener(this);
        buttons.add(btn3_2);

        btn3_3 = view.findViewById(R.id.btn3_3);
        btn3_3.setOnClickListener(this);
        buttons.add(btn3_3);

        btn3_4 = view.findViewById(R.id.btn3_4);
        btn3_4.setOnClickListener(this);
        buttons.add(btn3_4);

        btn3_5 = view.findViewById(R.id.btn3_5);
        btn3_5.setOnClickListener(this);
        buttons.add(btn3_5);

        btn3_6 = view.findViewById(R.id.btn3_6);
        btn3_6.setOnClickListener(this);
        buttons.add(btn3_6);

        btn3_7 = view.findViewById(R.id.btn3_7);
        btn3_7.setOnClickListener(this);
        buttons.add(btn3_7);


        btn3_8 = view.findViewById(R.id.btn3_8);
        btn3_8.setOnClickListener(this);
        buttons.add(btn3_8);


        btn3_9 = view.findViewById(R.id.btn3_9);
        btn3_9.setOnClickListener(this);
        buttons.add(btn3_9);


        btn3_10 = view.findViewById(R.id.btn3_10);
        btn3_10.setOnClickListener(this);
        buttons.add(btn3_10);


        btn4_1 = view.findViewById(R.id.btn4_1);
        btn4_1.setOnClickListener(this);
        buttons.add(btn4_1);

        btn4_2 = view.findViewById(R.id.btn4_2);
        btn4_2.setOnClickListener(this);
        buttons.add(btn4_2);


        btn4_3 = view.findViewById(R.id.btn4_3);
        btn4_3.setOnClickListener(this);
        buttons.add(btn4_3);


        btn4_4 = view.findViewById(R.id.btn4_4);
        btn4_4.setOnClickListener(this);
        buttons.add(btn4_4);


        btn4_5 = view.findViewById(R.id.btn4_5);
        btn4_5.setOnClickListener(this);
        buttons.add(btn4_5);


        btn4_6 = view.findViewById(R.id.btn4_6);
        btn4_6.setOnClickListener(this);
        buttons.add(btn4_6);


        btn4_7 = view.findViewById(R.id.btn4_7);
        btn4_7.setOnClickListener(this);
        buttons.add(btn4_7);


        btn4_8 = view.findViewById(R.id.btn4_8);
        btn4_8.setOnClickListener(this);
        buttons.add(btn4_8);


        btn4_9 = view.findViewById(R.id.btn4_9);
        btn4_9.setOnClickListener(this);
        buttons.add(btn4_9);
    }


    public void playGame() {

        String text = getArguments().getString("category");


        mediaPlayerGame = MediaPlayer.create(getActivity(), R.raw.background_sound);
        if (soundOn.getVisibility() == View.GONE) {
            mediaPlayerGame.pause();
        }
        if (!mediaPlayerGame.isPlaying()) {
            mediaPlayerGame.start();
            mediaPlayerGame.setLooping(true);
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            newToken = task.getResult().getToken();
                            Cursor cursor1 = mDb.rawQuery("select user.Id from user where user.token= " + "'" + newToken + "'", null);
                            cursor1.moveToFirst();
                            while (!cursor1.isAfterLast()) {
                                int Sc = Integer.parseInt(cursor1.getString(0));
                                usId = Sc;
                                cursor1.moveToNext();
                            }
                            cursor1.close();


                            ArrayList<String> repeatedWord = new ArrayList<>();
                            Cursor c = mDb.rawQuery("SELECT repeatWord.repeat_word FROM repeatWord INNER JOIN categories on " +
                                            "categories.id=repeatWord.category_id where categories.name=" + "'" + text + "' " +
                                            "AND repeatWord.user_id=" + "'" + usId + "'",
                                    null);
                            c.moveToFirst();

//                            for (int i = 0; i < buttons.size(); i++) {
//                                if (!buttons.get(i).isEnabled()) {
//                                    isClicked = true;
//                                    break;
//                                }
//                            }

                            while (!c.isAfterLast()) {

                                repeatedWord.add(c.getString(0));

                                c.moveToNext();
                            }
                            c.close();


                            repeatedCount = repeatedWord.size();

                            System.out.println("size2 22222222222222222222222222222222222222222    //     " + repeatedCount);
                            System.out.println("WordSize 22222222222222222222222222222222222222222    //     " + wordSize);


                            if (repeatedCount == wordSize) {
                                if (timer != null) {
                                    timer.cancel();
                                    timer = null;
                                }
                                Fragment someFragment = new HomePage();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.mainId, someFragment);
                                transaction.commit();
                                return;
                            }


                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }

                            set = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.flip_stop);
                            set.setTarget(binding.leftHand);
                            set.start();

                            set2 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.flip_stop);
                            set2.setTarget(binding.rightHand);
                            set2.start();

                            lives = 6;

                            binding.categoryName.setText(text);

                            boolean repeatedWords = true;

                            Cursor cursor = mDb.rawQuery("SELECT words.* from words INNER JOIN categories on categories.id = words.category_id " +
                                    "WHERE categories.name =  " + "'" + text + "' order by random() LIMIT 1", null);
                            cursor.moveToFirst();


                            int l = 0;
                            while (repeatedWord.contains(cursor.getString(1))) {
                                System.out.println(cursor.getString(1));
                                cursor = mDb.rawQuery("SELECT words.* from words INNER JOIN categories on categories.id = words.category_id " +
                                        "WHERE categories.name =  " + "'" + text + "' order by random() LIMIT 1", null);
                                cursor.moveToFirst();
                                l++;

                                if (l == wordSize) {
                                    break;
                                }
                            }

                            word = new Word();


                            while (!cursor.isAfterLast()) {
                                word.setId(Integer.parseInt(cursor.getString(0)));
                                word.setWords(cursor.getString(1));
                                word.setCategoryId(Integer.parseInt(cursor.getString(2)));
                                word.setHintText(cursor.getString(3));
                                word.setHintImg(cursor.getString(4));
                                word.setHintAudio(cursor.getString(5));
                                cursor.moveToNext();
                            }

                            cursor.close();


//                            if (!isClicked) {
//                                ContentValues contentValues = new ContentValues();
//                                contentValues.put("user_id", usId);
//                                contentValues.put("category_id", word.getCategoryId());
//                                contentValues.put("repeat_word", word.getWords());
//
//                                mDb.insert("repeatWord", null, contentValues);
//                            }

                            binding.backButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (timer != null) {
                                        timer.cancel();
                                        timer = null;
                                    }
                                    if (soundOn.getVisibility() == View.GONE) {
                                        soundOn.setVisibility(View.GONE);
                                    } else {
                                        soundOn.setVisibility(View.VISIBLE);
                                    }

                                    if (mediaPlayerGame.isPlaying()) {
                                        mediaPlayerGame.pause();
                                    }
                                    if (isClicked) {
                                        dialog = new Dialog(getActivity());
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.cutom_dialog_view);
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        ImageView dialogButton = (ImageView) dialog.findViewById(R.id.dialog_exit_btn);
                                        ConstraintLayout constraintLayout = dialog.findViewById(R.id.choose_lay);
                                        View chooseView = LayoutInflater.from(getActivity()).inflate(R.layout.cutom_dialog_view, constraintLayout, false);

                                        TextView yes_btn = (TextView) dialog.findViewById(R.id.reply_yes);
                                        Button no_btn = (Button) dialog.findViewById(R.id.reply_no);

                                        yes_btn.setOnClickListener(v1 -> {
                                            dialog.dismiss();
                                            Fragment someFragment = new HomePage();
                                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                            transaction.replace(R.id.mainId, someFragment);
                                            transaction.commit();

                                        });

                                        no_btn.setOnClickListener(v2 -> {
                                            dialog.dismiss();
                                            if (!mediaPlayerGame.isPlaying()) {
                                                mediaPlayerGame.start();
                                                mediaPlayerGame.setLooping(true);
                                            }
                                        });

                                        constraintLayout.addView(chooseView);

                                        dialogButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();

                                            }
                                        });

                                        dialog.show();
                                    } else {
                                        Fragment someFragment = new HomePage();
                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                        transaction.replace(R.id.mainId, someFragment);
                                        transaction.commit();
                                    }
                                }
                            });


                            binding.bulbB.setOnClickListener(v -> {
                                dialog = new Dialog(getActivity());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.new_post_save_dialog);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                                if (word.getHintText() != null) {
                                    TextView textView = new TextView(getActivity());
                                    textView.setText(word.getHintText());
                                    textView.setTextColor(Color.WHITE);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    textView.setPadding(50, 50, 50, 50);
                                    textView.setTextSize(15);
                                    textView.setBackgroundResource(R.drawable.image_popup);
                                    textView.setGravity(Gravity.CENTER);
                                    textView.setLayoutParams(params);
                                    textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
                                    ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
                                    constraintLayout.addView(textView);
                                } else if (word.getHintImg() != null) {
                                    String imageName = word.getHintImg();
                                    imageView = new ImageView(getActivity());
                                    getImg();
                                } else if (word.getHintAudio() != null) {
                                    ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);


                                    View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.audio_layout, constraintLayout, false);
                                    ImageView audioPlay = (ImageView) layout2.findViewById(R.id.audioPlayyy);
                                    ImageView pausePlay = (ImageView) layout2.findViewById(R.id.pausePlayyy);
                                    ImageView audiImage = (ImageView) layout2.findViewById(R.id.audiImg);
                                    audioTime = (TextView) layout2.findViewById(R.id.audioTime);
                                    audioPlayer = MediaPlayer.create(getActivity(), R.raw.beach_03);

                                    duration = audioPlayer.getDuration();
                                    String time = String.format("%02d:%02d",
                                            TimeUnit.MILLISECONDS.toMinutes(duration),
                                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                                    );
                                    audioTime.setText(time);


                                    audioPlay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            audioPlay.setVisibility(View.GONE);
                                            pausePlay.setVisibility(View.VISIBLE);
                                            if (!audioPlayer.isPlaying()) {
                                                audioPlayer.start();
                                            }

                                            long endTime = ((TimeUnit.MILLISECONDS.toSeconds(duration)) + 1) * 1000;
                                            System.out.println("EndTime     " + endTime);
                                            new CountDownTimer(endTime, 1000) {

                                                public void onTick(long millisUntilFinished) {
                                                    Log.d("log", String.valueOf(millisUntilFinished / 1000));
                                                    mHandler.post(updateUI);
                                                }

                                                public void onFinish() {
                                                    audioPlay.setVisibility(View.VISIBLE);
                                                    pausePlay.setVisibility(View.GONE);
                                                    audioTime.setText(time);

                                                }
                                            }.start();

                                        }
                                    });

                                    pausePlay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            audioPlay.setVisibility(View.VISIBLE);
                                            pausePlay.setVisibility(View.GONE);
                                            if (audioPlayer.isPlaying()) {
                                                audioPlayer.pause();
                                            }
                                        }
                                    });

                                    constraintLayout.addView(layout2);

                                } else {

                                }


                                ImageView dialogButton = (ImageView) dialog.findViewById(R.id.dialog_exit_btn);

                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();

                                MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.small_hit_in_a_game);
                                if (soundOn.getVisibility() == View.GONE) {
                                    mediaPlayer.pause();
                                }
                                mediaPlayer.start();

                                binding.bulbB.setVisibility(View.GONE);
                                binding.bulbB2.setVisibility(View.VISIBLE);
                                binding.bulbB.setEnabled(false);

                            });

                            currWord = word.getWords();

                            str = currWord;
                            System.out.println("************************************" + currWord);


                            int isSpace = -1;


                            StringBuffer stringBuffer = new StringBuffer(str);
                            for (int i = 0; i < stringBuffer.length(); i++) {
                                if (i + 1 <= stringBuffer.length() - 1 && stringBuffer.charAt(i) == 'Ո' && stringBuffer.charAt(i + 1) == 'ւ') {
                                    stringBuffer.deleteCharAt(i);
                                }
                            }
                            str = String.valueOf(stringBuffer);
                            currWord = str;
                            isSpace = currWord.indexOf(" ");


                            charsWords = new TextView[str.length()];
                            for (int i = 0; i < charsWords.length; i++) {
                                charsWords[i] = new TextView(getActivity());
                                System.out.println(charsWords[i].getText());
                                charsWords[i].setText(" - ");
                                charsWords[i].setBackgroundResource(R.drawable.for_single_letter);
                                charsWords[i].setGravity(Gravity.CENTER);
                                charsWords[i].setPadding(10, 10, 10, 10);
                                charsWords[i].setTextSize(20);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(0, 0, 5, 0);
                                charsWords[i].setLayoutParams(params);
                                charsWords[i].setTextColor(Color.BLACK);

                                if (i != isSpace) {
                                    if (i < isSpace || isSpace == -1 && i < 11) {
                                        binding.wordLinLay.addView(charsWords[i]);
                                    } else {
                                        binding.wordLinLay2.addView(charsWords[i]);
                                    }
                                }

                                if (currWord.equals("ՍԱՅԱԹ ՆՈՎԱ") || currWord.equals("ՆԱՐ ԴՈՍ")) {
                                    binding.lineLayout.setVisibility(View.VISIBLE);
                                }


                            }

                        }
                    }
                });


        FirebaseInstanceId.getInstance().

                getInstanceId()
                .

                        addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (task.isSuccessful()) {
                                    newToken = task.getResult().getToken();
                                    Cursor cursor1 = mDb.rawQuery("select user.score from user where user.token= " + "'" + newToken + "'", null);
                                    cursor1.moveToFirst();
                                    while (!cursor1.isAfterLast()) {
                                        String Sc = cursor1.getString(0);
                                        cursor1.moveToNext();
                                        cursor1.close();
                                        binding.scoreText.setText(Sc);
                                    }
                                }
                            }
                        });


    }


    private final Runnable updateUI = new Runnable() {
        public void run() {
            try {
                audioTime.setText("0" + (audioPlayer.getCurrentPosition() / audioPlayer.getDuration()) * 100 + ":0" + (audioPlayer.getDuration() - audioPlayer.getCurrentPosition()) / 1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onClick(View v) {
        if (!isClicked) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", usId);
            contentValues.put("category_id", word.getCategoryId());
            contentValues.put("repeat_word", word.getWords());
            mDb.insert("repeatWord", null, contentValues);
            isClicked = true;
        }

        Button button = (Button) v;

        if (button.getText().toString().toUpperCase().equals("Ու".toUpperCase()) && str.contains("ւ")) {
            while (str.contains("ւ")) {
                int index = 0;
                index = str.indexOf("ւ");
                charsWords[index].setText(button.getText().toString());
                str = replaceAt(index, str);
                if (charsWords[index].getText().equals("Լ")) {
                    charsWords[index].setPadding(20, 10, 20, 10);
                }
            }
            button.setBackgroundResource(R.drawable.right_letter);
            mediaPlayer = MediaPlayer.create(getActivity(), R.raw.unlock_game_notification);
            if (soundOn.getVisibility() == View.GONE) {
                mediaPlayer.pause();
            }
            mediaPlayer.start();
            button.setEnabled(false);
            checkWinner(str);
        } else {
            if (!str.toUpperCase().contains(button.getText().toString().toUpperCase()) && lives > 0) {
                button.setBackgroundResource(R.drawable.wrong_letter);
                button.setEnabled(false);
                lives--;
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.retro_block);
                if (soundOn.getVisibility() == View.GONE) {
                    mediaPlayer.pause();
                }
                mediaPlayer.start();
                checkPart();
                checkGameOver();
            } else {
                while (str.toUpperCase().contains(button.getText().toString().toUpperCase())) {
                    int index = 0;
                    index = str.indexOf(button.getText().toString());
                    charsWords[index].setText(button.getText().toString());
                    str = replaceAt(index, str);
                    if (charsWords[index].getText().equals("Լ")) {
                        charsWords[index].setPadding(20, 10, 20, 10);
                    }

                }
                button.setBackgroundResource(R.drawable.right_letter);
                button.setEnabled(false);
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.unlock_game_notification);
                if (soundOn.getVisibility() == View.GONE) {
                    mediaPlayer.pause();
                }
                mediaPlayer.start();
                checkWinner(str);
            }
        }

    }

    public void checkPart() {
        if (currPart < bodyParts.length) {
            bodyParts[currPart].setVisibility(View.VISIBLE);
            currPart++;
        }
    }

    public String replaceAt(int index, String str) {
        String s = "";
        for (int i = 0; i < str.length(); i++) {
            if (i == index) {
                s += " ";
            } else {
                s += str.charAt(i);
            }
        }
        return s;
    }


    public void checkGameOver() {
        if (lives == 0) {

            bodyParts[1].startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.body_anim));
            bodyParts[2].startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hand_anim));
            bodyParts[3].startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hand_anim));
            bodyParts[4].startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.up_down));
            bodyParts[5].startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.up_down));

            set = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.flip_stop);
            set.setTarget(binding.leftHand);
            set.start();
            set2 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.flip_stop);
            set2.setTarget(binding.rightHand);
            set2.start();

            MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.aww_sound);
            if (soundOn.getVisibility() == View.GONE) {
                mediaPlayer.pause();
            }
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }


            for (Button button : buttons) {
                button.setEnabled(false);
            }

            for (int i = 0; i < charsWords.length; i++) {
                if (String.valueOf(currWord.charAt(i)).equals("ւ")) {
                    charsWords[i].setText("Ու");
                } else {
                    charsWords[i].setText(String.valueOf(currWord.charAt(i)));
                }
            }

            binding.bulbB.setEnabled(false);
            binding.replyBtn.setVisibility(View.VISIBLE);
            binding.homeBtn.setVisibility(View.VISIBLE);

            binding.homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayerGame.isPlaying()) {
                        mediaPlayerGame.pause();
                    }
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    binding.wordLinLay.removeAllViews();
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    HomePage homePage = new HomePage();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.mainId, homePage);
                    fragmentTransaction.commit();
                }
            });

            binding.replyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayerGame.isPlaying()) {
                        mediaPlayerGame.pause();
                    }
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }

                    binding.wordLinLay.removeAllViews();
                    binding.wordLinLay2.removeAllViews();
                    for (Button button : buttons) {
                        button.setBackgroundResource(R.drawable.letter_background);
                        button.setEnabled(true);
                    }
                    playGame();

                    binding.bulbB.setVisibility(View.VISIBLE);
                    binding.bulbB.setEnabled(true);
                    binding.bulbB2.setVisibility(View.GONE);

                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    for (int i = 0; i < bodyParts.length; i++) {
                        bodyParts[i].clearAnimation();
                    }

                    bodyParts[0].setVisibility(View.INVISIBLE);
                    bodyParts[1].setVisibility(View.INVISIBLE);
                    bodyParts[2].setVisibility(View.INVISIBLE);
                    bodyParts[3].setVisibility(View.INVISIBLE);
                    bodyParts[4].setVisibility(View.INVISIBLE);
                    bodyParts[5].setVisibility(View.INVISIBLE);
                    currPart = 0;
                    binding.replyBtn.setVisibility(View.INVISIBLE);
                    binding.homeBtn.setVisibility(View.INVISIBLE);


                }
            });
        }

    }


    public void checkWinner(String s) {


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            newToken = task.getResult().getToken();
                            ObjectAnimator animator = null;
                            ObjectAnimator animator2 = null;
                            String str = newToken;

                            if (s.trim().length() == 0) {
                                Cursor cursor = mDb.rawQuery("select user.id from user where user.token= " + "'" + str + "'", null);
                                cursor.moveToFirst();
                                while (!cursor.isAfterLast()) {
                                    usId = Integer.parseInt(cursor.getString(0));
                                    cursor.moveToNext();
                                }
                                cursor.close();

                                Cursor cursor1 = mDb.rawQuery("select user.score from user where user.token= " + "'" + newToken + "'", null);
                                cursor1.moveToFirst();
                                int Sc = Integer.parseInt(cursor1.getString(0));
                                cursor1.close();

                                int scoree = Sc;
                                scoree++;

                                ContentValues contentValues1 = new ContentValues();
                                contentValues1.put("score", scoree);

                                mDb.update("user", contentValues1, "token = ?", new String[]{newToken});

                                bodyParts[0].setVisibility(View.VISIBLE);
                                bodyParts[1].setVisibility(View.VISIBLE);
                                bodyParts[2].setVisibility(View.VISIBLE);
                                bodyParts[3].setVisibility(View.VISIBLE);
                                bodyParts[4].setVisibility(View.VISIBLE);
                                bodyParts[5].setVisibility(View.VISIBLE);


                                for (int i = 0; i < bodyParts.length; i++) {
                                    animator = ObjectAnimator.ofFloat(bodyParts[i], "translationY", 115f);
                                    animator.setDuration(500);
                                    animator.start();
                                }

                                animator2 = ObjectAnimator.ofFloat(binding.leftHand, "translationY", 80f);
                                animator2.setDuration(500);
                                animator2.start();

                                animator2 = ObjectAnimator.ofFloat(binding.rightHand, "translationY", 80f);
                                animator2.setDuration(500);
                                animator2.start();


                                set = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.flip);
                                set.setTarget(binding.leftHand);
                                set.start();

                                set2 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.flip);
                                set2.setTarget(binding.rightHand);
                                set2.start();


                                binding.bulbB.setEnabled(false);

                                timer = new Timer();
                                myTimerTask = new MyTimerTask();
                                timer.schedule(myTimerTask, 2000);


                                for (int i = 0; i < bodyParts.length; i++) {
                                    bodyParts[i].animate().translationY(-30);
                                    bodyParts[i].animate().translationX(-90);
                                }

                                TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 150);
                                animation.setFillAfter(false);
                                binding.leftHand.startAnimation(animation);
                                binding.rightHand.startAnimation(animation);


                                bodyParts[2].startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake_left));
                                bodyParts[3].startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake_right));


                                MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.completion_of_a_level);
                                if (soundOn.getVisibility() == View.GONE) {
                                    mediaPlayer.pause();
                                }
                                mediaPlayer.start();

                                currPart = 0;

                            }
                        }
                    }
                });


    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    bodyParts[2].clearAnimation();
                    bodyParts[3].clearAnimation();

                    binding.wordLinLay.removeAllViews();
                    binding.wordLinLay2.removeAllViews();
                    for (Button button : buttons) {
                        button.setBackgroundResource(R.drawable.letter_background);
                        button.setEnabled(true);
                    }

                    bodyParts[0].setVisibility(View.INVISIBLE);
                    bodyParts[1].setVisibility(View.INVISIBLE);
                    bodyParts[2].setVisibility(View.INVISIBLE);
                    bodyParts[3].setVisibility(View.INVISIBLE);
                    bodyParts[4].setVisibility(View.INVISIBLE);
                    bodyParts[5].setVisibility(View.INVISIBLE);

                    for (int i = 0; i < bodyParts.length; i++) {
                        bodyParts[i].setVisibility(View.INVISIBLE);
                    }


                    for (int i = 0; i < bodyParts.length; i++) {
                        bodyParts[i].animate().translationY(0);
                        bodyParts[i].animate().translationX(0);
                    }


                    timer.cancel();
                    timer = null;


                    binding.bulbB2.setVisibility(View.GONE);
                    binding.bulbB.setVisibility(View.VISIBLE);
                    binding.bulbB.setEnabled(true);

                    playGame();
                }


            });
        }
    }

    public void getImg() {


        if (currWord.equals("ԴւԲԱՅ")) {
            imageView.setImageResource(R.drawable.dubai);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԼՈՆԴՈՆ")) {
            imageView.setImageResource(R.drawable.london);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՓԱՐԻԶ")) {
            imageView.setImageResource(R.drawable.paris);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՄՈՍԿՎԱ")) {
            imageView.setImageResource(R.drawable.moscow);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՍևԱԶԳԵՍՏ ՄԱՐԴԻԿ")) {
            imageView.setImageResource(R.drawable.men_in_black);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՍԱՐԴ ՄԱՐԴԸ")) {
            imageView.setImageResource(R.drawable.spider_man);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԲԵԹՄԵՆ")) {
            imageView.setImageResource(R.drawable.batman);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՎՐԻԺԱՌւՆԵՐԸ")) {
            imageView.setImageResource(R.drawable.vrijaruner);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՔԱՅԼ ՁԻՈՎ")) {
            imageView.setImageResource(R.drawable.qayl_dziov);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՍւՊԵՐ ՄԱՄԱ")) {
            imageView.setImageResource(R.drawable.super_mama);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԻՔՍ ՄԱՐԴԻԿ")) {
            imageView.setImageResource(R.drawable.x_men);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՖՈՐՍԱԺ")) {
            imageView.setImageResource(R.drawable.fast_and_furious);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԹՈՐ")) {
            imageView.setImageResource(R.drawable.thor);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՏՐԱՆՍՖՈՐՄԵՐՆԵՐԸ")) {
            imageView.setImageResource(R.drawable.transformers);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՔԱՂՑԱԾ ԽԱՂԵՐ")) {
            imageView.setImageResource(R.drawable.hunger_games);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԴԻՎԵՐԳԵՆՏ")) {
            imageView.setImageResource(R.drawable.divergent);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՄԹՆՇԱՂ")) {
            imageView.setImageResource(R.drawable.twilight);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԱԼԼԱԴԻՆ")) {
            imageView.setImageResource(R.drawable.alladin);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՊԻՆՈԿԻՈ")) {
            imageView.setImageResource(R.drawable.pinokio);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԳՐԻՆՉ")) {
            imageView.setImageResource(R.drawable.grinch);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՄւՄԻԱՆ")) {
            imageView.setImageResource(R.drawable.mumian);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՑԵՆՏւՐԻՈՆ")) {
            imageView.setImageResource(R.drawable.centurion);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՆԱՀԱՊԵՏ")) {
            imageView.setImageResource(R.drawable.nahapet);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԽԱԹԱԲԱԼԱ")) {
            imageView.setImageResource(R.drawable.khatabala);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԵՌԱՆԿՅւՆԻ")) {
            imageView.setImageResource(R.drawable.erankyuni);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԷՐԱԳՈՆ")) {
            imageView.setImageResource(R.drawable.eragon1);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԱՐԹւՐ ԱՐՔԱՆ")) {
            imageView.setImageResource(R.drawable.arthur_king);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԽԱՂԱԼԻՔ")) {
            imageView.setImageResource(R.drawable.xaxaliq);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՊԵԼԵ")) {
            imageView.setImageResource(R.drawable.pele);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՄՅւԼԼԵՐ")) {
            imageView.setImageResource(R.drawable.muller);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՄԲԱՊԵ")) {
            imageView.setImageResource(R.drawable.mbape);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՆԵՅՄԱՐ")) {
            imageView.setImageResource(R.drawable.neymar);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԱԼԱԲԱ")) {
            imageView.setImageResource(R.drawable.alaba_al);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՌՈՅՍ")) {
            imageView.setImageResource(R.drawable.reus);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԻԲՐԱՀԻՄՈՎԻՉ")) {
            imageView.setImageResource(R.drawable.ibrahimovich);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԿԱւՏԻՆՅՈ")) {
            imageView.setImageResource(R.drawable.cautinho);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԲԵՆԶԵՄԱ")) {
            imageView.setImageResource(R.drawable.benzema);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԲՈԱՏԵՆԳ")) {
            imageView.setImageResource(R.drawable.boateng);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԿԻՄԻԽ")) {
            imageView.setImageResource(R.drawable.joshua_kimmich);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՀՅւՄԵԼՍ")) {
            imageView.setImageResource(R.drawable.hummels);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՏՈՆԻ ԿՐՈՍ")) {
            imageView.setImageResource(R.drawable.toni_kroos);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՖԻԼԻՊ ԼԱՀՄ")) {
            imageView.setImageResource(R.drawable.lahm);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՍԱՆԵ")) {
            imageView.setImageResource(R.drawable.leroy_sane);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՌԻԲԵՐԻ")) {
            imageView.setImageResource(R.drawable.riberi);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՌՈԲԵՆ")) {
            imageView.setImageResource(R.drawable.roben);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԳՐԻԶՄԱՆ")) {
            imageView.setImageResource(R.drawable.griezman);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԿԱԿԱ")) {
            imageView.setImageResource(R.drawable.kaka);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՆԱՍՐԻ")) {
            imageView.setImageResource(R.drawable.nasri);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԷԴԵՆ ԱԶԱՐ")) {
            imageView.setImageResource(R.drawable.eden_hazard);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՇՅւՌԼԵ")) {
            imageView.setImageResource(R.drawable.schurrle);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԳՈՄԵՍ")) {
            imageView.setImageResource(R.drawable.mario_gomez);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԿԱՎԱՆԻ")) {
            imageView.setImageResource(R.drawable.cavani);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԴՐԱՔՍԼԵՐ")) {
            imageView.setImageResource(R.drawable.draxler);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՊՈԳԲԱ")) {
            imageView.setImageResource(R.drawable.pogba);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԲԵՅԼ")) {
            imageView.setImageResource(R.drawable.bale);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԱԼԲԱ")) {
            imageView.setImageResource(R.drawable.jordi_alba);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՌԵՇՖՈՐԴ")) {
            imageView.setImageResource(R.drawable.marcus_rashford);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՉԱՎԻ")) {
            imageView.setImageResource(R.drawable.xavi);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԷՐՆԱՆԴԵԶ")) {
            imageView.setImageResource(R.drawable.hernandez);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՍԻԼՎԱ")) {
            imageView.setImageResource(R.drawable.thiago_silv);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԱԳւԵՐՈ")) {
            imageView.setImageResource(R.drawable.aguero);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԿԱՆՏԵ")) {
            imageView.setImageResource(R.drawable.ngolo_kante);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՍՏԵՐԼԻՆԳ")) {
            imageView.setImageResource(R.drawable.raheem_sterling);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՏևԵՍ")) {
            imageView.setImageResource(R.drawable.tevez);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԴԻԲԱԼԱ")) {
            imageView.setImageResource(R.drawable.dybala);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՀԱՐԻ ՔԵՅՆ")) {
            imageView.setImageResource(R.drawable.hary_kane);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՏՈՌԵՍ")) {
            imageView.setImageResource(R.drawable.torez);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԻԳւԱՅԻՆ")) {
            imageView.setImageResource(R.drawable.higuain);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՍԱԼԱՀ")) {
            imageView.setImageResource(R.drawable.salah);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԼՅՈՐԻՍ")) {
            imageView.setImageResource(R.drawable.ugo_lorris);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՕԲԱՄԵՅԱՆԳ")) {
            imageView.setImageResource(R.drawable.aubameyang);
            imageView.setPadding(30, 30, 30, 30);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՌւՆԻ")) {
            imageView.setImageResource(R.drawable.wayne_rooney);
            imageView.setPadding(38, 39, 38, 39);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԷՐԻԿՍԵՆ")) {
            imageView.setImageResource(R.drawable.christian_eriksen);
            imageView.setPadding(55, 32, 55, 32);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՀՈՆՔ")) {
            imageView.setImageResource(R.drawable.eyebrow);
            imageView.setPadding(55, 32, 55, 32);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԹԱՐԹԻՉ")) {
            imageView.setImageResource(R.drawable.eyelash);
            imageView.setPadding(55, 32, 55, 32);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԱՏԱՄ")) {
            imageView.setImageResource(R.drawable.tooth);
            imageView.setPadding(55, 32, 55, 32);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("Թև")) {
            imageView.setImageResource(R.drawable.tev);
            imageView.setPadding(55, 32, 55, 32);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ՄԱԶ")) {
            imageView.setImageResource(R.drawable.mazer);
            imageView.setPadding(55, 32, 55, 32);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԱՂԻՔ")) {
            imageView.setImageResource(R.drawable.timthumb);
            imageView.setPadding(55, 32, 55, 32);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԾՆՈՏ")) {
            imageView.setImageResource(R.drawable.tsnot);
            imageView.setPadding(55, 32, 55, 32);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԱՆՐԱԿ")) {
            imageView.setImageResource(R.drawable.anrak);
            imageView.setPadding(55, 32, 55, 32);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        } else if (currWord.equals("ԹԻԱԿ")) {
            imageView.setImageResource(R.drawable.tiak);
            imageView.setPadding(55, 32, 55, 32);
            imageView.setBackgroundResource(R.drawable.image_popup_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ConstraintLayout constraintLayout = dialog.findViewById(R.id.child);
            constraintLayout.addView(imageView);
        }


    }


}


