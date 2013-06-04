import static org.testng.Assert.*;

def old_key = getKey();
def old_apiVersion = getApiVersion();

def  key = callScript("..\\farreaches-customer-service-client\\src\\main\\resources\\commandScripts\\GetCusServiceKey.groovy");

setKey(key);
setApiVersion('su');
response = request("DatabaseTransactionManagement",[:]);

//check the response is correct.
def statusCode = getHttpStatusCode();
if(statusCode != 200){
	fail("the customer service key can't access the su api");
}

key = callScript("..\\farreaches-customer-service-client\\src\\main\\resources\\commandScripts\\GetKeyNotCusPermission.groovy",[:]);
setKey(key);
setApiVersion('su');

def response = request("DatabaseTransactionManagement",[:]);
statusCode = getHttpStatusCode();
if(statusCode == 200){
	fail("not only the customer service key can access the su api");
}


setKey(old_key);
setApiVersion(old_apiVersion);


