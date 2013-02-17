/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.grea09.android.authentic.ui.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import fr.grea09.android.authentic.R;
import fr.grea09.android.authentic.ui.activity.AccountChooser;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Antoine GRÉA <grea09@gmail.com>
 */
public class AccountSelection extends ExtendedFragment implements OnCheckedChangeListener
{
	public final Account account;
	private State state = null;
	private ImageView logo;
	private TextView type;
	private ImageView authorize;
	private TextView name;
	
	private final static Map<String, Drawable> icons = new HashMap<String, Drawable>();
	private ProgressBar loading;
	
	public enum State
	{
		AUTHORIZED,
		FAILLED,
		LOADING;
	}

	public AccountSelection(Account account)
	{
		this.account = account;
	}
	
	public AccountSelection(Account account, State state)
	{
		this.account = account;
		this.state = state;
	}
	
	public void state(State state)
	{
		this.state = state;
		
		switch(state)
		{
			case AUTHORIZED :
			case FAILLED :
				authorize.setVisibility(View.VISIBLE);
				loading.setVisibility(View.GONE);
				break;
			case LOADING :
				authorize.setVisibility(View.GONE);
				loading.setVisibility(View.VISIBLE);
				break;
			default:
				authorize.setVisibility(View.GONE);
				loading.setVisibility(View.GONE);
				break;
		}
		
		switch(state)
		{
			case AUTHORIZED : 
				authorize.setImageResource(R.drawable.ic_action_dark_check);
				break;
			case FAILLED : 
				authorize.setImageResource(R.drawable.ic_action_dark_cancel);
				break;
		}
		
	}


	@Override
	protected void draw()
	{
		if(view==null)
		{
			return;
		}
		logo = (ImageView) find(R.id.account_logo);
		logo.setImageDrawable(icon());
		
//		type = (TextView) find(R.id.account_type);
//		type.setText(account.type);
		
		name = (TextView) find(R.id.account_name);
		name.setText(account.name);
		
		authorize = (ImageView) find(R.id.account_authorize);
//		authorize.setChecked(authorized);
		
//		authorize.setOnCheckedChangeListener(this);
		
		loading = (ProgressBar) find(R.id.account_loading);
		
	}

	@Override
	protected int layout()
	{
		return R.layout.account;
	}

	@Override
	protected int id()
	{
		return R.id.account;
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if(isChecked)
		{
			buttonView.setChecked(false);
			buttonView.setEnabled(false);
			buttonView.setVisibility(View.GONE);
			
			Intent intent = new Intent(AccountChooser.AUTHORIZE);
			intent.putExtra(AccountChooser.TO_AUTHORIZE, account);
			activity().sendBroadcast(intent);
			
		}
		else
		{
			buttonView.setChecked(true);
		}
	}

	private Drawable icon()
	{
		if(icons.isEmpty())
		{
			AuthenticatorDescription[] descriptions = AccountManager.get(activity()).getAuthenticatorTypes();
			for (AuthenticatorDescription description : descriptions)
			{
				PackageManager packageManager = activity().getPackageManager();
				icons.put(description.type, packageManager.getDrawable(description.packageName, description.iconId, null));
			}
		}
		return icons.get(account.type);
	}
	
}
