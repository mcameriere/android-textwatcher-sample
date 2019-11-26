package com.example.structuredcommunication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = findViewById(R.id.editText);

        editText.addTextChangedListener(new TextWatcher() {

            String beforeText = "";

            @Override
            public void beforeTextChanged(CharSequence beforeCharSequence, int start, int count, int after) {
                beforeText = beforeCharSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence afterCharSequence, int start, int before, int count) {

                if (afterCharSequence == null) return;

                int initialCursorPosition = start + before;

                int numOfDigitsToRightOfCursor = getNumberOfDigits(
                        beforeText.substring(initialCursorPosition)
                );

                String newCommunication;

                String s = computeContentAfterDeletingSlash(beforeText, afterCharSequence.toString());
                if (!s.equals("")) {
                    newCommunication = s;
                } else {
                    newCommunication = formatCommunication(afterCharSequence.toString());
                }

                editText.removeTextChangedListener(this);

                editText.setText(newCommunication);
                editText.setSelection(getNewCursorPosition(numOfDigitsToRightOfCursor, newCommunication));

                editText.addTextChangedListener(this);
            }


            @Override
            public void afterTextChanged(Editable editable) {
            }

            private int getNumberOfDigits(String text) {
                int count = 0;
                for (int i = 0; i < text.length(); i++) {
                    if (text.substring(i, i + 1).matches("^\\d{1}$")) count++;
                }
                return count;
            }

            private int getNewCursorPosition(int numOfDigitsToRightOfCursor, String numberString) {
                int position = 0;
                    int c = numOfDigitsToRightOfCursor;
                for (int i = numberString.length() - 1; i >= 0; i--) {
                    if (c == 0)
                        break;

                    if (numberString.substring(i, i + 1).matches("^\\d$"))
                        c--;
                    position++;
                }
                return numberString.length() - position;
            }

            private String formatCommunication(String text) {
                String result = removeFormatting(text);
                return addSlashes(result);
            }

            private String computeContentAfterDeletingSlash(String beforeText, String afterText) {

                // 14 chars -> 13 chars : deleting first slash

                if (beforeText.matches("^\\d{3}/\\d{4}/\\d{5}$") && afterText.matches("^\\d{7}/\\d{5}$"))
                    return formatCommunication(afterText.substring(0, 2) + afterText.substring(3, 13));

                // 14 chars -> 13 chars : deleting second slash

                if (beforeText.matches("^\\d{3}/\\d{4}/\\d{5}") && afterText.matches("^\\d{3}/\\d{9}$"))
                    return formatCommunication(afterText.substring(0, 7) + afterText.substring(8, 13));

                // 13 chars -> 12 chars : deleting first slash

                if (beforeText.matches("^\\d{3}/\\d{4}/\\d{4}") && afterText.matches("^\\d{7}/\\d{4}$"))
                    return formatCommunication(afterText.substring(0, 2) + afterText.substring(3, 12));

                // 13 chars -> 12 chars : deleting second slash

                if (beforeText.matches("^\\d{3}/\\d{4}/\\d{4}") && afterText.matches("^\\d{3}/\\d{8}$"))
                    return formatCommunication(afterText.substring(0, 7) + afterText.substring(8, 12));

                // 12 chars -> 11 chars : deleting first slash

                if (beforeText.matches("^\\d{3}/\\d{4}/\\d{3}$") && afterText.matches("^\\d{7}/\\d{3}$"))
                    return formatCommunication(afterText.substring(0, 2) + afterText.substring(3, 11));

                // 12 chars -> 11 chars : deleting second slash

                if (beforeText.matches("^\\d{3}/\\d{4}/\\d{3}$") && afterText.matches("^\\d{3}/\\d{7}$"))
                    return formatCommunication(afterText.substring(0, 7) + afterText.substring(8, 11));

                // 11 chars -> 10 chars : deleting first slash

                if (beforeText.matches("^\\d{3}/\\d{4}/\\d{2}$") && afterText.matches("^\\d{7}/\\d{2}$"))
                    return formatCommunication(afterText.substring(0, 2) + afterText.substring(3, 10));

                // 11 chars -> 10 chars : deleting second slash

                if (beforeText.matches("^\\d{3}/\\d{4}/\\d{2}$") && afterText.matches("^\\d{3}/\\d{6}$"))
                    return formatCommunication(afterText.substring(0, 7) + afterText.substring(8, 10));

                // 10 chars -> 9 chars : deleting first slash

                if (beforeText.matches("^\\d{3}/\\d{4}/\\d$") && afterText.matches("^\\d{7}/\\d$"))
                    return formatCommunication(afterText.substring(0, 2) + afterText.substring(3, 9));

                // 10 chars -> 9 chars : deleting second slash

                if (beforeText.matches("^\\d{3}/\\d{4}/\\d$") && afterText.matches("^\\d{3}/\\d{5}$"))
                    return formatCommunication(afterText.substring(0, 7) + afterText.substring(8, 9));

                // 8 chars -> 7 chars : deleting first slash

                if (beforeText.matches("^\\d{3}/\\d{4}$") && afterText.matches("^\\d{7}$"))
                    return formatCommunication(afterText.substring(0, 2) + afterText.substring(3, 7));

                // 7 chars -> 6 chars : deleting first slash

                if (beforeText.matches("^\\d{3}/\\d{3}$") && afterText.matches("^\\d{6}$"))
                    return formatCommunication(afterText.substring(0, 2) + afterText.substring(3, 6));

                // 6 chars -> 5 chars : deleting first slash

                if (beforeText.matches("^\\d{3}/\\d{2}$") && afterText.matches("^\\d{5}$"))
                    return formatCommunication(afterText.substring(0, 2) + afterText.substring(3, 5));

                // 5 chars -> 4 chars : deleting first slash

                if (beforeText.matches("^\\d{3}/\\d$") && afterText.matches("^\\d{4}$"))
                    return formatCommunication(afterText.substring(0, 2) + afterText.substring(3, 4));

                return "";
            }

            private String addSlashes(String input) {
                if (input == null) return "";
                String output = removeFormatting(input);

                if (input.length() >= 4 && input.length() <= 7) {
                    output = input.substring(0, 3) + "/" + input.substring(3);
                }

                if (input.length() >= 8) {
                    output =
                            input.substring(0, 3) + "/" + input.substring(3, 7) + "/" + input.substring(7);
                }

                return output;
            }

            private String removeFormatting(String input) {
                if (input == null) return "";
                return input.replaceAll("\\D", "");
            }

        });
    }

}
