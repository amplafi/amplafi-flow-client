request("CategoriesList", ["fsRenderResult":"json"])

checkReturnedValidJson()
request("CategoriesList", ["configuration":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CategoriesList", ["deleteCategories":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CategoriesList", ["configuration":"bogusData","deleteCategories":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CategoriesList", ["originalDeleteCategories":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CategoriesList", ["configuration":"bogusData","originalDeleteCategories":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CategoriesList", ["deleteCategories":"bogusData","originalDeleteCategories":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CategoriesList", ["configuration":"bogusData","deleteCategories":"bogusData","originalDeleteCategories":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CategoriesList", ["defaultCategory":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
request("CategoriesList", ["configuration":"bogusData","defaultCategory":"bogusData","fsRenderResult":"json"])

checkReturnedValidJson()
