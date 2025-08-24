# capacitor-acrgis-maps

A Capacitor plugin for ArcGIS Maps

## Install

```bash
npm install capacitor-acrgis-maps
npx cap sync
```

## API

<docgen-index>

* [`init(...)`](#init)
* [`signIn()`](#signin)
* [`testAuth(...)`](#testauth)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### init(...)

```typescript
init(initOptions: InitOptions) => Promise<void>
```

| Param             | Type                                                |
| ----------------- | --------------------------------------------------- |
| **`initOptions`** | <code><a href="#initoptions">InitOptions</a></code> |

--------------------


### signIn()

```typescript
signIn() => Promise<SignInResult>
```

**Returns:** <code>Promise&lt;<a href="#signinresult">SignInResult</a>&gt;</code>

--------------------


### testAuth(...)

```typescript
testAuth(testAuthOptions: TestAuthOptions) => Promise<TestAuthResult>
```

| Param                 | Type                                                        |
| --------------------- | ----------------------------------------------------------- |
| **`testAuthOptions`** | <code><a href="#testauthoptions">TestAuthOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#testauthresult">TestAuthResult</a>&gt;</code>

--------------------


### Interfaces


#### InitOptions

| Prop              | Type                |
| ----------------- | ------------------- |
| **`portalUrl`**   | <code>string</code> |
| **`clientId`**    | <code>string</code> |
| **`redirectUri`** | <code>string</code> |
| **`licenseKey`**  | <code>string</code> |
| **`apiKey`**      | <code>string</code> |


#### SignInResult

| Prop                 | Type                  |
| -------------------- | --------------------- |
| **`username`**       | <code>string</code>   |
| **`fullName`**       | <code>string</code>   |
| **`email`**          | <code>string</code>   |
| **`organizationId`** | <code>string</code>   |
| **`role`**           | <code>string</code>   |
| **`privileges`**     | <code>string[]</code> |
| **`tokenExpires`**   | <code>string</code>   |


#### TestAuthResult

| Prop                           | Type                  |
| ------------------------------ | --------------------- |
| **`portalUrl`**                | <code>string</code>   |
| **`isAuthenticated`**          | <code>boolean</code>  |
| **`username`**                 | <code>string</code>   |
| **`fullName`**                 | <code>string</code>   |
| **`email`**                    | <code>string</code>   |
| **`organizationId`**           | <code>string</code>   |
| **`role`**                     | <code>string</code>   |
| **`privileges`**               | <code>string[]</code> |
| **`orgTitle`**                 | <code>string</code>   |
| **`securedLayerUrl`**          | <code>string</code>   |
| **`securedLayerFeatureCount`** | <code>number</code>   |


#### TestAuthOptions

| Prop                  | Type                |
| --------------------- | ------------------- |
| **`securedLayerUrl`** | <code>string</code> |

</docgen-api>
