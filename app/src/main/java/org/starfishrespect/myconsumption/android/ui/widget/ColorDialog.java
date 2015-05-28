package org.starfishrespect.myconsumption.android.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import org.starfishrespect.myconsumption.android.R;

/**
 * Simple dialog used to select a color, with 3 sliders
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
public class ColorDialog extends Dialog {

    private SeekBar seekBarRed, seekBarGreen, seekBarBlue;
    private LinearLayout linearLayoutResultColor;
    private int red = 0, green = 0, blue = 0, color = 0xff0000;
    private OnColorSelected colorSelected;

    public ColorDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_color);
        setCancelable(true);
        setTitle("Color : ");

        linearLayoutResultColor = (LinearLayout) findViewById(R.id.linearLayoutResultColor);

        seekBarRed = (SeekBar) findViewById(R.id.seekBarRed);
        seekBarGreen = (SeekBar) findViewById(R.id.seekBarGreen);
        seekBarBlue = (SeekBar) findViewById(R.id.seekBarBlue);

        SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.equals(seekBarRed)) {
                    red = progress;
                }
                if (seekBar.equals(seekBarBlue)) {
                    blue = progress;
                }
                if (seekBar.equals(seekBarGreen)) {
                    green = progress;
                }
                color = 0xff000000 | (red << 16) | (green << 8) | blue;
                linearLayoutResultColor.setBackgroundColor(color);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        seekBarRed.setOnSeekBarChangeListener(seekListener);
        seekBarGreen.setOnSeekBarChangeListener(seekListener);
        seekBarBlue.setOnSeekBarChangeListener(seekListener);

        Button okButton = (Button) findViewById(R.id.buttonColorSelected);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (colorSelected != null) {
                    colorSelected.colorSelected(ColorDialog.this, color);
                }
            }
        });
    }

    public void setColor(int color) {
        this.color = color;
        linearLayoutResultColor.setBackgroundColor(color);
        red = (color & 0x00ff0000) >> 16;
        green = (color & 0x0000ff00) >> 8;
        blue = color & 0x000000ff;
        seekBarRed.setProgress(red);
        seekBarGreen.setProgress(green);
        seekBarBlue.setProgress(blue);
    }

    public void setOnColorSelected(OnColorSelected colorSelected) {
        this.colorSelected = colorSelected;
    }

    /**
     * Callback to get the selected color
     */
    public interface OnColorSelected {
        public void colorSelected(Dialog dlg, int color);
    }
}
