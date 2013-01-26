request("UserCalendarSettings", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("UserCalendarSettings", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserCalendarSettings", ["timezone":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserCalendarSettings", ["configuration":"bogusData","timezone":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserCalendarSettings", ["browserUserTimeZoneOffset":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserCalendarSettings", ["configuration":"bogusData","browserUserTimeZoneOffset":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserCalendarSettings", ["timezone":"bogusData","browserUserTimeZoneOffset":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserCalendarSettings", ["configuration":"bogusData","timezone":"bogusData","browserUserTimeZoneOffset":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserCalendarSettings", ["browserUserTimeZone":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("UserCalendarSettings", ["configuration":"bogusData","browserUserTimeZone":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
