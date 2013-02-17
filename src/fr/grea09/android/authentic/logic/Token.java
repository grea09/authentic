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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
	
	public static final String BROADCAST_TOKEN_READY = "fr.grea09.android.authentic.TokenReady";
	
	public static final String TOKEN_KEY = "token";
	public static final String ACCOUNT_KEY = "account";
	public static final String ERROR_KEY = "error";
	public static final String BROADCAST_TOKEN_FAILLED  = "fr.grea09.android.authentic.TokenFailled";
	
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
		request();
	}

	public void run(AccountManagerFuture<Bundle> future)
	{
		try
		{
			Bundle authTokenBundle = future.getResult();
			if(authTokenBundle.containsKey(AccountManager.KEY_ERROR_MESSAGE))
			{
				Message message = new Message();
				message.obj = authTokenBundle.getString(AccountManager.KEY_ERROR_MESSAGE);
				handleMessage(message);
				return;
			}
			
			synchronized(this)
			{
				token = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN);
			}
			synchronized(this)
			{
				notifyAll();
				Intent intent = new Intent();
				intent.setAction(BROADCAST_TOKEN_READY);
				intent.putExtra(TOKEN_KEY, token);
				intent.putExtra(ACCOUNT_KEY, 
						new Account(
							authTokenBundle.getString(AccountManager.KEY_ACCOUNT_NAME), 
							authTokenBundle.getString(AccountManager.KEY_ACCOUNT_TYPE)
						));
				context.sendBroadcast(intent);
			}
		} catch (OperationCanceledException ex)
		{
			Message message = new Message();
			message.obj = ex.getLocalizedMessage();
			handleMessage(message);
		} catch (IOException ex)
		{
			Message message = new Message();
			message.obj = ex.getLocalizedMessage();
			handleMessage(message);
		} catch (AuthenticatorException ex)
		{
			Message message = new Message();
			message.obj = ex.getLocalizedMessage();
			handleMessage(message);
		}
	}

	public boolean handleMessage(final Message msg)
	{
		if(context instanceof Activity)
		{
			((Activity) context).runOnUiThread(new Runnable() 
			{
				public void run()
				{
					Toast.makeText(context, "Error : " + msg.obj.toString(), Toast.LENGTH_LONG).show();
				}
			});
		}
		synchronized(this)
		{
			notifyAll();
			Intent intent = new Intent();
			intent.setAction(BROADCAST_TOKEN_FAILLED);
			intent.putExtra(ERROR_KEY, msg.obj.toString());
			context.sendBroadcast(intent);
		}
		return true;
	}

	public void request()
	{
		AccountManagerFuture<Bundle> tmp = null;
		if (context instanceof Activity && !background)
		{
			tmp = AccountManager.get(context).getAuthToken(
			account,						// Account retrieved using getAccountsByType()
			scope,							// Auth scope
			Bundle.EMPTY,                   // Authenticator-specific options
			(Activity) context,             // Your activity
			this,							// Callback called when a token is successfully acquired
			new Handler(this));
		}
		else
		{
			tmp = AccountManager.get(context).getAuthToken(
			account,						// Account retrieved using getAccountsByType()
			scope,							// Auth scope
			Bundle.EMPTY,                   // Authenticator-specific options
			background,                     // Background notification ?
			this,							// Callback called when a token is successfully acquired
			new Handler(this));
		}
		final AccountManagerFuture<Bundle> future = tmp;
		new AsyncTask<AccountManagerFuture<Bundle>, Void, Void>()
		{
			@Override
			protected Void doInBackground(AccountManagerFuture<Bundle>... params)
			{
				run(params[0]);
				return null;
			}

		}.execute(future);
	}
	
	public void invalidate()
	{
		AccountManager.get(context).invalidateAuthToken(account.type, token);
		request();
	}
	
}