//
//  ContactProvider.m
//  BigDataVoice
//
//  Created by Luc Belliveau on 2017-05-07.
//  Copyright © 2017 Luc Belliveau. All rights reserved.
//

#import <Contacts/Contacts.h>
#import "ContactProvider.h"

@implementation NSObject (BVJSONString)

-(NSString*) bv_jsonStringWithPrettyPrint:(BOOL) prettyPrint {
  NSError *error;
  NSData *jsonData = [NSJSONSerialization dataWithJSONObject:self
                                                     options:(NSJSONWritingOptions)    (prettyPrint ? NSJSONWritingPrettyPrinted : 0)
                                                       error:&error];

  if (! jsonData) {
    NSLog(@"bv_jsonStringWithPrettyPrint: error: %@", error.localizedDescription);
    return @"{}";
  } else {
    return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
  }
}
@end


@implementation ContactProvider

- (void) prepareContacts
{
  if ([CNContactStore class]) {
    self.contacts = [[NSMutableArray alloc] init];
    //ios9 or later
    CNEntityType entityType = CNEntityTypeContacts;
    if( [CNContactStore authorizationStatusForEntityType:entityType] == CNAuthorizationStatusNotDetermined)
    {
      CNContactStore *contactStore = [[CNContactStore alloc] init];
      [contactStore requestAccessForEntityType:entityType completionHandler:^(BOOL granted, NSError * _Nullable error) {
        if(granted){
          [self getAllContact];
        }
      }];
    }
    else if( [CNContactStore authorizationStatusForEntityType:entityType]== CNAuthorizationStatusAuthorized)
    {
      [self getAllContact];
    }
  }
}

-(void)getAllContact
{
  if([CNContactStore class])
  {
    //iOS 9 or later
    NSError* contactError;
    CNContactStore* addressBook = [[CNContactStore alloc]init];
    [addressBook containersMatchingPredicate:[CNContainer predicateForContainersWithIdentifiers: @[addressBook.defaultContainerIdentifier]] error:&contactError];
    NSArray * keysToFetch =@[CNContactEmailAddressesKey, CNContactPhoneNumbersKey, CNContactFamilyNameKey, CNContactGivenNameKey, CNContactImageDataAvailableKey, CNContactThumbnailImageDataKey];
    CNContactFetchRequest * request = [[CNContactFetchRequest alloc]initWithKeysToFetch:keysToFetch];
    [addressBook enumerateContactsWithFetchRequest:request error:&contactError usingBlock:^(CNContact * __nonnull contact, BOOL * __nonnull stop){
      NSString *displayName = [contact.givenName stringByAppendingString:@" "];
      displayName = [displayName stringByAppendingString:contact.familyName];
      NSString *mobileNumber = @"";

      for (CNLabeledValue<CNPhoneNumber*>* number in contact.phoneNumbers) {
        if ([number.label isEqualToString:CNLabelPhoneNumberMobile]) {
          mobileNumber =  [[number valueForKey:@"value"] valueForKey:@"digits"];
          break;
        } else if ([mobileNumber isEqualToString:@""]) {
          mobileNumber =  [[number valueForKey:@"value"] valueForKey:@"digits"];
        }
      }
      NSMutableDictionary *user = [[NSMutableDictionary alloc] init];
      [user setValue:contact.identifier forKey:@"key"];
      [user setValue:displayName forKey:@"name"];
      [user setValue:mobileNumber forKey:@"number"];
      if (contact.imageDataAvailable) {
        NSString *avatar = @"data:image/png;base64,";
        [user setValue:[avatar stringByAppendingString:[contact.thumbnailImageData base64EncodedStringWithOptions:0]] forKey:@"avatar"];
      }
      [self.contacts addObject:user];
    }];
  }
}

@end
