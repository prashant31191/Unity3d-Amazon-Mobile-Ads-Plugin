/*
 *  Copyright Majid Khosravi @ 2014 - Twimler.com
 * 
 * MIT License
 */
using UnityEngine;
using System.Collections;

public class AdRotator : MonoBehaviour 
{

	public static AdRotator current = null;

	// Vertical Align  ( Top or Bottom )
	public enum VerticalAligns { Top = 0 , Bottom = 1 };
	public VerticalAligns BannerAlign = VerticalAligns.Bottom;
	
	void Awake()
	{
		if ( current == null )
		{
			current = this;

			gameObject.name = "TwimlerAdRotator";

			Screen.sleepTimeout = SleepTimeout.NeverSleep;
			
			DontDestroyOnLoad ( this );

			AmazonAds.current.Init();
		}
		else
		{
			Destroy ( gameObject );
		}
	}

	// Testing
	void OnGUI()
	{
		GUILayout.BeginHorizontal();

		GUILayout.Toggle ( AmazonAds.current.IsInterstitialReady() , "  Amazon Interstitials Loaded" , GUILayout.Width ( Screen.width  ) );



		GUILayout.EndHorizontal();

		// Create the banner
		if ( GUI.Button ( new Rect ( Screen.width * 0.05f , Screen.height * 0.1f , Screen.width * 0.4f , Screen.height * 0.15f ) , "Create Amazon Banners") )
		{
			AmazonAds.current.CreateBanner();
		}
		
		if ( GUI.Button ( new Rect ( Screen.width * 0.55f , Screen.height * 0.1f , Screen.width * 0.4f , Screen.height * 0.15f ) , "Refresh Amazon Banners") )
		{
			AmazonAds.current.Refresh();
		}
		
		if ( GUI.Button ( new Rect ( Screen.width * 0.05f , Screen.height * 0.3f , Screen.width * 0.4f , Screen.height * 0.15f ) , "Hide Amazon Banners") )
		{
			AmazonAds.current.HideBanner ( true );
		}
		
		if ( GUI.Button ( new Rect ( Screen.width * 0.55f , Screen.height * 0.3f , Screen.width * 0.4f , Screen.height * 0.15f ) , "UnHide Amazon Banners") )
		{
			AmazonAds.current.HideBanner ( false );
		}

		// Interstitials
		if ( GUI.Button ( new Rect ( Screen.width * 0.05f , Screen.height * 0.5f , Screen.width * 0.4f , Screen.height * 0.15f ) , "Request Amazon Interstitials") )
		{
			AmazonAds.current.RequestInterstitials();
		}

		if ( GUI.Button ( new Rect ( Screen.width * 0.55f , Screen.height * 0.5f , Screen.width * 0.4f , Screen.height * 0.15f ) , "Show Amazon Interstitials") )
		{
			AmazonAds.current.ShowInterstitials();
		}

	}


	// Invokes when a banner loads
	private void OnBannerLoaded ( string network )
	{

	}

	// Invokes when a banner fails to loads
	private void OnBannerFailed ( string network )
	{
		
	}

	// Invokes when an interstitial ad is loaded
	private void OnInterstitialsLoaded ( string network )
	{
		if ( network == "amazon" )
		{
			AmazonAds.current.InterstitialLoaded = true;
		}
	}

	// Invokes when interstitial fails to display
	private void OnInterstitialsFailed ( string network )
	{
		if ( network == "amazon" )
		{
			AmazonAds.current.InterstitialLoaded = false;
		}
	}


	// Ad events posted from the plugin
	public void OnAdEvent ( string strMessage )
	{


		// Amazon banner loaded
		if ( strMessage == "amazon-yes" )
		{
			OnBannerLoaded ("amazon");
		}

		// Amazon banner failed
		if ( strMessage == "amazon-no" )
		{
			OnBannerFailed ( "amazon" );
		}

		// Admob interstitials loaded
		if ( strMessage == "amazon-interstitial-yes" )
		{
			OnInterstitialsLoaded("amazon");
		}
		
		// Admob interstitials failed
		if ( strMessage == "amazon-interstitial-no" )
		{
			OnInterstitialsFailed ("amazon");
		}

	}


}
