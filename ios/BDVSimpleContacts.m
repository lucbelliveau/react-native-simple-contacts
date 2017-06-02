
#import "BDVSimpleContacts.h"
#import "ContactProvider.h"

@implementation BDVSimpleContacts

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(getProfile:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    resolve([NSNumber numberWithInteger: 1]);
}

RCT_EXPORT_METHOD(findContactByNumber:(NSString*) number resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    resolve([NSNumber numberWithInteger: 1]);
}

RCT_EXPORT_METHOD(getContacts:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    ContactProvider *cp = [ContactProvider new];
    [cp prepareContacts];
    resolve([cp.contacts bv_jsonStringWithPrettyPrint:false]);
}

@end
  
