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
import android.widget.TextView;
import android.widget.ToggleButton;
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
	private boolean authorized = false;
	private ImageView logo;
	private TextView type;
	private ToggleButton authorize;
	private TextView name;
	
	private final static Map<String, Drawable> icons = new HashMap<String, Drawable>();

	public AccountSelection(Account account)
	{
		this.account = account;
	}
	
	public AccountSelection(Account account, boolean authorized)
	{
		this.account = account;
		this.authorized = authorized;
	}
	
	public void authorized(boolean authorized)
	{
		this.authorized = authorized;
		authorize.setChecked(authorized);
	}
	
	public void loading(boolean loading)
	{
		authorize.setEnabled(loading);
	}


	@Override
	protected void draw()
	{
		if(view==null)
			return;
		logo = (ImageView) find(R.id.account_logo);
		logo.setImageDrawable(icon());
		
		type = (TextView) find(R.id.account_type);
		type.setText(account.type);
		
		name = (TextView) find(R.id.account_name);
		name.setText(account.name);
		
		authorize = (ToggleButton) find(R.id.account_authorize);
		authorize.setChecked(authorized);
		
		authorize.setOnCheckedChangeListener(this);
		
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
		return icons.get(account);
	}
	
}
