package sqoop;

//Here I am using a table Persons, with columns PersonID and LastName
import org.apache.sqoop.client.SqoopClient;
import org.apache.sqoop.model.MConnection;
import org.apache.sqoop.model.MConnectionForms;
import org.apache.sqoop.model.MJob;
import org.apache.sqoop.model.MJobForms;
import org.apache.sqoop.model.MSubmission;
import org.apache.sqoop.validation.Status;
 
/**
 * @author  devan
 * @date 19-Sep-2013
 * @mail msdevanms@gmail.com
 */
 
public class SqoopImport {
 public static void main(String[] args) {
   
   
  String connectionString = "jdbc:mysql://YourMysqlIP:3306/test";
  String username = "YourMysqUserName";
  String password = "YourMysqlPassword";
  String schemaName = "YourMysqlDB";
  String tableName = "Persons";
  String columns = "PersonID,LastName"; //comma seperated column names
  String partitionColumn = "PersonID";
  String outputDirectory = "/output/Persons";
  String url = "http://YourSqoopIP:12000/sqoop/";
 
   
  SqoopClient client = new SqoopClient(url);
  //client.setServerUrl(newUrl);
  //Dummy connection object
  MConnection newCon = client.newConnection(1);
 
  //Get connection and framework forms. Set name for connection
  MConnectionForms conForms = newCon.getConnectorPart();
  MConnectionForms frameworkForms = newCon.getFrameworkPart();
  newCon.setName("MyConnection");
 
  //Set connection forms values
  conForms.getStringInput("connection.connectionString").setValue(connectionString);
  conForms.getStringInput("connection.jdbcDriver").setValue("com.mysql.jdbc.Driver");
  conForms.getStringInput("connection.username").setValue(username);
  conForms.getStringInput("connection.password").setValue(password);
 
  //frameworkForms.getIntegerInput("security.maxConnections").setValue(0);
 
  Status status  = client.createConnection(newCon);
  if(status.canProceed()) {
   System.out.println("Created. New Connection ID : " +newCon.getPersistenceId());
  } else {
   System.out.println("Check for status and forms error ");
  }
 
  //Creating dummy job object
  MJob newjob = client.newJob(newCon.getPersistenceId(), org.apache.sqoop.model.MJob.Type.IMPORT);
  MJobForms connectorForm = newjob.getConnectorPart();
  MJobForms frameworkForm = newjob.getFrameworkPart();
 
  newjob.setName("ImportJob");
  //Database configuration
  connectorForm.getStringInput("table.schemaName").setValue(schemaName);
  //Input either table name or sql
  connectorForm.getStringInput("table.tableName").setValue(tableName);
  //connectorForm.getStringInput("table.sql").setValue("select id,name from table where ${CONDITIONS}");
   
   
  connectorForm.getStringInput("table.columns").setValue(columns);
  connectorForm.getStringInput("table.partitionColumn").setValue(partitionColumn);
   
  //Set boundary value only if required
  //connectorForm.getStringInput("table.boundaryQuery").setValue("");
 
  //Output configurations
  frameworkForm.getEnumInput("output.storageType").setValue("HDFS");
  frameworkForm.getEnumInput("output.outputFormat").setValue("TEXT_FILE");//Other option: SEQUENCE_FILE / TEXT_FILE
  frameworkForm.getStringInput("output.outputDirectory").setValue(outputDirectory);
  //Job resources
  frameworkForm.getIntegerInput("throttling.extractors").setValue(1);
  frameworkForm.getIntegerInput("throttling.loaders").setValue(1);
 
  status = client.createJob(newjob);
  if(status.canProceed()) {
   System.out.println("New Job ID: "+ newjob.getPersistenceId());
  } else {
   System.out.println("Check for status and forms error ");
  }
  //Now Submit the Job
  MSubmission submission = client.startSubmission(newjob.getPersistenceId());
  System.out.println("Status : " + submission.getStatus());
  
 }
 
  
}