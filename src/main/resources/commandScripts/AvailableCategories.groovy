description "AvailableCategories", "Available Categories", "none";

setApiVersion("apiv1");

// Request list of categories
def response = requestResponse("AvailableCategoriesFlow", ["fsRenderResult":"json"]);

checkError(response) ;

def categories = [:];

def entries = getResponseData();

//  Parse into a map of id -> id, name
for(entry in response.getResponseAsJSONArray()) {
    def id = entry.opt("entityId") ;
    def name = entry.opt("name") ;
    categories[id] = ["id": id, "name": name] ;
}

// Pretty print the map
printTaskInfo "Available Categories"
String tabularTmpl = '%1$10s%2$20s' ;
def headers =  ['Topic Id', 'Name'] ;
def keys = ['id', 'name'] ;
printTabularMap(categories, tabularTmpl, headers, keys);

// In case someone needs this.
return categories;