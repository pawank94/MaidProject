package com.example.pawank.themaidproject.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.pawank.themaidproject.DataClass.ShoppingListItem;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.utils.MiscUtils;
import com.example.pawank.themaidproject.Managers.PrefManager;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by pawan.k on 09-02-2017.
 */
public class ListDataAdapter extends RecyclerView.Adapter<ListDataAdapter.ViewHold>{
    private Context ctx;
    private ArrayList<ShoppingListItem> array;
    private String TAG = "ListDataAdapter";
    private InputMethodManager inputManager;
    private EditText editItemName;
    private Spinner editItemType;
    private Switch editItemIsBought;
    private ImageView editItemBackButton;
    private ImageView editItemSaveButton;
    private TextView editItemDialogTitle;

    public ListDataAdapter(Context ctx, ArrayList<ShoppingListItem> items) {
        this.ctx=ctx;
        this.array = items;
        inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sl_list_adpter_view,null);
        return new ViewHold(v);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }


    @Override
    public void onBindViewHolder(ViewHold holder,final int position) {
            holder.cancelItem.setTag(position);
            holder.checkBox.setTag(position);
            holder.itemName.setTag(position);
            holder.itemName.setTypeface(Typeface.createFromAsset(ctx.getAssets(),"Rubik/rubik.ttf"));
            holder.itemName.setTextSize(20.2f);
            /*
            //*-NotifyDataSetChanged always REUSES the viewholder, which in turn creates problem.
            always set all view values to default
             */
            holder.checkBox.setChecked(false);
            holder.checkBox.setEnabled(true);
            holder.itemName.setPaintFlags(holder.checkBox.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            //////////////////////////////////////////////////////////////////////////
        ShoppingListItem workObject = array.get(position);
        String text_to_set = workObject.getTitle();
        if(workObject.getBought_by_name()!=null) {
            if (!workObject.getBought_by_name().equals(PrefManager.getSharedVal("username")) && workObject.isBought().equals("Y")) {
                holder.checkBox.setEnabled(false);
            }
            else{
                holder.checkBox.setEnabled(true);
            }
        }
        //if object is bought and a buyer name is present than append that info
        if(workObject.isBought().equals("Y") && workObject.getBought_by_name()!=null)
        {
            holder.bought_by_name.setText("bought by "+workObject.getBought_by_name());
            holder.bought_by_name.setVisibility(View.VISIBLE);
        }
        else{
            holder.bought_by_name.setVisibility(View.GONE);
        }

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) //cross platform dependency
            holder.itemName.setText(Html.fromHtml(text_to_set,Html.FROM_HTML_MODE_LEGACY));
        else
            holder.itemName.setText(Html.fromHtml(text_to_set));
        switch (workObject.getType())
        {
            case "VEGGY":
                //*- setting color in a vector drawable
                holder.itemType.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_small_dot_for_veggy));
                break;
            case "STAPLE":
                holder.itemType.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_small_dot_for_staple));
                break;
            case "UTILITY":
                holder.itemType.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_small_dot_for_utility));
                break;
            default:
                MiscUtils.logD(TAG,"wrong type sent");
                break;
        }
        /*
        checkbox only works if current user is using it
         */
        if (workObject.isBought().equals("Y")) {
            holder.checkBox.setChecked(true);
        }
        else
            holder.checkBox.setChecked(false);
        if(holder.checkBox.isChecked()) {
            holder.itemName.setPaintFlags(holder.checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

    }

    private void editListItemData(final View parentView,final int position) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.layout_sl__dialog_list_data_entry,null);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.MATCH_PARENT);

        editItemName = (EditText)v.findViewById(R.id.sl_list_new_item_name);
        editItemBackButton = (ImageView)v.findViewById(R.id.sl_list_new_item_back);
        editItemSaveButton = (ImageView)v.findViewById(R.id.sl_list_new_item_save);
        editItemType = (Spinner)v.findViewById(R.id.sl_list_new_item_type);
        editItemIsBought = (Switch)v.findViewById(R.id.sl_list_new_item_is_bought);
        editItemDialogTitle = (TextView)v.findViewById(R.id.sl_list_item_entry_dialog_title);
        editItemDialogTitle.setText("Edit Item Details");
        editItemName.setText(array.get(position).getTitle());
        editItemIsBought.setVisibility(View.GONE);
        ArrayAdapter<String> ar = new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_1,ctx.getResources().getStringArray(R.array.sl_new_item_type));
        editItemType.setAdapter(ar);
        editItemType.setSelection(getItemTypeIndex(array.get(position).getType()));
        final AlertDialog editItemDialog = new AlertDialog.Builder(ctx)
                .setView(v)
                .create();
        MiscUtils.forceShowKeyboard(editItemName,editItemDialog);
        editItemDialog.show();
        editItemBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItemDialog.cancel();
            }
        });
        editItemSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if nothing is filled
                if(editItemName.getText().length()!=0)
                    array.get(position).setTitle(editItemName.getText().toString());
                array.get(position).setType(editItemType.getSelectedItem().toString().toUpperCase());
                array.get(position).setIs_dirty(1);
                ListDataAdapter.this.notifyItemChanged(position);
                MiscUtils.logD(TAG,"list data updated");
                editItemDialog.cancel();
            }
        });
    }

    //Utility function
    private int getItemTypeIndex(String type)
    {
        switch (type)
        {
            case "STAPLE":
                return 0;
            case "VEGGY":
                return 1;
            case "UTILITY":
                return 2;
            default:
                MiscUtils.logD(TAG,"wrong type sent");
                return -1;
        }
    }
    ///////////////////////////////////

    public class ViewHold extends RecyclerView.ViewHolder{
        public CheckBox checkBox;
        public EditText itemName;
        public ImageButton cancelItem;
        public TextView bought_by_name;
        public ImageView itemType;
        public ViewHold(View itemView) {
            super(itemView);
            checkBox = (CheckBox)itemView.findViewById(R.id.sl_item_checkBox);
            itemName = (EditText) itemView.findViewById(R.id.sl_item_name);
            cancelItem = (ImageButton)itemView.findViewById(R.id.sl_item_cancel);
            bought_by_name = (TextView) itemView.findViewById(R.id.sl_item_bought_by_status);
            itemType = (ImageView) itemView.findViewById(R.id.sl_item_type);
            itemName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editListItemData(v,(int)v.getTag());
                }
            });
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText present = (EditText) ((ViewGroup)v.getParent()).findViewById(R.id.sl_item_name);
                    CheckBox buttonView = (CheckBox)v;
                    if(buttonView.isEnabled()) {
                        if (buttonView.isChecked()) {
                            present.setPaintFlags(itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            array.get((int) buttonView.getTag()).setBought("Y");
                            array.get((int) buttonView.getTag()).setBought_by_name(PrefManager.getSharedVal("username"));
                            array.get((int) buttonView.getTag()).setIs_dirty(1);
                        } else {
                            present.setPaintFlags(itemName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                            array.get((int) buttonView.getTag()).setBought("N");
                            array.get((int) buttonView.getTag()).setBought_by_name(null);
                            array.get((int) buttonView.getTag()).setIs_dirty(1);
                        }
                    }
                }
            });
            cancelItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new AlertDialog.Builder(ctx)
                            .setMessage("Are you sure you want to Delete this item?")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    array.remove(Integer.parseInt(v.getTag()+""));
                                    ListDataAdapter.this.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create().show();
                }
            });
        }

    }

}
