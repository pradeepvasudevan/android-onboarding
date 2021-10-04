# Onboarding SDK 

This Android sdk supports the onboarding journey for the following features and following sections details how to use this SDK in an Android project.

- Personal Current Account
- Business Current Account


### Requirments

|                        | Language | Minimum API  |
|------------------------|----------|--------------------|
| OnboardingSDK      | Kotlin/Java   | 23             |

### Setup
Onboarding SDK is uploaded to the local nexus repository. Since being under development, its been uploaded to the following location
`https://nexus.almuk.santanderuk.corp/repository/firefly-mobility/android/ddv-test`
Once in production it will be moved to:
`https://nexus.almuk.santanderuk.corp/repository/maven-releases'`

In the project build gradle file add the url 
`maven {
            url 'https://nexus.almuk.santanderuk.corp/repository/firefly-mobility/android/ddv-test'
        }`

add dependency in the app build gradle file
`implementation "uk.co.santander:OnBoarding:1.6.0"`
Sync the project to make sure the the onboarding library can be included in the project.

### Usage

**Initialisation**
init (<ID & V Client ID >, <ID &V Client secret>, <Environment>)
Init function sets the client id and secret for the ID&V verification library
Init also takes a string value for the environment. acceptable values are "TEST" or "PROD"
where "PROD" is for the live environment.

Usage:
`        Onboarding.init(
            ID_VERIFICATION_CLIENT_ID,
            ID_VERIFICATION_CLIENT_SECRET,
            "TEST"
        )`
        
There are two methods introduced to set the proxy certificates for the DDV library to use to test
the application in our internal network. Call these method with proxy certificates

**Certificate Keys**
setDdvCertificateKeys(<Array of Strings>)
Set the key hashes. Empty array for the production version

these are the values given by the ID&V team for our internal network
{request ID&V these keys}

**Proxy Certificates**
setDdvCertificateResources(<Array of integer resource ids>)

ID&V team requires Android resource IDs of certificates in the raw folder of the resource

eg:
setDdvCertificateResources(arrayOf(R.raw.santander_ca_root, R.raw.diassl))

**Start**
start(<url to navitate>)
start function starts the onboarding webview and navigate to the url given. The url typically set by PCA or BCA application

usage:
`Onboarding.start(onboaringUrl)`

**ID &V Start**
A javascript bridge to the SDK is required to start the ID&V process

web sdk has to implement a javascript method with the following interface name:
 **onboardingWebView**
call the postMessage with an ID&V session id
eg:
```
if (oS === 'Android') {
    window.onboardingWebView?.postMessage(sessionId);
} else if (oS === 'iOS') {
    //
}
```
ID&V documentation: [https://idvcore.pages.almuk.santanderuk.corp/developer-guidance-website/api-redoc/redoc.html](https://idvcore.pages.almuk.santanderuk.corp/developer-guidance-website/api-redoc/redoc.html)

### Other Javascript Interface calls

**exit()**
web sdk can call exit() method to close the native webview from Javascript
```aidl
window.onboardingWebView.exit();
```

**openUrl(url)**
use this Javascript method to open a URL on an external browser window (also closes the webview)
```aidl
window.onboardingWebView.openUrl('http://www.bing.com');
```
Note: url needs to be in a valid URL format . eg: needs to include http:// etc

The sdk also allows to set an allowed domains list using the method
**setWhitelistDomains(domains)**
where domains is a comma separated string list of domain names
eg: setWhitelistDomains("www.google.com,bing.com,microsoft.com")

#### Optional Methods
**ID & V Dynatrace**
Call the **setDdVDynatraceParams** method If the Dynatrace logs are required from the ID&V library
```
Onboarding.setDdVDynatraceParams(
            dynatraceAppId,
            dynatraceBeaconUrl,
            dynatraceUserOptIn
        )
```

**ID & V Gass**
Call the **setIDVGassParams** method If the Gass logs are required from the ID&V library
```
Onboarding.setIDVGassParams(
            sourceSystemId,
            userId
        )
```

When ID&V journey is completed, onboarding sdk will call the url
`<onboaringUrl>?idvComplete`
(eg: http://pca.co.uk?idvComplete where pca.co.uk is the onboarding url)
Can Override the above url using the method setIDVOnCompleteUrl
`Onboarding.setIDVOnCompleteUrl(onCompleteUrl)`

checkNfcEnabled - call this method with false if NFC enabled check on start up needs to be skipped.
default:  true
`Onboarding.checkNfcEnabled(false)`



#### Onboarding Test App
The library project source comes with a test app to test the Onboarding SDK in isolation.
Replace the onboarding url in the main activity (OnboardingTestActivity) to test the SDK.
