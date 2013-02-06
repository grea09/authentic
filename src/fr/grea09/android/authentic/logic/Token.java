/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.grea09.android.authentic.logic;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import java.io.IOException;

/**
 *
 * @author Antoine GRÉA <grea09@gmail.com>
 */
public class Token implements AccountManagerCallback<Bundle>, Handler.Callback
{
	public Context context;
	public Account account;
	public String scope;
	public boolean background;
	public String token;
	
	@Override
	public String toString()
	{
		return token;
	}
	
	public Token()
	{
		
	}
	
	public Token(Context context, Account account, String scope, boolean background)
	{
		this.context = context;
		this.account = account;
		this.scope = scope;
		this.background = background;
		synchronized(this)
		{
			request();
			try
			{
				wait();
			} catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void run(AccountManagerFuture<Bundle> future)
	{
		try
		{
			Bundle authTokenBundle = future.getResult();
			synchronized(this)
			{
				token = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN).toString();
			}
			synchronized(this)
			{
				notifyAll();
			}
		} catch (OperationCanceledException ex)
		{
			ex.printStackTrace(); //TODO handleMessage
		} catch (IOException ex)
		{
			ex.printStackTrace();
		} catch (AuthenticatorException ex)
		{
			ex.printStackTrace();
		}
	}

	public boolean handleMessage(Message msg)
	{
		Toast.makeText(context, "Error : " + msg.toString(), Toast.LENGTH_LONG).show();
		return true;
	}

	public void request()
	{
		if (context instanceof Activity && !background)
		{
			AccountManager.get(context).getAuthToken(
			account,						// Account retrieved using getAccountsByType()
			scope,							// Auth scope
			Bundle.EMPTY,                   // Authenticator-specific options
			(Activity) context,             // Your activity
			this,							// Callback called when a token is successfully acquired
			new Handler(this));
		}
		else
		{
			AccountManager.get(context).getAuthToken(
			account,						// Account retrieved using getAccountsByType()
			scope,							// Auth scope
			Bundle.EMPTY,                   // Authenticator-specific options
			background,                     // Background notification ?
			this,							// Callback called when a token is successfully acquired
			new Handler(this));
		}
	}
	
	public void invalidate()
	{
		AccountManager.get(context).invalidateAuthToken(account.type, token);
		synchronized(this)
		{
			request();
			try
			{
				wait();
			} catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
}