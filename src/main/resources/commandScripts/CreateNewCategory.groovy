/**
 * Obtains a new category from the wire server
 */
description "CreateNewCategory", "create a new category", [paramDef("callbackHost","The host to callback to",true,"example.com")];

def category = null;


emitOutput( "NEW CATEGORY IS : " + category );


def response = requestResponse("Category",["callbackUri":"http://${callbackHost}/"]);

emitOutput( "response category = " + category);

return category;
