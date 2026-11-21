package com.ambition.myfirstapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvCounter;
    private TextView tvSideLabel;
    private TextView tvCardText;
    private Button btnShowAnswer;
    private Button btnPrevious;
    private Button btnNext;
    private Button btnAdd;
    private Button btnEdit;
    private Button btnDelete;

    private final List<Flashcard> flashcards = new ArrayList<>();
    private int currentIndex = 0;
    private boolean showingAnswer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCounter = findViewById(R.id.tvCounter);
        tvSideLabel = findViewById(R.id.tvSideLabel);
        tvCardText = findViewById(R.id.tvCardText);
        btnShowAnswer = findViewById(R.id.btnShowAnswer);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        seedDefaultCards();
        renderCard();

        btnShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashcards.isEmpty()) return;
                showingAnswer = !showingAnswer;
                renderCard();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashcards.isEmpty()) return;
                currentIndex = (currentIndex + 1) % flashcards.size();
                showingAnswer = false;
                renderCard();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashcards.isEmpty()) return;
                currentIndex = (currentIndex - 1 + flashcards.size()) % flashcards.size();
                showingAnswer = false;
                renderCard();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCardDialog(null, -1);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashcards.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No card to edit", Toast.LENGTH_SHORT).show();
                    return;
                }
                showCardDialog(flashcards.get(currentIndex), currentIndex);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashcards.size() <= 1) {
                    Toast.makeText(MainActivity.this, "Keep at least one flashcard", Toast.LENGTH_SHORT).show();
                    return;
                }
                flashcards.remove(currentIndex);
                if (currentIndex >= flashcards.size()) {
                    currentIndex = flashcards.size() - 1;
                }
                showingAnswer = false;
                renderCard();
                Toast.makeText(MainActivity.this, "Flashcard deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seedDefaultCards() {
        flashcards.add(new Flashcard(
                "What is a prime implicant in Boolean algebra?",
                "A product term that cannot be combined with any other term to eliminate a literal."
        ));
        flashcards.add(new Flashcard(
                "What does HTTP stand for?",
                "HyperText Transfer Protocol"
        ));
        flashcards.add(new Flashcard(
                "What is the time complexity of binary search on a sorted array?",
                "O(log n)"
        ));
    }

    private void renderCard() {
        if (flashcards.isEmpty()) {
            tvCounter.setText("Card 0 of 0");
            tvSideLabel.setText("EMPTY");
            tvCardText.setText("Tap Add to create a flashcard.");
            btnShowAnswer.setText("Show Answer");
            return;
        }

        Flashcard card = flashcards.get(currentIndex);
        tvCounter.setText("Card " + (currentIndex + 1) + " of " + flashcards.size());

        if (showingAnswer) {
            tvSideLabel.setText("ANSWER");
            tvCardText.setText(card.getAnswer());
            btnShowAnswer.setText("Show Question");
        } else {
            tvSideLabel.setText("QUESTION");
            tvCardText.setText(card.getQuestion());
            btnShowAnswer.setText("Show Answer");
        }
    }

    private void showCardDialog(final Flashcard existingCard, final int editIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(existingCard == null ? "Add Flashcard" : "Edit Flashcard");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, pad);

        final EditText etQuestion = new EditText(this);
        etQuestion.setHint("Enter question");
        if (existingCard != null) {
            etQuestion.setText(existingCard.getQuestion());
        }

        final EditText etAnswer = new EditText(this);
        etAnswer.setHint("Enter answer");
        if (existingCard != null) {
            etAnswer.setText(existingCard.getAnswer());
        }

        container.addView(etQuestion);
        container.addView(etAnswer);
        builder.setView(container);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String q = etQuestion.getText().toString().trim();
            String a = etAnswer.getText().toString().trim();

            if (q.isEmpty() || a.isEmpty()) {
                Toast.makeText(MainActivity.this, "Both question and answer are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (existingCard == null) {
                flashcards.add(new Flashcard(q, a));
                currentIndex = flashcards.size() - 1;
            } else {
                existingCard.setQuestion(q);
                existingCard.setAnswer(a);
                currentIndex = editIndex;
            }

            showingAnswer = false;
            renderCard();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}