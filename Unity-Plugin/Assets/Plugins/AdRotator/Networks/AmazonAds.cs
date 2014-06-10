/*
 *  Copyright Majid Khosravi @ 2014 - Twimler.com
 * 
 * MIT License
 */
using UnityEngine;
using System.Collections;

public class AmazonAds : MonoBehaviour 
{
	public static AmazonAds current = null ;

	public string AppKey = "";

	public bool TestMode = true;

	public int RefreshInterval = 60;

	public bool InterstitialLoaded = false;
	
	private bool IsInit = false;

	private bool IsBannerCreated = false;

	#if UNITY_ANDROID && ! UNITY_EDITOR
	private AndroidJavaObject plugin;
	#endif

	// Initializing AdRotator
	void Awake()
	{

		if ( current == null )
		{
			current = this;

			#if UNITY_ANDROID && ! UNITY_EDITOR
			AndroidJavaClass jc = new AndroidJavaClass("com.twimler.plugins.AmazonAds"); 
			plugin = jc.CallStatic<AndroidJavaObject>("getInstance"); 	
			#endif

			InvokeRepeating ( "Refresh" , RefreshInterval , RefreshInterval );
		}
	}
	
	// Add an advertising network
	public void Init ()
	{
		#if UNITY_ANDROID && ! UNITY_EDITOR
			plugin.Call ("init", AppKey , TestMode );
		#endif

		IsInit = true;
	}

	// Refresh the banner
	public void Refresh()
	{
		if ( IsInit )
		{
			#if UNITY_ANDROID && ! UNITY_EDITOR
				plugin.Call ("refresh");
			#endif
		}
	}

	// Create Banner
	public void CreateBanner()
	{

		if ( IsInit )
		{
			#if UNITY_ANDROID && ! UNITY_EDITOR
				plugin.Call ("createBanner" , GetPosition() );
			#endif

			IsBannerCreated = true;
		}
	}

	// Destroy Banner
	public void DestroyBanner()
	{
		if ( IsInit )
		{
			#if UNITY_ANDROID && ! UNITY_EDITOR
				plugin.Call ("destroyBanner");
			#endif

			IsBannerCreated = false;
		}
	}

	// Hide Banner
	public void HideBanner( bool hide )
	{
		if ( IsInit )
		{
			if ( IsBannerCreated )
			{
				#if UNITY_ANDROID && ! UNITY_EDITOR
					plugin.Call ("hideBanner" , hide);
				#endif
			}
			else
			{
				Debug.Log ("You must call CreateBanner function first");
			}
		}
		else
		{
			Debug.Log ("Amazon ads are not initialized yet");
		}
	}


	// Request interstitials
	public void RequestInterstitials()
	{
		if ( IsInit )
		{
			#if UNITY_ANDROID && ! UNITY_EDITOR
			plugin.Call ("requestInterstital" );
			#endif
		}
	}
	
	// Show interstitials
	public void ShowInterstitials()
	{
		if ( IsInit )
		{
			#if UNITY_ANDROID && ! UNITY_EDITOR
			if ( InterstitialLoaded )
			{
				plugin.Call ("showInterstitial" );
				InterstitialLoaded = false;
			}
			#endif
		}
	}
	
	// If interstitial is ready
	public bool IsInterstitialReady()
	{
		return InterstitialLoaded;
	}

	// Get Vertical Position
	private string GetPosition()
	{
		string position = "BM";

		if ( AdRotator.current.BannerAlign == AdRotator.VerticalAligns.Top )
		{
			position = "TM";
		}

		if ( AdRotator.current.BannerAlign == AdRotator.VerticalAligns.Bottom )
		{
			position = "BM";
		}

		return position;
	}

}
