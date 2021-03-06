/**
 * 
 */
package org.amplafi.dbanalysis;
import groovy.transform.Canonical;
import groovy.transform.TupleConstructor;
/**
 * @author paul 
 * Script for finding db table relationships that pat is too busy to tell us about
 * Generally give developers and DBA's a headstart by highlighting indirect relationships between tables in the db. 
 * 
 * This is run from the dbRelationships DSL script
 * 
 * you run this with something like:  
 * 
 * FAdmin.bat dbRelationships fromTable=message_points toTable=users username=amplafi password=amplafi schema=amplafi
 *
 * or with the ant target
 * 
 * and it outputs something like:
 * 
 * message_points --->providers --->users users
 * __________________________________________________
 * message_points -- column END_POINT_PROVIDER links to ->
 * providers -- column DEF_CONTACT_USER links to ->
 * users
 * 
 *  ++++++++++++++++ Example select +++++++++++++++++++
 *
 * select  message_points.*,
 *     providers.*,
 *     users.* 
 * from  message_points,
 *     providers,
 *     users
 * where message_points.END_POINT_PROVIDER = providers.ID
 *  and providers.DEF_CONTACT_USER = users.ID;
 * 
 * And other longer paths
 * 
 *  handy for investigating the db structure and writing queries.
 *  
 *  NOTE not all paths will be output. Currently if a table is visited by one path then it will be excluded from other paths. This means many useful
 *  paths are lost. For example the abouve query should have gone through roles but it found the providers.DEF_CONTACT_USER path first.
 *  will address this later
 */
@Canonical // creates constructor toSting, equals, hashCode
public class PathFinder {
    def schema
    def username
    def password 
    private static final int MAX_PATH_LENGTH = 5;
    
    /* Consider tables to be nodes in a network */
    def tables = []
    /* map of table name to table object */
    def lookup = [:]
    
    /* Groovy sql object */
    private def sql
    private def sqlTargetSchema
    
    def PathFinder(schema,username,password){
        this.schema = schema;
        this.username = username;
        this.password = password;
        sql = Sql.newInstance("jdbc:mysql://localhost:3306/information_schema", username,password, "com.mysql.jdbc.Driver");
        sqlTargetSchema  = Sql.newInstance("jdbc:mysql://localhost:3306/" + schema, username,password, "com.mysql.jdbc.Driver");
    }
    
    /**
     * @param fromTableName
     * @param toTableName
     */
    def void findPath(String fromTableName, String toTableName){
        println "${fromTableName}, ${toTableName} " + this;
        listTables();
        
        if (lookup[fromTableName] == null){
            println "Table ${fromTableName} not found in database ${schema}"
            return;	
        }

        if (lookup[toTableName] == null){
            println "Table ${toTableName} not found in database ${schema}"
            return;
        }

                
        // Print all the tables	
        tables.each{ tb ->
            println tb.name;
        }
        
        listRelationships();
        
        def from = lookup[fromTableName];
        def to = lookup[toTableName];
        
        def successfulPaths = [];
        searchRec(from,to, new Path(), successfulPaths);
        
        successfulPaths.sort{ path ->
            path.steps.size();
        }
         
        def querySet = [];        
   
        println "Paths :"
        successfulPaths.each{ path ->
            println "Path :"
            
            StringBuffer briefPath = new StringBuffer();
            StringBuffer detailPath = new StringBuffer();
            def tables = [] as Set;
            def conditions = [] as Set;

    
            
            for (int i = 0; i< path.steps.size(); i++){
                
                TableNode currentStep = path.steps[i];
                briefPath << "${currentStep.name} "
                detailPath << "${currentStep.name} "
                tables << currentStep.name;
        
                if (path.steps.size() > i + 1){
                    TableNode nextStep = path.steps[i + 1];

                    if (currentStep != nextStep){	
                        def linkDetail = currentStep.linksDetail[nextStep.name];

                        if (linkDetail != null){
                            if (linkDetail.fromTableNode == currentStep.name ){
                                detailPath << "-- column ${linkDetail.columnName} links to -> \n"
                                briefPath << "--->"
                                
                            }
                            
                            if (linkDetail.toTableNode == currentStep.name ){
                                detailPath << "<-- is referenced by ${linkDetail.columnName} in -- \n"
                                briefPath << "<---"
                            }
                            conditions << "${linkDetail.fromTableNode}.${linkDetail.columnName} = ${linkDetail.toTableNode}.${linkDetail.toTableColumn}"
                        }
                    }
                }
            }
            
            String exampleSql = exampleSelect(tables,conditions);
            def resultNum = executeExampleSelectSql(exampleSql);
            

            if (resultNum > 0){
                querySet << ["sql":exampleSql,"recordNum":resultNum];
            }
           
            
            println briefPath;
            println "__________________________________________________";
            
            println detailPath;
            println "++++++++++++++++ Example select +++++++++++++++++++";
            println "";
            //println exampleSql;
            println "";
     
            
            println "#####################################################################################";
        }


        querySet.sort{ query ->
            query["recordNum"];
        }

       println "++++++++++++++++ Most Promsing Queries +++++++++++++++++++";
        querySet.each{ query ->
            println query["sql"];
            println "Number of results " + query["recordNum"];
            println "--------------------------------------------";
        }

		println "Done"
	}
	
	/**
	 * Reccursive search the network of tables to find paths
	 * @param from
	 * @param to
	 * @param currentPath - just and array of table nodes.
	 * @param sucessfullPaths - when a path hits the target it is moved to this list of lists.
	 * @return
	 */
	def searchRec(TableNode from, TableNode to, Path currentPath, sucessfullPaths){
		currentPath.lookup.add(from);
        if (currentPath.steps.size() < MAX_PATH_LENGTH) {
            currentPath.steps << from;
            from.linksTo.each{ linksTo ->
                if (linksTo == to ){
                    currentPath.steps << linksTo; 
                    sucessfullPaths << currentPath;
                    return;
                }
                
                if (!currentPath.lookup.contains(linksTo)){
                    searchRec(linksTo, to, currentPath.clone(), sucessfullPaths);
                }
                
            }

            from.linksFrom.each{ linksFrom ->
                if (linksFrom == to ){
                    currentPath.steps << linksFrom;
                    sucessfullPaths << currentPath;
                    return;
                }
                
                if (!currentPath.lookup.contains(linksFrom)){
                    searchRec(linksFrom, to, currentPath.clone(), sucessfullPaths)
                }
            }
        }

    }
    
    
    /**
     * Build a list of tables in this schema.
     * @return
     */
    def listTables(){
        sql.eachRow("select TABLE_NAME from TABLES where  TABLE_SCHEMA= ${schema};") { row ->
            def table = new TableNode(row.TABLE_NAME);
            tables << table;
            lookup[row.TABLE_NAME] = table;
        }
    }

	/**
	 * Construct a network of relationships between the tables. 
	 * @return
	 */
	def listRelationships(){
		// loop over all the tables and find the tables that reference them	
		tables.each{ tb ->
			println "########### Table ${tb.name} References ############"
			// list all the tables this table references. 
			sql.eachRow("""select TABLE_NAME,COLUMN_NAME,CONSTRAINT_NAME, REFERENCED_TABLE_NAME,REFERENCED_COLUMN_NAME from KEY_COLUMN_USAGE where TABLE_SCHEMA = ${schema} AND TABLE_NAME = ${tb.name};""") { row ->
                println "      ${row}  "
				if (row.REFERENCED_TABLE_NAME != null){
				    println "      ${row.REFERENCED_TABLE_NAME}.${row.REFERENCED_COLUMN_NAME}  "
					def referenced = lookup[row.REFERENCED_TABLE_NAME];
					if (referenced == null){
						throw new Exception("how is table " + row.REFERENCED_TABLE_NAME + "not in the lookup?")
					}
					
					def detail = new LinkDetail(tb.name,row.COLUMN_NAME,referenced.name,row.REFERENCED_COLUMN_NAME );
								
					tb.linksTo << referenced;
					tb.linksDetail[row.REFERENCED_TABLE_NAME] = detail;  
					
					referenced.linksFrom << tb; 
					referenced.linksDetail[tb.name] = detail;
				}
			} 
		}
	}

    def String exampleSelect(tables,conditions){
        def sb = new StringBuffer();
        sb << "select "
        def sep = ""
        tables.each{ t ->
            sb << sep
            sb << " ${t}.*"
            sep = ",\n     "
        }
        sb << "\nfrom "
        sep = ""
        tables.each{ t ->
            sb << sep
            sb << " ${t}"
            sep = ",\n     "
        }
        
        sb << "\nwhere "
        sep = ""
        conditions.each{ c ->
            sb << sep
            sb << "${c}"
            sep = "\nand "
        }
        sb << ";"
        return sb.toString();
    }
    
    def executeExampleSelectSql(String query){
        def result = sqlTargetSchema.rows(query);
        def resultNum = result.size();
        return resultNum;
    }

    
}


/**
 * Class represeting a table
 */ 
@TupleConstructor
public class TableNode {

	def name;
	def linksTo = [];
	def linksFrom = [];
	
	def linksDetail = [:]


}

/**
 * Represents a path between TableNodes
 * Is basically an array of TableNodes with a set of the same nodes to
 * facilitate checking for loops.
 */
public class Path {
    def steps = [];
    def lookup = [] as Set;
    public Object clone(){
        def o = new Path();
        o.steps = this.steps.clone();
        o.lookup = this.lookup.clone();
        //  o.lookup = this.lookup;
        return o;
    }
}

/**
 *  Link detail
 */
@Canonical
public class LinkDetail {
    def fromTableNode
    def columnName
    def toTableNode
    def toTableColumn
}