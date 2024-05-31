package com.example.labyrinth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.Locale;

public class ExitFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Вы нашли выход!");
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_exit, null);
        builder.setView(view);
        builder.setPositiveButton("Сохранить и выйти", (dialog, id) -> {
            EditText etName = view.findViewById(R.id.etName);
            String name = etName.getText().toString();
            String finalName;
            if (name.equals("")) finalName = "Без имени";
            else finalName = name;
            Date d = new Date();
            String time = String.format(new Locale("ru"),
                    "%td.%tm.%tY; %tH:%tM:%tS", d, d, d, d, d, d);

            ((LabyrinthActivity) requireActivity()).save(time, finalName);
            ((LabyrinthActivity) requireActivity()).exit();
        });
        builder.setNegativeButton("Выйти не сохраняя", (dialog, id) -> ((LabyrinthActivity) requireActivity()).exit());
        builder.setNeutralButton("Остаться", (dialog, id) -> {
            dialog.cancel();
            ((LabyrinthActivity) requireActivity()).retToLab();
        });
        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((LabyrinthActivity) requireActivity()).retToLab();
    }
}