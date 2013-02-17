/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.grea09.android.authentic.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import fr.grea09.android.authentic.adapter.AccountList;
import fr.grea09.android.authentic.logic.Token;
import fr.grea09.android.authentic.ui.fragment.AccountSelection;
import fr.grea09.android.authentic.ui.fragment.AccountSelection.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Antoine GRÉA <grea09@gmail.com>
 */
public class AccountChooser extends ListActivity
{
	public static final Set<Account> authorized= new HashSet<Account>();
	
	private static List<Account> accounts= new ArrayList<Account>();
    protected Intent intent;
	public static int resultCode;
	public static final Object wait = new Object();
	
	public static final String ACCOUNT_TYPES = "account_types";
	
	public static final String PREFERENCE_ACCOUNTS_KEY = "accounts";
	public static final String PREFERENCE_NAME = "authentic";
	
	public static final String AUTHORIZE = "authorize";
	public static final String TO_AUTHORIZE = "to_authorize";
	
	private int authorizing = -1;
	
	private Set<BroadcastReceiver> receivers = new HashSet<BroadcastReceiver>();
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
		setProgressBarIndeterminateVisibility(false);
//		if(getIntent().getBundleExtra(INTENT) != null)
//		{
//			startActivityForResult((Intent) getIntent().getBundleExtra(INTENT).get(INTENT), 0);
//			synchronized(wait)
//			{
//				try
//				{
//					wait.wait();
//				} catch (InterruptedException ex)
//				{
//					ex.printStackTrace();
//				}
//			}
//		}
		
		accounts = Arrays.asList(AccountManager.get(getApplicationContext()).getAccounts());
		this.setListAdapter(new AccountList(this, android.R.layout.simple_list_item_1, accounts));
		
		SharedPreferences settings = getSharedPreferences(PREFERENCE_NAME, 0);
		Set<String> authorizedTmp = settings.getStringSet(PREFERENCE_ACCOUNTS_KEY, null);
		Account account;
		if(authorizedTmp != null)
		{
			for (String string : authorizedTmp)
			{
				String[] split = string.split(":");
				account = new Account(split[0], split[1]);
				authorized.add(account);
				((AccountList) getListAdapter()).states.put(accounts.indexOf(account), State.AUTHORIZED);
				((AccountList) getListAdapter()).notifyDataSetChanged();
			}
		}
		
		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent)
			{
				authorize(accounts.indexOf(intent.getParcelableExtra(TO_AUTHORIZE)));
			}
		}, new IntentFilter(AUTHORIZE));
		
		
		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent)
			{
				Account account = (Account) intent.getParcelableExtra(Token.ACCOUNT_KEY);
				authorized.add(account);
				setProgressBarIndeterminateVisibility(false);
				((AccountList) getListAdapter()).states.put(authorizing, State.AUTHORIZED);
				authorizing = -1;
				((AccountList) getListAdapter()).notifyDataSetChanged();
			}
		}, new IntentFilter(Token.BROADCAST_TOKEN_READY));
		
		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent)
			{
				setProgressBarIndeterminateVisibility(false);
				((AccountList) getListAdapter()).states.put(authorizing, State.FAILLED);
				authorizing = -1;
				((AccountList) getListAdapter()).notifyDataSetChanged();
			}
		}, new IntentFilter(Token.BROADCAST_TOKEN_FAILLED));
		
        
    }

	@Override
	protected void onStop()
	{
		super.onStop();
		Set<String> authorizedTmp = new HashSet<String>()
		{{
				for (Account account : authorized)
				{
					add(account.name + ":" + account.type);
				}
		}};
		SharedPreferences settings = getSharedPreferences(PREFERENCE_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putStringSet(PREFERENCE_ACCOUNTS_KEY, authorizedTmp);

		// Commit the edits!
		editor.commit();
	}
	
	

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        authorize(position);
    }
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter)
	{
		receivers.add(receiver);
		return super.registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy()
	{
		for(BroadcastReceiver receiver : receivers)
		{
			unregisterReceiver(receiver);
		}
		super.onDestroy();
	}
	
	private void authorize(int position)
	{
		State state = ((AccountList) getListAdapter()).states.get(position);
		if(authorizing == -1 && (state==null || state != State.AUTHORIZED))
		{
			setProgressBarIndeterminateVisibility(true);
			authorizing = position;
			((AccountList) getListAdapter()).states.put(authorizing, AccountSelection.State.LOADING);
			((AccountList) getListAdapter()).notifyDataSetChanged();
			//TODO type parameter
			Token token = new Token((Context) this, (Account) getListAdapter().getItem(position), "", false);
		}
	}
	
	
	
//	@Override
//	public void onActivityResult (int requestCode, int resultCode, Intent data)
//	{
//		AccountChooser.resultCode = resultCode;
//		Log.d("Result is " + resultCode);
//		synchronized(wait)
//		{
//			wait.notifyAll();
//		}
//		setProgressBarIndeterminateVisibility(false);
//		
//		switch(resultCode)
//		{
//			case RESULT_OK:
//				//TODO store unauthorized accounts.
//				((AccountList)getListAdapter()).states.put(authorizing, new Boolean[]{true, false});
//				((AccountList)getListAdapter()).notifyDataSetChanged();
//				authorized.add(((AccountList)getListAdapter()).getItem(authorizing));
//				if(getListAdapter().isEmpty())
//				{
//					finish();
//				}
//				Toast.makeText(this, "All accounts authorized", Toast.LENGTH_SHORT).show();
//				break;
//			case RESULT_CANCELED:
//				((AccountList)getListAdapter()).states.put(authorizing, new Boolean[]{false, false});
//				((AccountList)getListAdapter()).notifyDataSetChanged();
//				Toast.makeText(this, "Access denied", Toast.LENGTH_LONG).show();
//		}
//		authorizing = -1;
//	}
}
