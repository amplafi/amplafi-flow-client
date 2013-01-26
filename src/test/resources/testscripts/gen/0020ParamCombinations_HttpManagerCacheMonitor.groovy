request("HttpManagerCacheMonitor", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("HttpManagerCacheMonitor", ["cachedUris":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
