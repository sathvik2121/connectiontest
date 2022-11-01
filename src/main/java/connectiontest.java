
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import javax.xml.transform.TransformerException;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;


public class connectiontest {

	public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=mqbawblobstorage01;AccountKey=4eEEA1jiy/kEpf9PvN8ikjQeXGFODXXH33G+VPhUiyhqzF7K7RrwFg/0CDEBJpkaYzWArR1bW2XD+AStaWP6zg==;EndpointSuffix=core.windows.net";
   
    //public static void main( String[] args ) throws StorageException, URISyntaxException, GeneralSecurityException, TransformerException, IOException
	public String returnValue(String name)throws StorageException, URISyntaxException, GeneralSecurityException, TransformerException, IOException
    {
    	File text=File.createTempFile("text", ".txt");
        	
    		

    		CloudStorageAccount storageAccount;
    		CloudBlobClient blobClient = null;
    		CloudBlobContainer container=null;

    		
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			blobClient = storageAccount.createCloudBlobClient();
			container = blobClient.getContainerReference("bawcsl");
			CloudBlockBlob blob2 = container.getBlockBlobReference(name);
			FileOutputStream os= new FileOutputStream(text);
			blob2.download(os);
			 BufferedReader br= new BufferedReader(new FileReader(text));
			 String st;
			 String s="";
		        while ((st = br.readLine()) != null)
		        {
		        	 s=st;
		        }  
		        br.close();
			 
	 text.deleteOnExit();
	 //System.out.println(s);
	  return s;
			
			
    }
}
