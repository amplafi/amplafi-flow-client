request("AccountCalendarInformation", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountCalendarInformation", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountCalendarInformation", ["timezone":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountCalendarInformation", ["configuration":"bogusData","timezone":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountCalendarInformation", ["browserUserTimeZoneOffset":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountCalendarInformation", ["configuration":"bogusData","browserUserTimeZoneOffset":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountCalendarInformation", ["timezone":"bogusData","browserUserTimeZoneOffset":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountCalendarInformation", ["configuration":"bogusData","timezone":"bogusData","browserUserTimeZoneOffset":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountCalendarInformation", ["browserUserTimeZone":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("AccountCalendarInformation", ["configuration":"bogusData","browserUserTimeZone":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
