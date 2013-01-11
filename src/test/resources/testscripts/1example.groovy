// You can put any groovy and most Java code in this file; including imports and calls to other classes
// this simple DSL offers 2 methods request() and expect()
println("1example.groovy");

request('HelloFlow',['param1':'dog','param2':'pig']);

expect("""
{	"validationErrors":{
      "flow-result":{
         "flowValidationTracking":[
            {
               "key":"MissingRequiredTracking",
               "parameters":[
                  "HelloFlow"
               ]
            },
            {
               "key":"flow.definition-not-found",
               "parameters":[

               ]
            }
         ]
      }
   }
}
""");
