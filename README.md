# @sudeepjhs/nfc-plugin

NFC plugin for Capacitor

## Install

```bash
npm install @sudeepjhs/nfc-plugin
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`isAvailable()`](#isavailable)
* [`scanTag()`](#scantag)
* [`writeTag(...)`](#writetag)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### isAvailable()

```typescript
isAvailable() => Promise<{ available: boolean; }>
```

**Returns:** <code>Promise&lt;{ available: boolean; }&gt;</code>

--------------------


### scanTag()

```typescript
scanTag() => Promise<{ tagId: string; data: string; }>
```

**Returns:** <code>Promise&lt;{ tagId: string; data: string; }&gt;</code>

--------------------


### writeTag(...)

```typescript
writeTag(options: { data: string; }) => Promise<{ success: boolean; }>
```

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ data: string; }</code> |

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------

</docgen-api>
