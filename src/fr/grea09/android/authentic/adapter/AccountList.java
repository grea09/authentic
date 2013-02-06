/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.grea09.android.authentic.adapter;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import fr.grea09.android.authentic.ui.fragment.AccountSelection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Antoine GRÉA <grea09@gmail.com>
 */
public class AccountList extends ArrayAdapter<Account>
{
	public final Map<Integer, Boolean[]> states = new HashMap<Integer, Boolean[]>();
	
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
		if(states.containsKey(position))
		{
			accountSelection.authorized(states.get(position)[0]);
			accountSelection.loading(states.get(position)[1]);
		}
		
		return accountSelection.reUse(convertView, parent);
		//		return super.getView(position, convertView, parent);
	}
	
	
}
