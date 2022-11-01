package com.example.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

import javax.xml.transform.TransformerException;

import com.itextpdf.text.DocumentException;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Connectiontest {

	public static void main(String[] args) throws DocumentException, URISyntaxException, StorageException, IOException, GeneralSecurityException, TransformerException  {
		
		Connectiontest ob= new Connectiontest();
		String s=ob.returnValue();
		System.out.println(s);
		SpringApplication.run(Connectiontest.class, args);
	}

	@GetMapping("/")
	
	public String method2()
	{
		
		return "welcome to ContainerCheck";
	}
	
	@GetMapping("/testing")
	
	

		
	   
	    //public static void main( String[] args ) throws StorageException, URISyntaxException, GeneralSecurityException, TransformerException, IOException
		public String returnValue()throws StorageException, URISyntaxException, GeneralSecurityException, TransformerException, IOException
	    {
			  final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=mqbawblobstorage01;AccountKey=4eEEA1jiy/kEpf9PvN8ikjQeXGFODXXH33G+VPhUiyhqzF7K7RrwFg/0CDEBJpkaYzWArR1bW2XD+AStaWP6zg==;EndpointSuffix=core.windows.net";
	    	File text=File.createTempFile("text", ".txt");
	        	
	    		

	    		CloudStorageAccount storageAccount;
	    		CloudBlobClient blobClient = null;
	    		CloudBlobContainer container=null;

	    		
				storageAccount = CloudStorageAccount.parse(storageConnectionString);
				blobClient = storageAccount.createCloudBlobClient();
				container = blobClient.getContainerReference("bawcsl");
				CloudBlockBlob blob2 = container.getBlockBlobReference("Welcome.txt");
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
	