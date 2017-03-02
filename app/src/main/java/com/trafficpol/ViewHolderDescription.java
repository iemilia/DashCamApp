package com.trafficpol;

import android.content.Context;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


/**
 * Created by Emilia on 3/22/2016.
 */
public class ViewHolderDescription extends MyRecyclerViewVideosAdapter
        .VideoObjectHolder {
    EditText description;
    EditText other;
    Context mContext;
    public RadioGroup categories;
    public RadioButton cat1, cat2, cat3, cat4, cat5, cat6, cat7, catother;

    String category="accident";

    public String getDescription() {
        return description.getText().toString();
    }

    public String getCategory() {
        return category;
    }

    public ViewHolderDescription(final View itemView, final Context context) {

        super(itemView);
        mContext = context;
        description = (EditText) itemView.findViewById(R.id.description);
        other = (EditText) itemView.findViewById(R.id.othercat);
       // categories = (RadioGroup) itemView.findViewById(R.id.categorii);
        cat1 = (RadioButton) itemView.findViewById(R.id.cat1);
        cat2 = (RadioButton) itemView.findViewById(R.id.cat2);
        cat3 = (RadioButton) itemView.findViewById(R.id.cat3);
        cat4 = (RadioButton) itemView.findViewById(R.id.cat4);
        cat5 = (RadioButton) itemView.findViewById(R.id.cat5);
        cat6 = (RadioButton) itemView.findViewById(R.id.cat6);
        cat7 = (RadioButton) itemView.findViewById(R.id.cat7);
        catother = (RadioButton) itemView.findViewById(R.id.catother);

        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                description.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(description, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        description.requestFocus();

        cat1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                category = "accident";
                //((RadioButton) itemView.findViewById(R.id.cat1)).setChecked(true);
                ((RadioButton) itemView.findViewById(R.id.cat2)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat3)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat4)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat5)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat6)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat7)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.catother)).setChecked(false);
                //Toast.makeText(getApplicationContext(), "Check box "+arg0.getText().toString()+" is "+String.valueOf(arg1) , Toast.LENGTH_LONG).show();
            }
        });
        cat2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                category = "dangerous bahavior";
                ((RadioButton) itemView.findViewById(R.id.cat1)).setChecked(false);
                //((RadioButton) itemView.findViewById(R.id.cat2)).setChecked(true);
                ((RadioButton) itemView.findViewById(R.id.cat3)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat4)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat5)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat6)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat7)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.catother)).setChecked(false);
                //Toast.makeText(getApplicationContext(), "Check box "+arg0.getText().toString()+" is "+String.valueOf(arg1) , Toast.LENGTH_LONG).show();
            }
        });
        cat3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                category = "traffic violation";
                ((RadioButton) itemView.findViewById(R.id.cat1)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat2)).setChecked(false);
                //((RadioButton) itemView.findViewById(R.id.cat3)).setChecked(true);
                ((RadioButton) itemView.findViewById(R.id.cat4)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat5)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat6)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat7)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.catother)).setChecked(false);
                //Toast.makeText(getApplicationContext(), "Check box "+arg0.getText().toString()+" is "+String.valueOf(arg1) , Toast.LENGTH_LONG).show();
            }
        });
        cat4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                category = "violance";
                ((RadioButton) itemView.findViewById(R.id.cat1)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat2)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat3)).setChecked(false);
                //((RadioButton) itemView.findViewById(R.id.cat4)).setChecked(true);
                ((RadioButton) itemView.findViewById(R.id.cat5)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat6)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat7)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.catother)).setChecked(false);
                //Toast.makeText(getApplicationContext(), "Check box "+arg0.getText().toString()+" is "+String.valueOf(arg1) , Toast.LENGTH_LONG).show();
            }
        });
        cat5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                category = "burglary";
                ((RadioButton) itemView.findViewById(R.id.cat1)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat2)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat3)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat4)).setChecked(false);
                //((RadioButton) itemView.findViewById(R.id.cat5)).setChecked(true);
                ((RadioButton) itemView.findViewById(R.id.cat6)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat7)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.catother)).setChecked(false);
                //Toast.makeText(getApplicationContext(), "Check box "+arg0.getText().toString()+" is "+String.valueOf(arg1) , Toast.LENGTH_LONG).show();
            }
        });
        cat6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                category = "dangerous place";
                ((RadioButton) itemView.findViewById(R.id.cat1)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat2)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat3)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat4)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat5)).setChecked(false);
                //((RadioButton) itemView.findViewById(R.id.cat6)).setChecked(true);
                ((RadioButton) itemView.findViewById(R.id.cat7)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.catother)).setChecked(false);
                //Toast.makeText(getApplicationContext(), "Check box "+arg0.getText().toString()+" is "+String.valueOf(arg1) , Toast.LENGTH_LONG).show();
            }
        });
        cat7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                category = "littering";
                ((RadioButton) itemView.findViewById(R.id.cat1)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat2)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat3)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat4)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat5)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat6)).setChecked(false);
                //((RadioButton) itemView.findViewById(R.id.cat7)).setChecked(true);
                ((RadioButton) itemView.findViewById(R.id.catother)).setChecked(false);
                //Toast.makeText(getApplicationContext(), "Check box "+arg0.getText().toString()+" is "+String.valueOf(arg1) , Toast.LENGTH_LONG).show();
            }
        });
        catother.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                category = "other";
                ((RadioButton) itemView.findViewById(R.id.cat1)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat2)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat3)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat4)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat5)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat6)).setChecked(false);
                ((RadioButton) itemView.findViewById(R.id.cat7)).setChecked(false);
                //((RadioButton) itemView.findViewById(R.id.catother)).setChecked(true);
                //Toast.makeText(getApplicationContext(), "Check box "+arg0.getText().toString()+" is "+String.valueOf(arg1) , Toast.LENGTH_LONG).show();
            }
        });

        /*
        categories.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Log.d("chk", "id" + checkedId);
                // find which radio button is selected
                if (cat1.isChecked()) {
                    Toast.makeText(mContext, "choice:" + R.string.cat1,
                            Toast.LENGTH_SHORT).show();
                } else if (cat2.isChecked()) {
                    Toast.makeText(mContext, "choice:" + R.string.cat2,
                            Toast.LENGTH_SHORT).show();
                } else if (cat3.isChecked()) {
                    Toast.makeText(mContext, "choice:" + R.string.cat3,
                            Toast.LENGTH_SHORT).show();
                } else if (cat4.isChecked()) {
                    Toast.makeText(mContext, "choice:" + R.string.cat4,
                            Toast.LENGTH_SHORT).show();
                } else if (cat5.isChecked()) {
                    Toast.makeText(mContext, "choice:" + R.string.cat5,
                            Toast.LENGTH_SHORT).show();
                } else if (cat6.isChecked()) {
                    Toast.makeText(mContext, "choice:" + R.string.cat6,
                            Toast.LENGTH_SHORT).show();
                } else if (cat7.isChecked()) {
                    Toast.makeText(mContext, "choice:" + R.string.cat7,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "choice:" + R.string.catother,
                            Toast.LENGTH_SHORT).show();
                    other.setVisibility(View.VISIBLE);
                }
            }
        });
*/
    }

}