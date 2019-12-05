package com.flaringapp.base.presentation.dialogs.message.impl;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flaringapp.base.R;
import com.flaringapp.base.app.di.Di;
import com.flaringapp.base.presentation.dialogs.message.MessageContract;
import com.flaringapp.base.presentation.dialogs.message.MessageContract.MessageDialogParent;
import com.flaringapp.base.presentation.mvp.BaseDialog;

public class MessageDialog extends BaseDialog<MessageContract.PresenterContract>
        implements MessageContract.ViewContract {

    private static final String HEADER_KEY = "key_header";
    private static final String MESSAGE_KEY = "key_message";
    private static final String ICON_KEY = "key_icon";
    private static final String BUTTON_TEXT_KEY = "key_button_text";

    private static final int NO_ICON = -1;

    @Nullable
    private TextView textHeader;
    @Nullable
    private TextView textMessage;
    @Nullable
    private ImageView imageIcon;
    @Nullable
    private TextView textButtonAction;

    public static MessageDialog newInstance(String message) {
        return newInstance(null, message);
    }

    public static MessageDialog newInstance(String header, String message) {
        MessageDialog messageDialog = new MessageDialog();
        Bundle b = new Bundle();
        b.putString(HEADER_KEY, header);
        b.putString(MESSAGE_KEY, message);
        b.putInt(ICON_KEY, NO_ICON);
        messageDialog.setArguments(b);
        return messageDialog;
    }

    public MessageDialog withIcon(@DrawableRes int iconRes) {
        Bundle nundle = getArguments();
        if (nundle == null) nundle = new Bundle();

        nundle.putInt(ICON_KEY, iconRes);

        setArguments(nundle);

        return this;
    }

    public MessageDialog withButtonText(String buttonText) {
        Bundle nundle = getArguments();
        if (nundle == null) nundle = new Bundle();

        nundle.putString(BUTTON_TEXT_KEY, buttonText);

        setArguments(nundle);

        return this;
    }

    @NonNull
    @Override
    protected MessageContract.PresenterContract providePresenter() {
        return Di.inject(MessageContract.PresenterContract.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments == null) return;

        MessageDialogParent listener = null;
        if (getContext() instanceof MessageDialogParent) {
            listener = (MessageDialogParent) getContext();
        }
        else if (getParentFragment() instanceof MessageDialogParent) {
            listener = (MessageDialogParent) getParentFragment();
        }

        presenter.init(
                listener,
                arguments.getString(HEADER_KEY),
                arguments.getString(MESSAGE_KEY),
                arguments.getInt(ICON_KEY),
                arguments.getString(BUTTON_TEXT_KEY)
        );
    }

    @Override
    protected int provideLayoutRes() {
        return R.layout.dialog_message;
    }

    @Override
    protected void initViews(View view) {
        textHeader = view.findViewById(R.id.textTitle);
        textMessage = view.findViewById(R.id.textMessage);
        textButtonAction = view.findViewById(R.id.textButtonAction);
        imageIcon = view.findViewById(R.id.imageIcon);
    }

    @Override
    protected void init() {
        super.init();

        textButtonAction.setOnClickListener(view -> presenter.onActionButtonClick());
    }

    @Override
    protected void releaseViews() {
        textHeader = null;
        textMessage = null;
        textButtonAction = null;
        imageIcon = null;
    }

    @Override
    public void setHeaderVisible(boolean visible) {
        if (textHeader != null) {
            if (visible) textHeader.setVisibility(View.VISIBLE);
            else textHeader.setVisibility(View.GONE);
        }
    }

    @Override
    public void setIconVisible(boolean visible) {
        if (imageIcon != null) {
            if (visible) imageIcon.setVisibility(View.VISIBLE);
            else imageIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void setHeader(String header) {
        if (textHeader != null) textHeader.setText(header);
    }

    @Override
    public void setMessage(String message) {
        if (textMessage != null) textMessage.setText(message);
    }

    @Override
    public void setIcon(int iconRes) {
        if (imageIcon != null && iconRes != NO_ICON) imageIcon.setImageResource(iconRes);
    }

    @Override
    public void setActionButtonText(String actionButtonText) {
        if (textButtonAction != null) textButtonAction.setText(actionButtonText);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        presenter.onClose();
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        presenter.onClose();
        super.onCancel(dialog);
    }
}
