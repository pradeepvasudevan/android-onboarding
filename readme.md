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
init (<ID & V Client ID >, <ID &V Client secret>)
Init function sets the client id and secret for the ID&V verfication library

Usage:
`        Onboarding.init(
            ID_VERIFICATION_CLIENT_ID,
            ID_VERIFICATION_CLIENT_SECRET
        )`

**Start**
start(<url to navitate>)
start function starts the onboarding webview and navigate to the url given. The url typically set by PCA or BCA application

usage:
`Onboarding.start(onboaringUrl)`

**ID &V Start**
A javascript bridge to the SDK is required to start the ID&V process

web sdk has to implement a javascript method with the following interface name:
 **astoWebView**
call the postMessage with an ID&V session id
eg:
```
if (oS === 'Android') {
    window.astoWebView?.postMessage(sessionId);
} else if (oS === 'iOS') {
    //
}
```
ID&V documentation: [https://idvcore.pages.almuk.santanderuk.corp/developer-guidance-website/api-redoc/redoc.html](https://idvcore.pages.almuk.santanderuk.corp/developer-guidance-website/api-redoc/redoc.html)

#### Optional Methods
**ID & V Dynatrace**
Call the **setIDVDynatraceParams** method If the Dynatrace logs are required from the ID&V library
```
Onboarding.setDynatraceParams(
            sourceSystemId,
            userId,
            dynatraceAppId,
            dynatraceBeaconUrl,
            dynatraceUserOptIn
        )
```

When ID&V journey is completed, onboarding sdk will call the url
`<onboaringUrl>?idvComplete`
(eg: http://pca.co.uk?idvComplete where pca.co.uk is the onboarding url)
Can Override the above url using the method setIDVOnCompleteUrl
`Onboarding.setIDVOnCompleteUrl(onCompleteUrl)`

#### Onboarding Test App
The library project source comes with a test app to test the Onboarding SDK in isolation.
Replace the onboarding url in the main activity (OnboardingTestActivity) to test the SDK.
