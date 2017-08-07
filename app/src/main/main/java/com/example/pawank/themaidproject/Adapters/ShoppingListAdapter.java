package com.example.pawank.themaidproject.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawank.themaidproject.DataClass.ShoppingList;
import com.example.pawank.themaidproject.DataClass.ShoppingListItem;
import com.example.pawank.themaidproject.Fragments.ShoppingListFragment;
import com.example.pawank.themaidproject.MainActivity;
import com.example.pawank.themaidproject.R;
import com.example.pawank.themaidproject.utils.Callback;
import com.example.pawank.themaidproject.Managers.ComManager;
import com.example.pawank.themaidproject.utils.MiscUtils;
import com.example.pawank.themaidproject.Managers.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
/**
 * Created by pawan.k on 06-02-2017.
 */

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHold>{
    private Context ctx;
    private ShoppingListFragment fr;
    private ArrayList<ShoppingList> ar;
    private final String TAG="Shopping List Adapter";
    private ComManager comManager;
    private ArrayList<ShoppingListItem> listItems;
    private int sustainableTextLength=9;
    private AlertDialog alertDialog;
    private RecyclerView listRecyclerView;
    private TextView listToolbarTitle,listLastModified;
    private ImageView listAddItem,listToolbarBack,listToolbarUpload;
    private ImageButton newItemBackButton, newItemSaveButton;
    private Toolbar listToolbar;
    private ShoppingListItem sli;
    private EditText newItemName;
    private Spinner newItemType;
    private Switch newItemIsBought;
    private ProgressBar listUploadProgressBar;

    public ShoppingListAdapter(Context ctx, ShoppingListFragment fr, ArrayList<ShoppingList> ar, ComManager comManager){
        this.ctx=ctx;
        this.fr=fr;
        this.ar=ar;
        this.comManager=comManager;
    }
    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sl_cards,parent,false);
        return new ViewHold(v);
    }

    @Override
    public void onBindViewHolder(final ViewHold holder, final int position) {
        holder.sl_item_list_name.setText(MiscUtils.trimmedString(ar.get(position).getTitle(),12));
        holder.sl_item_list_name.setTypeface(Typeface.createFromAsset(ctx.getAssets(),"font/Roboto-Medium.ttf"));
        holder.sl_item_1.setVisibility(View.GONE);
        holder.sl_item_2.setVisibility(View.GONE);
        holder.sl_item_3.setVisibility(View.GONE);
        holder.sl_all_item_bought.setVisibility(View.GONE);
        listItems = ar.get(position).getItems();
        if(listItems!=null && listItems.size()>=1)
        {
            if(listItems.get(0)!=null)
            {
                holder.sl_item_1.setText(MiscUtils.trimmedString(listItems.get(0).getTitle(),sustainableTextLength));
                holder.sl_item_1.setVisibility(View.VISIBLE);
            }
            if(listItems.size()>=2) {
                if (listItems.get(1) != null) {
                    holder.sl_item_2.setText(MiscUtils.trimmedString(listItems.get(1).getTitle(), sustainableTextLength));
                    holder.sl_item_2.setVisibility(View.VISIBLE);
                }
            }
            if(listItems.size()>2)
            {
                holder.sl_item_3.setVisibility(View.VISIBLE);
            }
        }
        //if list is all bought
        if(ar.get(position).getIs_done().equals("Y"))
        {
            holder.sl_all_item_bought.setVisibility(View.VISIBLE);
        }
        //Card operations
        holder.sl_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    setList(v,holder,position);
            }
        });

        holder.sl_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                optionsDialogForList(v,holder,position);
                return true;
            }
        });
    }

    private void optionsDialogForList(View v, ViewHold holder,final int position) {

        final View view = LayoutInflater.from(ctx).inflate(R.layout.layout_sl_card_options,null);
        Button delete = (Button) view.findViewById(R.id.sl_delete_card);
        Button rename = (Button) view.findViewById(R.id.sl_rename_card);
        final AlertDialog ad = new AlertDialog.Builder(ctx)
                            .setView(view)
                            .create();
        ad.show();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(ctx)
                        .setTitle("Delete List")
                        .setMessage("Are you sure you want to delete this list?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              Callback callback = new Callback() {
                                  @Override
                                  public void onSuccess(Object obj) throws JSONException, ParseException {

                                      Snackbar.make(((MainActivity)ctx).findViewById(android.R.id.content),"List deleted!!", Toast.LENGTH_SHORT).show();
                                      fr.getAllShoppingList();
                                  }
                                  @Override
                                  public void onFailure(Object obj) throws JSONException {
                                      JSONObject workObject = (JSONObject) obj;
                                      if(workObject.getString("ERRORCODE").equals("52"))//condition Some unbought items still in list
                                      {
                                          Snackbar.make(((MainActivity)ctx).findViewById(android.R.id.content),"Some unbought items still in list", Toast.LENGTH_SHORT).show();
                                      }
                                  }
                              };
                              comManager.deleteShoppingList(ctx,ar.get(position).getId(),ar.get(position).getTitle(),callback);
                            ad.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                dialog.cancel();
                                ad.cancel();
                            }
                        }).create();
                dialog.show();
            }
        });

        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText listName =new EditText(ctx);
                AlertDialog dialog = new AlertDialog.Builder(ctx)
                        .setTitle("Rename List")
                        .setView(listName)
                        .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ar.get(position).setTitle(listName.getText().toString());
                                dialog.cancel();
                                ad.cancel();
                                ShoppingListAdapter.this.notifyDataSetChanged();
                                MiscUtils.forceHideKeyboard((MainActivity)ctx);
                                //calling server to rename
                                try {
                                    uploadListToServer(view, position,null);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ad.cancel();
                            }
                        }).create();
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return ar.size();
    }

    /*
    * Shopping List Data
     */
    public void setList(final View v, ViewHold holder, final int position) {
        final View listView = LayoutInflater.from(ctx).inflate(R.layout.layout_sl_list,null);
        listToolbar = (Toolbar)listView.findViewById(R.id.list_toolbar);
        listRecyclerView = (RecyclerView)listView.findViewById(R.id.list_recycler_view);
        listAddItem = (ImageView)listView.findViewById(R.id.list_add_item);
        listToolbarBack = (ImageView)listToolbar.findViewById(R.id.sl_list_toolbar_back);
        listToolbarUpload = (ImageView)listToolbar.findViewById(R.id.sl_list_toolbar_upload);
        listToolbarTitle = (TextView)listToolbar.findViewById(R.id.sl_list_toolbar_title);
        listLastModified = (TextView)listView.findViewById(R.id.list_modified_details);
        listUploadProgressBar = (ProgressBar) listView.findViewById(R.id.sl_list_upload_progress_bar);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                                        .setView(listView);
        alertDialog = builder.create();
        alertDialog.show();
        /*Toolbar settings*/
        listToolbarTitle.setText(ar.get(position).getTitle());
        if(ar.get(position).getLast_modified() != null && ar.get(position).getLast_modified_by() !=null)
            listLastModified.setText("Last edited on \n"+MiscUtils.getShoppingListDateFormat(ar.get(position).getLast_modified())+" by " +ar.get(position).getLast_modified_by());
        listToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.hide();
            }
        });
        /*list recycler View population*/
        //Sorting data in accordance to isBought
        final ListDataAdapter lda = new ListDataAdapter(ctx, ar.get(holder.getAdapterPosition()).getItems());
        listRecyclerView.setAdapter(lda);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        /*
          *  //*-very Important lesson: set Tag can be used to bind objects
         */
        listAddItem.setTag(R.id.list_data_adpter,lda);
        listAddItem.setTag(R.id.list_data_array,ar.get(holder.getAdapterPosition()).getItems());
        listToolbarUpload.setTag(R.id.list_position_in_array,position);
        /*list upload*/
        listToolbarUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(isAtleastOneItemDirty((int)v.getTag(R.id.list_position_in_array))) {
                        listUploadProgressBar.setVisibility(View.VISIBLE);
                        uploadListToServer(listView, (int) v.getTag(R.id.list_position_in_array), alertDialog);
                    }
                    else
                        Snackbar.make(listView,"Please Change Atleast 1 item!",Snackbar.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                try {
                    if(isAtleastOneItemDirty(position))
                        uploadListToServer(listView,position,alertDialog);
                    else
                        Snackbar.make(fr.getActivity().findViewById(android.R.id.content),"Please Change Atleast 1 item!",Snackbar.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        /*
        Add List Item
         */
        listAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewListItemData(v,position);
            }
        });
    }

    /*
    This function checks if even a single item has been changed or not
       @see #uploadListToServer()
     */
    private boolean isAtleastOneItemDirty(int position) {
        ArrayList<ShoppingListItem> array = ar.get(position).getItems();
        boolean dirtyCounter = false;
        for(ShoppingListItem item:array){
            if(item.getIs_dirty()==1) {
                Log.d(TAG,item.getTitle());
                dirtyCounter = true;
            }
        }
        Log.d(TAG,"dirt"+dirtyCounter+"");
        return dirtyCounter;
    }
    private void getNewListItemData(final View parentView, final int position) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.layout_sl__dialog_list_data_entry,null);
        sli = null;
        newItemName = (EditText)v.findViewById(R.id.sl_list_new_item_name);
        newItemBackButton = (ImageButton)v.findViewById(R.id.sl_list_new_item_back);
        newItemSaveButton = (ImageButton)v.findViewById(R.id.sl_list_new_item_save);
        newItemType = (Spinner)v.findViewById(R.id.sl_list_new_item_type);
        newItemIsBought = (Switch)v.findViewById(R.id.sl_list_new_item_is_bought);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_1,ctx.getResources().getStringArray(R.array.sl_new_item_type));
        newItemType.setAdapter(spinnerAdapter);
        final AlertDialog newItemDialog = new AlertDialog.Builder(ctx)
                                        .setView(v)
                                        .create();
        MiscUtils.forceShowKeyboard(newItemName,newItemDialog);
        newItemDialog.show();
        newItemSaveButton.setEnabled(false);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0) {
                    newItemSaveButton.setEnabled(false);
                }
                else {
                    newItemSaveButton.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        newItemName.addTextChangedListener(watcher); // watcher to enable disable submit button
        newItemBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newItemDialog.cancel();
            }
        });
        newItemSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sli=new ShoppingListItem();
                sli.setTitle(newItemName.getText().toString());
                sli.setId(-1);
                sli.setBought(newItemIsBought.isChecked()?"Y":"N");
                if(sli.isBought().equals("Y"))
                    sli.setBought_by_name(PrefManager.getSharedVal("username"));
                sli.setIs_dirty(1);
                sli.setType(newItemType.getSelectedItem().toString().toUpperCase());
                ListDataAdapter lda = (ListDataAdapter)parentView.getTag(R.id.list_data_adpter);
                ar.get(position).getItems().add(0,sli);
                lda.notifyItemInserted(0);
                lda.notifyDataSetChanged();
                newItemDialog.cancel();
            }
        });
    }

    class ViewHold extends RecyclerView.ViewHolder{
        public TextView  sl_item_list_name,sl_item_1,sl_item_2,sl_item_3;
        CardView sl_card;
        ImageView sl_all_item_bought;
        public ViewHold(View itemView) {
            super(itemView);
            sl_item_list_name = (TextView)itemView.findViewById(R.id.sl_item_list_name);
            sl_item_1 = (TextView)itemView.findViewById(R.id.sl_item_1);
            sl_item_2 = (TextView)itemView.findViewById(R.id.sl_item_2);
            sl_item_3 = (TextView) itemView.findViewById(R.id.sl_item_3);
            sl_card = (CardView) itemView.findViewById(R.id.sl_card_view);
            sl_all_item_bought = (ImageView) itemView.findViewById(R.id.sl_item_list_is_all_bought);
        }
    }

    private void uploadListToServer(final View listView, int position, final AlertDialog dialog) throws JSONException {
        Callback callback = new Callback() {
            @Override
            public void onSuccess(Object obj) throws JSONException, ParseException {
                Snackbar.make(fr.getActivity().findViewById(android.R.id.content),"Uploaded to server!!",Snackbar.LENGTH_SHORT).show();
                fr.getAllShoppingList();
                if(listUploadProgressBar!=null)
                    listUploadProgressBar.setVisibility(View.GONE);
                if(dialog!=null)
                    dialog.hide();
            }

            @Override
            public void onFailure(Object obj) throws JSONException {
                Snackbar.make(listView,"Server error occured",
                        Snackbar.LENGTH_SHORT).show();
                listUploadProgressBar.setVisibility(View.GONE);
            }
        };
        comManager.uploadShoppingList(ctx,callback,MiscUtils.shoppingListJSONParser(ar.get(position)));
    }
}