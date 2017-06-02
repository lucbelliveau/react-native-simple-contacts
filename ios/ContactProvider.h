//
//  ContactProvider.h
//  BigDataVoice
//
//  Created by Luc Belliveau on 2017-05-07.
//  Copyright © 2017 Facebook. All rights reserved.
//

#ifndef ContactProvider_h
#define ContactProvider_h

@interface ContactProvider : NSObject

@property NSMutableArray *contacts;

- (void)prepareContacts;

@end

@interface NSObject (BVJSONString)
-(NSString*) bv_jsonStringWithPrettyPrint:(BOOL) prettyPrint;
@end


#endif /* ContactProvider_h */
