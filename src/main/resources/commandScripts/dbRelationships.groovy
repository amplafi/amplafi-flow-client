import org.amplafi.dbanalysis.PathFinder;

description "dbRelationships", "Script for finding db table relationships that we don't know about", [paramDef("schema","",true,"amplafi"),
																										paramDef("fromTable","Table at start for releationship",false,null),
																										paramDef("toTable","Table you want to get to",false,null),
																										paramDef("username","username able to access the information schema.",false,null),
																										paramDef("password","db password",false,null)];

					
// For newbies the variables below are injected due to the script description above.																									
def finder = new PathFinder(schema,username,password  );

// find all paths between these tables;
finder.findPath(fromTable, toTable)