Zendesk Unity3D Plugin
======================

This is a Unity plugin that wraps the iOS and Android Zendesk Support SDKs. Review the [CHANGELOG](./CHANGELOG.md) for details about upgrading from previous versions.

This is an open source project, and is not directly supported by Zendesk. Check out the [CONTRIBUTING](./CONTRIBUTING.md) page to find out how you can make changes or report issues.

## Requirements

- Unity 2018.1
- Using the Gradle build system

### iOS requirements

- Xcode 9.0+
- iOS 9 to 11
- Android requirements also have to be met, even if only building the iOS plugin.

### Android requirements

Most requirements will be downloaded automatically. You will have to ensure that some components are up to date in the Android SDK Manager.

- Android API 16 (4.1) and above.
- Android SDK Build-tools 27.0.1
- Latest version of Android Support Repository

## Integrating the Plugin

0. **Note:** The refactoring of the build system is not yet complete. In future we will publish the 
artifact for the Android plugin on a Maven repo and pull it in from there. Without that step in place,
you are still required to build it locally, deploy it to some repo, and then pull it into 
your Unity project from there. `android-plugin/build.gradle` is currently configured to deploy to 
`file:///tmp/repo`.

1. Import the Zendesk Support SDK for Android into your Unity project 

    * Confirm that your Unity project is using Gradle for Android. Player Settings -> Publishing 
    Settings -> Build -> Build System.
    * Tick the `Custom Gradle Template` checkbox.
    * Open the Gradle file (by default, this is `Assets/Plugins/Android/mainTemplate.gradle`).
    * Add the Zendesk repository to the `repositories` section:
    ```groovy
    repositories {
    		maven { 
    			url 'file:///tmp/repo'
 			}
    	}
    ``` 
    * **Note:** As detailed in Step 0, this is currently configured to use a local tmp dir. This 
    will be replaced in future with an online Maven repo.
    
2. Import the Zendesk Support SDK for iOS into your Unity project

    * Copy the contents of `ios-plugin/setup-src` into `Assets/Plugins/iOS` in your Unity project.
    * Copy the contents of `ios-plugin/src` into `Assets/Plugins/iOS` in your Unity project.  
    
    You may see some errors like this: `Could not create texture from Assets/Plugins/iOS/ZendeskSDK.bundle/{name}.png: File could not be read`.
    These are safe to ignore and will disappear when you build the project for iOS. You also may need to ensure that the `MessageUI`, `Security` and `MobileCoreServices` frameworks have been added to the project that Unity exports to Xcode. These frameworks can be added by selecting the correct target in Xcode and then selecting the aforementioned frameworks in the `Linked Frameworks and Libraries` under the `General` tab.
    
3. Import the Zendesk Unity plugin scripts into your Unity project
    
    * Copy the contents of `unity-src/scripts` into `Assets/Plugins/Zendesk/Scripts` in your Unity 
    project. 


## Using the Plugin

### Viewing the Sample 

We provide a sample script called `ZendeskTester.cs` in the `sample` directory. 
    
    Copy this to your assets folder and attach it to a `GameObject` in your scene. When you run the scene, you should see 
    a number of buttons provided by the script as a quick interface to the Support SDK. 

### Creating your own class that uses Zendesk

    * To use the Zendesk SDK in Unity, you must create a class that extends `MonoBehaviour` and attach it to a `GameObject` in your scene.
    * Include the following two methods in your class:

    ```c#
    // initialize Zendesk and set an identity. See ZendeskExample.cs for more information
    void Awake() {
        ZendeskSDK.ZDKConfig.Initialize (gameObject, "https://{subdomain}.zendesk.com", "{applicationId}", "{oauthClientId}"); // DontDestroyOnLoad automatically called on your supplied gameObject
        ZendeskSDK.ZDKConfig.AuthenticateAnonymousIdentity();
    }

    // must include this method for any zendesk callbacks to work
    void OnZendeskCallback(string results) {
        ZendeskSDK.ZDKConfig.CallbackResponse (results);
    }
    ```


## App Configuration and Zendesk App Interfaces

Example C#:

    c#
    ZendeskSDK.ZDKHelpCenter.ShowHelpCenter();
    ZendeskSDK.ZDKHelpCenter.ShowHelpCenter(options);

    ZendeskSDK.ZDKRequests.ShowRequestCreation
    ZendeskSDK.ZDKRequests.ShowRequestCreationWithConfig(config)

App configuration and the Zendesk Help Center and Requests interfaces can be found in the 
`/Assets/Zendesk` folder and are named:

* ZDKConfig.cs
* ZDKHelpCenter.cs
* ZDKRequests.cs
* ZDKPush.cs
* ZDKLogger.cs

## Zendesk Data Providers

The Zendesk SDK provider interfaces can be found in the `/Assets/Zendesk` folder and are named:

* ZDKAvatarProvider.cs
* ZDKHelpCenterProvider.cs
* ZDKRequestProvider.cs
* ZDKSettingsProvider.cs
* ZDKUploadProvider.cs
* ZDKUserProvider.cs

Example C#:

    c#
    ZendeskSDK.ZDKRequestProvider.GetAllRequests((results, error) => {
        if (error != null) {
            Debug.Log("ERROR: ZDKRequestProvider.GetAllRequests - " + error.Description);
        }
        else {
            Debug.Log("GetAllRequests returned results");
            foreach(Hashtable request in results) {
                Debug.Log(String.Format("RequestId: {0}", request["requestId"]));
            }
        }
    });


## Push notifications

Enabling and disabling push notifications for the current user is pretty straightforward.

    c#
    if (!pushEnabled) {
        ZendeskSDK.ZDKPush.EnableWithIdentifier("{device-or-channel-identifier}", (result, error) => {
            if (error != null) {
                Debug.Log("ERROR: ZDKPush.Enable - " + error.Description);
            }
            else {
                pushEnabled = true;
                Debug.Log("ZDKPush.Enable Successful Callback - " + MakeResultString(result));
            }
        });
    } else {
        ZendeskSDK.ZDKPush.Disable("device-or-channel-identifier", (result, error) => {
            if (error != null) {
                Debug.Log("ERROR: ZDKPush.Disable - " + error.Description);
            }
            else {
                pushEnabled = false;
                Debug.Log("ZDKPush.Disable Successful Callback - " + MakeResultString(result));
            }
        });
    }

There is an an example of this in the `ZendeskTester.cs` script file.

Notifications are a complex, OS-dependent feature. We provide the interfaces for enabling and disabling push. To handle incoming push messages you will need to configure the Urban Airship Unity SDK or the GCM / APNS SDKs.

### Request Updates API

In version 1.10.0.1 of the Support SDK, the [Request Updates API](https://developer.zendesk.com/embeddables/docs/android/show_open_requests#check-for-updates-on-your-requests) was added to allow querying for updates on requests without having to start the UI. Please note that the API is disabled when push notifications are enabled. Push integration should remove the need to query for request updates.

See `ZDKRequestProvider.cs` for the Request Updates methods: `GetUpdatesForDevice` and `MarkRequestAsRead`.

## Interface customization

### iOS

Zendesk application customization can be specified with IOSAppearance. ZenColor supports rgb and rbga values.

Example C#:

```c#
IOSAppearance appearance = new IOSAppearance ();
appearance.StartWithBaseTheme ();

appearance.SetPrimaryTextColor(new ZenColor (1.0f, 1.0f, 0f));
appearance.SetSecondaryTextColor (new ZenColor (1.0f, 0f, 0f));
appearance.SetPrimaryBackgroundColor(new ZenColor(0f, 0f, 1.0f));
appearance.SetSecondaryBackgroundColor (new ZenColor (0f, 1f, 0f));
appearance.SetMetaTextColor (new ZenColor (0.5f, 0f, 0f));
appearance.SetEmptyBackgroundColor (new ZenColor (0.5f, 0.5f, 0f));
appearance.SetSeparatorColor (new ZenColor (0.5f, 0f, 0.5f));
appearance.SetInputFieldColor (new ZenColor(0.5f, 0.7f, 0.2f));
appearance.SetInputFieldBackgroundColor(new ZenColor(0.9f, 0.1f, 0.9f));

appearance.ApplyTheme ();
```


### Android

By default, the Android Unity plugin uses a Material Design (`AppCompat`) light theme with a dark 
ActionBar (`Toolbar`). The primary colour is blue and the accent is yellow (see `android-plugin/src/main/res/values/colors.xml`).

To customize these values, you must edit this file as required, and then re-build and deploy the 
android-plugin artifact. See [Rebuilding the Android Plugin](#rebuilding-the-android-plugin) for 
details on how to do this. 
   
Include your style changes in:

`/sdk_unity_plugin/android-plugin/src/main/res/values/styles.xml`

The default `styles.xml` defines a theme called `UnityTheme`. This is then referenced by the `AndroidManifest.xml` file in your Unity project at `/Assets/Plugins/Android`.

To find defined styles and examples, see:

https://developer.zendesk.com/embeddables/docs/android/customize_the_look

## Help Center Appearance Customization

Custom Help Center articles are styled with CSS that can be specified in the following files.

### iOS

`/Assets/Plugins/iOS/ZendeskSDK.bundle/help_center_article_style.css`

### Android

On Android, any customization of the Help Center article CSS file must be done before the artifact is 
built. See [Rebuilding the Android plugin](#rebuilding-the-android-plugin) for details on how to do this. The file is located at:

`/sdk_unity_plugin/android-plugin/src/main/assets/help_center_article_style.css`

## String and Localization Customization

Custom strings and localizations can be specified in the following files. To change the default strings in your application, add replacements to the string you wish to modify. Make sure to include placeholders in the replacement of any strings that contain them.

### iOS

Strings are specified in plist files, one for each Locale. Each locale is a separate `[Locale]` folder.

`/Assets/Plugins/iOS/ZendeskSDKStrings.bundle/[Locale].lproj/Localizable.strings`

To find list of strings, see:

https://developer.zendesk.com/embeddables/docs/ios/localize_text

### Android

Strings are specified in xml files, one for each Locale. Each locale is a separate `[Locale]` folder. 
On Android, these files must be edited before the plugin is built. See [Rebuilding the Android plugin](#rebuilding-the-android-plugin). 

`/Assets/Plugins/Android/zendesk-lib/res/values-[Locale]/strings.xml`

To find list of strings, see:

https://developer.zendesk.com/embeddables/docs/android/localize_text

## Rebuilding the Android plugin

Some customizations will require you to alter values in the `android-plugin` source code, which means 
that the published artifact of the plugin is not suitable for your project. You can re-build and 
re-deploy the artifact to a Maven repo that you control, and then add this repository to your Unity
project's Gradle file to pull the dependency from there instead. 

**Note:** In this example, we use a local temp directory (`tmp/repo`), but it could be any Maven repo you can push/pull from. 

1. Open up `android-plugin/build.gradle`, and find the `uploadArchives` task which defines the 
repository it publishes to:

    ```groovy
    apply plugin: 'maven'
    
    afterEvaluate { project ->
        uploadArchives {
            repositories {
                mavenDeployer {
    
                    snapshotRepository(url: "file:///tmp/repo")
    
                    repository(url: "file:///tmp/repo")
    ```

2. Customise `repository` (and/or `snapshotRepository`, if you are using a `-SNAPSHOT` build number)
to alter where the artifact is deployed to. In this example, the values are both pointing to a local 
temp repo. 

3. Run `./gradlew uploadArchives` in a command line to publish the artifact to the specified location.

4. In your Unity Project's custom Gradle file (`mainTemplate.gradle`), look for the `repositories` 
section. It should look something like this:

    ```groovy
    repositories {
           
            google()
            jcenter()
    
            maven {
                url 'https://zendesk.jfrog.io/zendesk/repo'
            }
    
            maven {
                url 'https://oss.sonatype.org/content/repositories/snapshots'
            }
    
        }
    ```
    
5. Add the repo specified in the `uploadArchives` task just inside the `repositories` section, above 
the other repos listed. In this example (the local Maven repo), this 
would be:

    ```groovy
    maven { 
    			url 'file:///tmp/repo'
    }
    ```
    
Your Unity project should now pull your customised dependency from your custom Maven repo.
