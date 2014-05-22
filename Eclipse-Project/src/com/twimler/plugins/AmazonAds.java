package com.twimler.plugins;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.amazon.device.ads.*;

import com.unity3d.player.UnityPlayer;

public class AmazonAds implements AdListener
{
	public static String tag = "AmazonAds";
	
	private static final AmazonAds instance = new AmazonAds();
	
	private Activity activity;

	private com.amazon.device.ads.InterstitialAd interstitialAd;
	
	private boolean IsInitialized = false;
	
	public static boolean interstitialAdLoaded = false;
	
	private AdLayout adView = null;
	
	// Get instance of the AdRotator
	public static AmazonAds getInstance()
	{
		AmazonAds.instance.activity = UnityPlayer.currentActivity;
				
		Log.d ( tag , "Amazon Ads Plugin instantiated.");
		
		return AmazonAds.instance;
	}
	
	// Initialize Amazon Ads
	public void init ( String appKey, boolean testMode )
	{
		Log.d ( tag , "Initializing Amazon Ads plugin.");

		AdRegistration.enableTesting( testMode );
		
		AdRegistration.enableLogging( false );
		
		AdRegistration.setAppKey( appKey );
		 		 
		IsInitialized = true;
	}
	
	// Create a Banner
	public void createBanner ( final String position )
	{
		// Check if the plugin is initialized
		if ( ! IsInitialized )
		{
		    Log.d ( tag , "Amazon Ad plugin is not initialized yet!");
		    
		    return;
		}
		
		final AmazonAds self = this;
		
		// Create the ad view just once
		if ( adView == null )
		{
			// Run the thread on Unity activity
			activity.runOnUiThread (
			  new Runnable() { 
				public void run() 
				{
					adView = new AdLayout( activity );
					
			        adView.setListener( self );
					
					LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
			        		LayoutParams.WRAP_CONTENT , getGravity( position ) );
			                
			        activity.addContentView( adView, layoutParams );
			        
			        AdTargetingOptions adOptions = new AdTargetingOptions();
			        adView.loadAd(adOptions);
			        
				}
			  });
		}
		else
		{
			refresh();
			
		}
	}
	
	// Refresh banner for a new ad request
	public void refresh()
	{			
		if ( adView != null )
		{
		    Log.d ( tag , "Refreshing Amazon Ad banner.");
			
		    // Run the thread on Unity activity
			activity.runOnUiThread (
			  new Runnable() { 
				public void run() 
				{
					AdTargetingOptions adOptions = new AdTargetingOptions();
			        adView.loadAd(adOptions);
				}
			  });
		}
		else
		{
		    Log.d ( tag , "Amazon Ad plugin is not initialized yet!");
		}
	}
	
	// Hide the banner
	public void hideBanner( final boolean hide )
	{
		// Return if there is no ad view
		if ( adView == null )
		{
			return;
		}
		
		// Run the thread on Unity activity
		activity.runOnUiThread (
		  new Runnable() { 
			public void run() 
			{
				if ( hide )
				{
					adView.setVisibility( View.GONE );
				}
				else
				{
					adView.setVisibility( View.VISIBLE );
				}
			}
	   });
	}
	
	// Destroy the banner ad view
	public void destroyBanner()
	{
		if ( adView != null )
		{
			// Run the thread on Unity activity
			activity.runOnUiThread (
			  new Runnable() { 
				public void run() 
				{
					adView.destroy();
					
					adView = null;
				}
			  });
		}
	}
	
	// Request interstitials
	public void requestInterstital()
	{
		activity.runOnUiThread( new Runnable()
		{ 
			@Override public void run()
			{ 
				boolean shouldRequest = true;
			
				if ( interstitialAd != null )
				{
					if ( interstitialAd.isLoading() )
					{
						shouldRequest = false;
					}
					
					if ( AmazonAds.interstitialAdLoaded )
					{
						shouldRequest = false;
					}
				}
				
				if ( shouldRequest )
				{
					
				    Log.d ( tag , "Requesting Amazon Interstitials");
					
				    interstitialAd = new com.amazon.device.ads.InterstitialAd( activity  );
					
				    interstitialAd.setListener( new InterstitialsAdListener() );
				    					    
				    AdTargetingOptions adOptions = new AdTargetingOptions();
				    
				    interstitialAd.loadAd( adOptions );
				}
			} 
		  });

		}
		
		// Show interstitial ad if its loaded
		public void showInterstitial()
		{
			activity.runOnUiThread( new Runnable()
			{ 
				@Override public void run()
				{
					if ( interstitialAd != null )
					{
						if ( AmazonAds.interstitialAdLoaded )
						{
							interstitialAd.showAd();
							
							AmazonAds.interstitialAdLoaded = false;
						}
					}
				}
			});

		}
	
	// Get gravity of the banner based on the position
	private int getGravity ( String position )
	{
		int gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		
		// Top Left
		if ( position.equals( "TL") )
		{
			gravity = Gravity.TOP | Gravity.LEFT;
		}
		
		// Top Middle
		if ( position.equals( "TM") )
		{
			gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		}
		
		// Top Right
		if ( position.equals( "TR") )
		{
			gravity = Gravity.TOP | Gravity.RIGHT;
		}
		
		// Bottom Left
		if ( position.equals( "BL") )
		{
			gravity = Gravity.BOTTOM | Gravity.LEFT;
		}
		
		// Bottom Left
		if ( position.equals( "BM") )
		{
			gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		}
		
		// Bottom Right
		if ( position.equals( "BR") )
		{
			gravity = Gravity.BOTTOM | Gravity.RIGHT;
		}
		
		return gravity;
	}

	@Override
	public void onAdCollapsed(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdDismissed(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdExpanded(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdFailedToLoad(Ad arg0, AdError arg1) 
	{
		
		Log.d ( tag , "Amazon ads plugin failed to load a banner.");
		
		// Notify Unity
		UnityPlayer.UnitySendMessage( "TwimlerAdRotator", "OnAdEvent", "amazon-no");
	}

	@Override
	public void onAdLoaded(Ad arg0, AdProperties arg1) 
	{
		Log.d ( tag , "Amazon banner loaded successfully.");
		
		// Notify Unity
		UnityPlayer.UnitySendMessage( "TwimlerAdRotator", "OnAdEvent", "amazon-yes");
		
	}
	
	// Interstitials ad listener
	class InterstitialsAdListener extends DefaultAdListener
    {
        /**
         * This event is called once an ad loads successfully.
         */
        @Override
        public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
        	UnityPlayer.UnitySendMessage( "TwimlerAdRotator", "OnAdEvent", "amazon-interstitial-yes");
        	
        	AmazonAds.interstitialAdLoaded = true;
        }
    
        /**
         * This event is called if an ad fails to load.
         */
        @Override
        public void onAdFailedToLoad(final Ad view, final AdError error) {
        	UnityPlayer.UnitySendMessage( "TwimlerAdRotator", "OnAdEvent", "amazon-interstitial-no");
        	
        	AmazonAds.interstitialAdLoaded = false;
        }
        
        /**
         * This event is called when an interstitial ad has been dismissed by the user.
         */
        @Override
        public void onAdDismissed(final Ad ad) {
        	UnityPlayer.UnitySendMessage( "TwimlerAdRotator", "OnAdEvent", "amazon-interstitial-dismiss");
        	
        }
    }
}
