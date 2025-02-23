// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  msalConfig: {
    auth: {
      clientId: 'e908559c-d3c1-4a02-8590-20947ac08f7c',
      authority: 'https://duocetcdemo.b2clogin.com/duocetcdemo.onmicrosoft.com/B2C_1_DemoAzureETC_Login',
    },
  },
  apiConfig: {
    scopes: ['https://graph.microsoft.com/User.ReadWrite.All'],
    uri: 'https://graph.microsoft.com/v1.0/me'
  },
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
