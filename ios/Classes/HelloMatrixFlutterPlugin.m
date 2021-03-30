#import "HelloMatrixFlutterPlugin.h"
#if __has_include(<hello_matrix_flutter/hello_matrix_flutter-Swift.h>)
#import <hello_matrix_flutter/hello_matrix_flutter-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "hello_matrix_flutter-Swift.h"
#endif

@implementation HelloMatrixFlutterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftHelloMatrixFlutterPlugin registerWithRegistrar:registrar];
}
@end
