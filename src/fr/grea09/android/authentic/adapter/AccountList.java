/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.grea09.android.authentic.adapter;

import android.accounts.Account;
import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import fr.grea09.android.authentic.ui.fragment.AccountSelection;
import fr.grea09.android.authentic.ui.fragment.AccountSelection.State;
import java.util.List;

/**
 *
 * @author Antoine GRÉA <grea09@gmail.com>
 */
public class AccountList extends ArrayAdapter<Account>
{
	public final SparseArray<State> states = new SparseArray<State>();
	
	private Activity activity;
	

	public AccountList(Activity activity, int resource, int textViewResourceId, List<Account> objects)
	{
		super(activity, resource, textViewResourceId, objects);
		this.activity = activity;
	}

	public AccountList(Activity activity, int textViewResourceId, List<Account> objects)
	{
		super(activity, textViewResourceId, objects);
		this.activity = activity;
	}

	public AccountList(Activity activity, int resource, int textViewResourceId, Account[] objects)
	{
		super(activity, resource, textViewResourceId, objects);
		this.activity = activity;
	}

	public AccountList(Activity activity, int textViewResourceId, Account[] objects)
	{
		super(activity, textViewResourceId, objects);
		this.activity = activity;
	}

	public AccountList(Activity activity, int resource, int textViewResourceId)
	{
		super(activity, resource, textViewResourceId);
		this.activity = activity;
	}

	public AccountList(Activity activity, int textViewResourceId)
	{
		super(activity, textViewResourceId);
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		AccountSelection accountSelection = new AccountSelection(getItem(position));
		accountSelection.activity(activity);
		View view = accountSelection.reUse(convertView, parent);
		if(states.indexOfKey(position) > 0 )
		{
			accountSelection.state(states.get(position));
		}
		
		return view;
		//		return super.getView(position, convertView, parent);
	}
	
	
}
