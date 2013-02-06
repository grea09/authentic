/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.grea09.android.authentic.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * @author antoine
 */
public abstract class ExtendedFragment extends Fragment
{
	
	private Activity activity;
	protected View view;

	public final View find(int id)
	{
		return view.findViewById(id);
	}
	
	final protected Activity activity()
	{
		if(getActivity() instanceof Activity)
		{
			return getActivity();
		}
		return activity;
	}
	
	final public void activity(Activity activity)
	{
		if(getActivity() instanceof Activity || this.activity instanceof Activity)
		{
			throw new IllegalAccessError("Activity already setted.");
		}
		this.activity = activity;
	}
	
	final public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState, Activity parent)
	{
		this.activity = parent;
		return onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	@Deprecated
	final public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState)
	{
		view = (View) inflater.inflate(layout(), container, false);
		nullDraw();
		return view;
	}
	
	final public View reUse(View view, ViewGroup parent)
	{
		if(view == null || view.findViewById(id()) == null)
		{
			return onCreateView(activity().getLayoutInflater(), parent, Bundle.EMPTY, activity());
		}
		this.view = view.findViewById(id());
		nullDraw();
		return this.view;
	}
	
	final protected void nullDraw()
	{
		if(view == null)
		{
			return;
		}
		draw();
	}
	
	protected abstract void draw();
	
	protected abstract int layout();
	
	protected abstract int id();
	
}
