package client;

//import java.lang.reflect.Field;

import java.io.File;

import java.io.IOException;

import java.nio.file.Path;

import java.util.List;
import java.util.List;
import java.util.concurrent.TimeUnit;

import oracle.stellent.ridc.IdcClient;
import oracle.stellent.ridc.IdcClientException;
import oracle.stellent.ridc.IdcClientManager;
import oracle.stellent.ridc.IdcContext;
import oracle.stellent.ridc.common.http.utils.RIDCHttpConstants;
import oracle.stellent.ridc.model.DataBinder;
import oracle.stellent.ridc.model.DataObject;
import oracle.stellent.ridc.model.DataResultSet;
import oracle.stellent.ridc.model.DataResultSet.Field;
import oracle.stellent.ridc.model.TransferFile;
import oracle.stellent.ridc.protocol.ServiceResponse; 
import java.util.concurrent.TimeUnit;

public class IntegrateWCPortal {
    IdcClientManager manager;
    IdcClient idcClient;
    IdcContext idcContext;
    DataBinder binder;
    public IntegrateWCPortal() throws IdcClientException {
        super();
        manager = new IdcClientManager(); 
        idcClient =  manager.createClient("idc://<ip or domain name>:4444");
        idcClient.getConfig ().setSocketTimeout (900000);  //in seconds
        idcClient.getConfig().setProperty("http.library",RIDCHttpConstants.HttpLibrary.apache4.name());
        idcContext = new IdcContext("weblogic", "weblogic123");
        binder = idcClient.createBinder();
        
    }
   
    
    
    public static void main(String[] args) {
       
        try {
            IntegrateWCPortal integrateWCPortal = new IntegrateWCPortal();
       System.out.println(integrateWCPortal.createFile("TestFile39","C:\\Users\\Ahmed\\Desktop\\TestFile19.txt"));
//            TimeUnit.SECONDS.sleep(4);
//            System.out.println(integrateWCPortal.getDocUrl("TestFile33"));

        } catch (IdcClientException e){//|InterruptedException e) {
            System.out.println("main message---> ");
            e.printStackTrace();
        }
    }
    
    public String createFile(String fileName,String filePath) throws IdcClientException {
     
      binder.putLocal ("IdcService", "CHECKIN_UNIVERSAL"); 
      binder.putLocal("fParentGUID", getFolderGUID("/ADAADocs"));
      binder.putLocal("IdcService","CHECKIN_UNIVERSAL"); // because getFolderGUID changed it so, i have to rest it.
      binder.putLocal ("dDocName", fileName);//"TestFile27"
      binder.putLocal ("dDocType", "Document");
      binder.putLocal ("dDocTitle", fileName);
      binder.putLocal("dDocAuthor", "weblogic");         
      binder.putLocal ("dSecurityGroup", "Public");
      binder.putLocal ("dRevisionID", "1");
//      binder.putLocal("UserTimeZone","America/New_York");
//      binder.putLocal ("dInDate","2008-04-07 13:20:00");
      
      // add a file      
      File file = new File(filePath);
        try {
            binder.addFile("primaryFile",  new TransferFile(file));
            ServiceResponse fileResponse = idcClient.sendRequest(idcContext, binder);            
//            System.out.println(fileResponse.getResponseAsString());             
        } catch (IOException e ) { 
            System.out.println("File message---> ");
            e.printStackTrace();
        }   
        
      return(getDocUrl(fileName));
    }
    public String getFolderGUID(String folderPath) throws IdcClientException {
           
            
            binder.putLocal("IdcService", "FLD_INFO");
            binder.putLocal("path", folderPath);

            // Execute service request
            ServiceResponse response = idcClient.sendRequest(idcContext, binder);
            DataResultSet folderResult = response.getResponseAsBinder().getResultSet("FolderInfo");

            // Retrieve folder GUID
            String folderGUID = folderResult.getRows().get(0).get("fFolderGUID").toString();
            System.out.println(folderGUID);
            // Return the first folder, there is only one.
            return folderGUID;
        }
    public String getDocUrl(String fileName) throws IdcClientException{
        System.out.println("-------------inside doc ------------");
            binder = idcClient.createBinder();
            binder.putLocal("IdcService", "DOC_INFO_BY_NAME");// DOC_INFO
            binder.putLocal ("dDocName", fileName);//"TestFile27"
            ServiceResponse response = idcClient.sendRequest (idcContext,binder);
            DataBinder respBinder = response.getResponseAsBinder();

        return respBinder.getLocal("DocUrl");
    }

}


