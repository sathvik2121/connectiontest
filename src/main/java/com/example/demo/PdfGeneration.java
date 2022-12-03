package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

import java.io.IOException;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import java.io.FileOutputStream;


import com.itextpdf.text.DocumentException;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;

import java.text.SimpleDateFormat;  
import java.util.Date;  

@RestController
@SpringBootApplication
public class PdfGeneration {

	public static void main(String[] args) throws InvalidKeyException, DocumentException, URISyntaxException, StorageException, UnsupportedEncodingException, MalformedURLException  {
		PdfGeneration ob= new PdfGeneration();
		String s=ob.run ("data1.xml");
		System.out.println(s);
		SpringApplication.run(PdfGeneration.class, args);
	}
@GetMapping("/")
	
	public String welcome()
	{
		
		return "welcome to pdf generation";
	}
	@GetMapping("/ContainerCheck")
	
	public List<String> method2() throws InvalidKeyException, URISyntaxException, StorageException, UnsupportedEncodingException, MalformedURLException
	{
		
		 final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=mqbawblobstorage01;AccountKey=4eEEA1jiy/kEpf9PvN8ikjQeXGFODXXH33G+VPhUiyhqzF7K7RrwFg/0CDEBJpkaYzWArR1bW2XD+AStaWP6zg==;EndpointSuffix=core.windows.net";
		 CloudStorageAccount storageAccount;
		 CloudBlobClient blobClient = null;
		 storageAccount = CloudStorageAccount.parse(storageConnectionString);
		 blobClient = storageAccount.createCloudBlobClient();
		 CloudBlobContainer container=blobClient.getContainerReference("xmlcontainer");
		 List<String> list=new ArrayList<String>();
		 for (ListBlobItem blobItem : container.listBlobs()) {
					URL url=blobItem.getUri().toURL();
					String s=FilenameUtils.getName(url.getPath());
	    		    String result = URLDecoder.decode(s,"utf-8");
	    			list.add(result);
	    				}
				 return list;
	}
	
	@GetMapping("/testing")
	
	public String run(@RequestParam( name="fileName") String name) throws DocumentException, URISyntaxException, StorageException, InvalidKeyException
	{
			final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=mqbawblobstorage01;AccountKey=4eEEA1jiy/kEpf9PvN8ikjQeXGFODXXH33G+VPhUiyhqzF7K7RrwFg/0CDEBJpkaYzWArR1bW2XD+AStaWP6zg==;EndpointSuffix=core.windows.net";
		    File xsltFile = null;
			CloudStorageAccount storageAccount;
			CloudBlobClient blobClient = null;
			String blobFileName=null;
			try {    
			
				storageAccount = CloudStorageAccount.parse(storageConnectionString);
				blobClient = storageAccount.createCloudBlobClient();
				CloudBlobContainer container2=blobClient.getContainerReference("fileaccess");
				CloudBlobContainer xmlcontainer=blobClient.getContainerReference("xmlcontainer");
				CloudBlobContainer buffercontainer=blobClient.getContainerReference("buffercontainer");	 
				xsltFile = File.createTempFile("template", ".xsl");
				CloudBlockBlob blob2 = container2.getBlockBlobReference("template.xsl");
		    	FileOutputStream xsloutput= new FileOutputStream(xsltFile);
		    	blob2.download(xsloutput);
		    	
		    	    File xmlFile=File.createTempFile("data", ".xml");
		    	    CloudBlockBlob blob3 = xmlcontainer.getBlockBlobReference(name);
		    	    FileOutputStream xmloutput= new FileOutputStream(xmlFile);
		    	    blob3.download(xmloutput);
		    	    StreamSource xmlSource = new StreamSource(xmlFile);
		    	    File outputFile=File.createTempFile("output", ".pdf");
		    	    FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
		    	    FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		    	    OutputStream out3;
		    	    out3 = new java.io.FileOutputStream( outputFile);
		    	    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out3);
		    	    TransformerFactory factory = TransformerFactory.newInstance();
		    	    Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));
		    	    Result res = new SAXResult(fop.getDefaultHandler());
		    	    transformer.transform(xmlSource, res);
		    	    out3.close();
		    	    File picture1=File.createTempFile("picture1",".jpg");
		    	    File picture2=File.createTempFile("picture2",".jpg");
		    	    PDDocument doc = PDDocument.load(outputFile);
		    	    PDPage page = doc.getPage(0);
		    	    CloudBlockBlob blob4 = container2.getBlockBlobReference("nvlaplogo.jpg");
		    	    FileOutputStream pic1= new FileOutputStream(picture1);
		    	    blob4.download(pic1);
		    	    CloudBlockBlob blob5 = container2.getBlockBlobReference("nvlapaddress.jpg");
		    	    FileOutputStream pic2= new FileOutputStream(picture2);
		    	    blob5.download(pic2);
		    	    PDImageXObject pdfimg= PDImageXObject.createFromFile(picture1.getAbsolutePath(), doc);
		    	    PDImageXObject pdfimg1= PDImageXObject.createFromFile(picture2.getAbsolutePath(), doc);
		    	    PDPageContentStream image= new PDPageContentStream(doc, page,AppendMode.APPEND, true, true);
		    	    image.drawImage(pdfimg, 330, 685,200,100);
		    	    image.drawImage(pdfimg1, 70, 685,200,100);
		    	    image.close();
		    	    doc.save(outputFile.getAbsoluteFile());
		    	    String fileNameWithOutExt = FilenameUtils.removeExtension(name);
		    	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");  
		    	    Date date = new Date();
		    	    String fileName=fileNameWithOutExt+"_"+formatter.format(date);
		    	    doc.close();
		    	    CloudBlockBlob blob = buffercontainer.getBlockBlobReference(fileName+".pdf");
		    	    blobFileName=fileName+".pdf";
		    	    blob.uploadFromFile(outputFile.getAbsolutePath());
		    	    BlobProperties props = blob.getProperties();
		    	    props.setContentType("application/pdf");
		    	    blob.uploadProperties();
		    	    xmloutput.close();
		    	    xsloutput.close();
		    	    pic1.close();
		    	    pic2.close();
		    	    xsltFile.deleteOnExit();
		    	    outputFile.deleteOnExit();
		    	    xmlFile.deleteOnExit();
		    	    picture1.deleteOnExit();
		    	    picture2.deleteOnExit();
	    			
	    	}
			
		 catch (FOPException | IOException | TransformerException e) {
		       e.printStackTrace();
		} 
				return blobFileName;
		   }
}
