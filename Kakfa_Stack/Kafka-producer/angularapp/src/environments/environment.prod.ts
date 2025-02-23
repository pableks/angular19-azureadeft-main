export const environment = {
  production: true,
  msalConfig: {
    auth: {
      clientId: 'e908559c-d3c1-4a02-8590-20947ac08f7c',
      authority: 'https://duocetcdemo.b2clogin.com/duocetcdemo.onmicrosoft.com/B2C_1_DemoAzureETC_Login',
    },
  },
  apiConfig: {
    scopes: ['https://duocetcdemo.onmicrosoft.com/e908559c-d3c1-4a02-8590-20947ac08f7c/user.read'],
    uri: 'YOUR_API_URI', // Replace with your production API URI
  },
};
