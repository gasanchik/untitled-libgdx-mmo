package com.hasanchik.game.utils;

import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;
import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import de.tomgrill.gdxdialogs.core.dialogs.GDXProgressDialog;
import de.tomgrill.gdxdialogs.core.dialogs.GDXTextPrompt;
import de.tomgrill.gdxdialogs.core.listener.ButtonClickListener;
import de.tomgrill.gdxdialogs.core.listener.TextPromptListener;

public class GDXDialogsFacade {
    //Use popup.build().show(); to show the popups
    private static final GDXDialogs dialogs = GDXDialogsSystem.install();
    public static GDXButtonDialog getSimpleInfoPopup(String message, ButtonClickListener buttonClickListener) {
        GDXButtonDialog buttonDialog = dialogs.newDialog(GDXButtonDialog.class);

        buttonDialog.setTitle("Information");
        buttonDialog.setMessage(message);

        buttonDialog.setClickListener(buttonClickListener);

        buttonDialog.addButton("OK");

        return buttonDialog;
    }

    public static GDXButtonDialog getSimpleInfoPopup(String message) {
        GDXButtonDialog buttonDialog = getSimpleInfoPopup(message, (button) -> {
        });
        return buttonDialog;
    }

    public static GDXButtonDialog getMultipleChoicePopup(String[] buttonNames, String title, String message, ButtonClickListener buttonClickListener) {
        GDXButtonDialog buttonDialog = dialogs.newDialog(GDXButtonDialog.class);

        buttonDialog.setTitle(title);
        buttonDialog.setMessage(message);

        buttonDialog.setClickListener(buttonClickListener);

        for (String button: buttonNames) {
            buttonDialog.addButton(button);
        }

        return buttonDialog;
    }

    public static GDXTextPrompt getTextPromptPopup(String title, String message, TextPromptListener textPromptListener) {
        GDXTextPrompt textPrompt = dialogs.newDialog(GDXTextPrompt.class);

        textPrompt.setTitle(title);
        textPrompt.setMessage(message);

        textPrompt.setConfirmButtonLabel("Confirm");
        textPrompt.setCancelButtonLabel("Cancel");

        textPrompt.setTextPromptListener(textPromptListener);

        return textPrompt;
    }

    public static GDXProgressDialog getProgressPopup(String title, String message) {
        GDXProgressDialog progressDialog = dialogs.newDialog(GDXProgressDialog.class);

        progressDialog.setTitle(title);
        progressDialog.setMessage(message);

        return progressDialog;
    }
}
