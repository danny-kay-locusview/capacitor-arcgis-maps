# capacitor-arcgis-maps

A Capacitor plugin for ArcGIS Maps

## Install

```bash
npm install capacitor-arcgis-maps
npx cap sync
```

## API

<docgen-index>

* [`signIn(...)`](#signin)
* [`signOut()`](#signout)
* [`query(...)`](#query)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### signIn(...)

```typescript
signIn(options: SignInOptions) => Promise<void>
```

| Param         | Type                                                    |
| ------------- | ------------------------------------------------------- |
| **`options`** | <code><a href="#signinoptions">SignInOptions</a></code> |

--------------------


### signOut()

```typescript
signOut() => Promise<void>
```

--------------------


### query(...)

```typescript
query(options: QueryOptions) => Promise<QueryResult>
```

| Param         | Type                                                  |
| ------------- | ----------------------------------------------------- |
| **`options`** | <code><a href="#queryoptions">QueryOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#queryresult">QueryResult</a>&gt;</code>

--------------------


### Interfaces


#### SignInOptions

| Prop              | Type                |
| ----------------- | ------------------- |
| **`portalUrl`**   | <code>string</code> |
| **`clientId`**    | <code>string</code> |
| **`redirectUrl`** | <code>string</code> |
| **`licenseKey`**  | <code>string</code> |
| **`apiKey`**      | <code>string</code> |


#### QueryResult

| Prop                   | Type                                                            |
| ---------------------- | --------------------------------------------------------------- |
| **`displayFieldName`** | <code>string</code>                                             |
| **`fields`**           | <code>Field[]</code>                                            |
| **`fieldAliases`**     | <code><a href="#record">Record</a>&lt;string, string&gt;</code> |
| **`geometryType`**     | <code>'esriGeometryPoint' \| 'esriGeometryPolyline'</code>      |
| **`spatialReference`** | <code><a href="#record">Record</a>&lt;string, string&gt;</code> |
| **`features`**         | <code>Feature[]</code>                                          |
| **`url`**              | <code>string</code>                                             |
| **`layerId`**          | <code>number</code>                                             |
| **`layerName`**        | <code>string</code>                                             |
| **`symbology`**        | <code>any</code>                                                |


#### Field

| Prop         | Type                |
| ------------ | ------------------- |
| **`name`**   | <code>string</code> |
| **`type`**   | <code>string</code> |
| **`alias`**  | <code>string</code> |
| **`length`** | <code>number</code> |


#### Feature

| Prop             | Type                                                         |
| ---------------- | ------------------------------------------------------------ |
| **`geometry`**   | <code>{ x: number; y: number; } \| number[][]</code>         |
| **`attributes`** | <code><a href="#record">Record</a>&lt;string, any&gt;</code> |


#### QueryOptions

| Prop           | Type                                                                     |
| -------------- | ------------------------------------------------------------------------ |
| **`layerUrl`** | <code>string</code>                                                      |
| **`where`**    | <code>string</code>                                                      |
| **`limit`**    | <code>number</code>                                                      |
| **`bbox`**     | <code>{ xmin: number; ymin: number; xmax: number; ymax: number; }</code> |


### Type Aliases


#### Record

Construct a type with a set of properties K of type T

<code>{
 [P in K]: T;
 }</code>

</docgen-api>
