
# react-native-simple-contacts

> This library is a work in progress.  Additional features will be
> added as they become required.

## Getting started

`$ npm install react-native-simple-contacts --save`

### Mostly automatic installation

`$ react-native link react-native-simple-contacts`

## Usage
```javascript
import simpleContacts from 'react-native-simple-contacts';

// Get all contacts
simpleContacts.getContacts().then((contacts) => {
  // Do something with the contacts
});

// Get all contacts for filter string (eg: email address)
simpleContacts.getContactsByFilter("username@domain.example").then((contacts) => {
  // Do something with the contact
});

// Get a specific contact based on a phone number
simpleContacts.findContactByNumber(number).then((contact) => {
  // Do something with the contact
});

// Get the user's profile
simpleContacts.getProfile().then((profile) => {
  // Do something with the profile
});

```
### API

Function | Description
--- | ---
<nobr>**getContacts**()</nobr> | Returns an array of all contacts
<nobr>**getContactsByFilter**(*string*)</nobr> | Returns an array of contacts by filter (eg: by email address)
<nobr>**getProfile**()</nobr> | Return the user's profile.
<nobr>**findContactByNumber**(*number*)</nobr> | Return the contact that matches the provided number.

Simple contacts only returns the following fields:

Field | Description
--- | ---
key    | A unique identifier
name   | The contact's display name
avatar | The contact's photo if available
number | The contacts phone number

Returned contacts look like this:

```javascript
const contact = {
  "key": "contact_###",
  "name": "John Doe",
  "avatar": "uri://picture",
  "number": "1-555-555-5555"
}

```

## Manual installation

### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-simple-contacts` and add `BDVSimpleContacts.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libBDVSimpleContacts.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import ca.bigdata.voice.contacts.BDVSimpleContactsPackage;` to the imports at the top of the file
  - Add `new BDVSimpleContactsPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-simple-contacts'
  	project(':react-native-simple-contacts').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-simple-contacts/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-simple-contacts')
  	```
