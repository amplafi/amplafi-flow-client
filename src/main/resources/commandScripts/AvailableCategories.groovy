description "AvailableCategories", "Available Categories", "none";

setApiVersion("apiv1");

// Request list of categories
request("AvailableCategoriesFlow", ["fsRenderResult":"json"]);
def categories = [:];

// We expect a JSON array if not then it is an error report
if(getResponseData() instanceof JSONArray) {
    def entries = getResponseData();
    
    //  Parse into a map of id -> id, name
    for(entry in getResponseData()) {
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
} else {
    println "ERROR: Cannot get the category list"
    prettyPrintResponse();
    return null;
}