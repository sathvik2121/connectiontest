package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.TimeZone;

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
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.text.DateFormat;
import java.text.SimpleDateFormat;  
import java.util.Date;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

@RestController
@SpringBootApplication
public class PdfGeneration {
	public static void main(String[] args) throws InvalidKeyException, DocumentException, URISyntaxException, StorageException, IOException, ParserConfigurationException, SAXException  {
//		PdfGeneration ob= new PdfGeneration();
//		String s=ob.approverName("hi","data1_2022_12_14.pdf");
//		System.out.println(s);
		SpringApplication.run(PdfGeneration.class, args);
	}
@GetMapping("/")
	
	public String welcome()
	{
		
		return "welcome to pdf generation";
	}
	@GetMapping("/ContainerCheck")
	
	public List<String> fileNames() throws InvalidKeyException, URISyntaxException, StorageException, UnsupportedEncodingException, MalformedURLException
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
	
	public List<String> run(@RequestParam( name="fileName") String name) throws DocumentException, URISyntaxException, StorageException, InvalidKeyException, ParserConfigurationException, SAXException
	{
			final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=mqbawblobstorage01;AccountKey=4eEEA1jiy/kEpf9PvN8ikjQeXGFODXXH33G+VPhUiyhqzF7K7RrwFg/0CDEBJpkaYzWArR1bW2XD+AStaWP6zg==;EndpointSuffix=core.windows.net";
		    File xsltFile = null;
			CloudStorageAccount storageAccount;
			CloudBlobClient blobClient = null;
			String blobFileName=null;
			List<String> list=new ArrayList<String>();
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
		    	    list.add(blobFileName);
		    	    blob.uploadFromFile(outputFile.getAbsolutePath());
		    	    BlobProperties props = blob.getProperties();
		    	    props.setContentType("application/pdf");
		    	    blob.uploadProperties();
		    	    xmloutput.close();
		    	    xsloutput.close();
		    	    pic1.close();
		    	    pic2.close();
		    	    //File inputFile = new File(xmlFile.getAbsolutePath());
		            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		            Document document = dBuilder.parse(xmlFile);
		            document.getDocumentElement().normalize();
		           // System.out.println("Root element :" + document.getDocumentElement().getNodeName());
		            NodeList nList = document.getElementsByTagName("ContactDelivery");
		            Node nNode = nList.item(0);
		            Element eElement = (Element) nNode;
		            String siteID= eElement.getElementsByTagName("Company").item(0).getTextContent();
		            list.add(siteID);
		    	    xsltFile.deleteOnExit();
		    	    outputFile.deleteOnExit();
		    	    xmlFile.deleteOnExit();
		    	    picture1.deleteOnExit();
		    	    picture2.deleteOnExit();
	    			
	    	}
			
		 catch (FOPException | IOException | TransformerException e) {
		       e.printStackTrace();
		} 
				return list;
		   }
@GetMapping("/AuditTrail")
	
	public String update(@RequestParam( name="fileName") String fn,@RequestParam( name="reviewerName") String rn,@RequestParam( name="reviewerAction") String ra,@RequestParam( name="reviewerReason") String rr,@RequestParam( name="reviewDateTime") String rd,@RequestParam( name="approverName") String an,@RequestParam( name="approverAction") String aa,@RequestParam( name="approverReason") String ar,@RequestParam( name="approverDateTime") String ad) throws InvalidKeyException, URISyntaxException, StorageException, IOException
	{
		
		 final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=mqbawblobstorage01;AccountKey=4eEEA1jiy/kEpf9PvN8ikjQeXGFODXXH33G+VPhUiyhqzF7K7RrwFg/0CDEBJpkaYzWArR1bW2XD+AStaWP6zg==;EndpointSuffix=core.windows.net";
		 CloudStorageAccount storageAccount;
		 CloudBlobClient blobClient = null;
		 File auditFile ;
		 storageAccount = CloudStorageAccount.parse(storageConnectionString);
		 blobClient = storageAccount.createCloudBlobClient();
		 CloudBlobContainer container=blobClient.getContainerReference("logs");
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");  
 	     Date date = new Date();
		 CloudBlockBlob blob = container.getBlockBlobReference("AuditTrails"+"_"+formatter.format(date)+".xlsx");
		 auditFile = File.createTempFile("auditTrail", ".xlsx");
	
		
		 
if(blob.exists())
{
	FileOutputStream audit= new FileOutputStream(auditFile);
    blob.download(audit);
		try {
			
			FileInputStream fileInputStream = new FileInputStream(auditFile.getAbsolutePath());
			Workbook workbook = WorkbookFactory.create(fileInputStream);		
			Sheet sheet = workbook.getSheetAt(0);
			int lastRowCount = sheet.getLastRowNum();
			System.out.println(lastRowCount);
			Row dataRow = sheet.createRow(++lastRowCount);
	        	dataRow.createCell(0).setCellValue(fn);
	        	dataRow.createCell(1).setCellValue(rn);
	        	dataRow.createCell(2).setCellValue(ra);       	        
	        	dataRow.createCell(3).setCellValue(rr);
	        	dataRow.createCell(4).setCellValue(rd);
	        	dataRow.createCell(5).setCellValue(an);
	        	dataRow.createCell(6).setCellValue(aa);
	        	dataRow.createCell(7).setCellValue(ar);
	        	dataRow.createCell(8).setCellValue(ad);
			FileOutputStream fileOutputStream = new FileOutputStream(auditFile);
			workbook.write(fileOutputStream);
			fileOutputStream.close();
			
			
		 fileInputStream.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
}
		 
		 
		 else {
			 Workbook workbook1 = new XSSFWorkbook();	
				Sheet sheet = workbook1.createSheet();
				int lastRowCount = sheet.getLastRowNum();
				Row dataRow = sheet.createRow(lastRowCount);
				CellStyle style = workbook1.createCellStyle();  
				 style = workbook1.createCellStyle();  
		            style.setFillForegroundColor(IndexedColors.BLUE.getIndex());  
		            style.setFillPattern(FillPatternType.SOLID_FOREGROUND); 
		            Font font = workbook1.createFont();
		          
		            font.setBold(true);
		            font.setColor(IndexedColors.WHITE.getIndex());
		            style.setFont(font);
				System.out.println("lastRowCount.. "  +  lastRowCount);
				dataRow.createCell(0).setCellValue("filename");
		        	dataRow.createCell(1).setCellValue("reviewername");
		        	dataRow.createCell(2).setCellValue("revieweraction");       	        
		        	dataRow.createCell(3).setCellValue("reviewerreason");
		        	dataRow.createCell(4).setCellValue("reviewdate");
		        	dataRow.createCell(5).setCellValue("Approverrname");
		        	dataRow.createCell(6).setCellValue("Approveraction");
		        	dataRow.createCell(7).setCellValue("approverreason");
		        	dataRow.createCell(8).setCellValue("Approvedate");
		        	dataRow.getCell(0).setCellStyle(style);
		        	dataRow.getCell(1).setCellStyle(style);
		        	dataRow.getCell(2).setCellStyle(style);
		        	dataRow.getCell(3).setCellStyle(style);
		        	dataRow.getCell(4).setCellStyle(style);
		        	dataRow.getCell(5).setCellStyle(style);
		        	dataRow.getCell(6).setCellStyle(style);
		        	dataRow.getCell(7).setCellStyle(style);
		        	dataRow.getCell(8).setCellStyle(style);
		        	Row nextRow = sheet.createRow(++lastRowCount);
		        	nextRow.createCell(0).setCellValue(fn);
		        	nextRow.createCell(1).setCellValue(rn);
		        	nextRow.createCell(2).setCellValue(ra);       	        
		        	nextRow.createCell(3).setCellValue(rr);
		        	nextRow.createCell(4).setCellValue(rd);
		        	nextRow.createCell(5).setCellValue(an);
		        	nextRow.createCell(6).setCellValue(aa);
		        	nextRow.createCell(7).setCellValue(ar);
		        	nextRow.createCell(8).setCellValue(ad);
		        	
				
				System.out.println("lastRowCount after excel sheet modified.. "  +  lastRowCount);
				
				
				FileOutputStream fileOutputStream = new FileOutputStream(auditFile.getAbsolutePath());
				workbook1.write(fileOutputStream);
				fileOutputStream.close();
				System.out.println("excel sheet updated successfully........");
				
				workbook1.close();
		 }
         blob.uploadFromFile(auditFile.getAbsolutePath());
         auditFile.deleteOnExit();

		return "successfull";
	}

@GetMapping("/sign")
public String approverName(@RequestParam( name="approverName") String approverName,@RequestParam( name="fileName") String fileName) throws InvalidPasswordException, IOException, URISyntaxException, StorageException, InvalidKeyException
{
	final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=mqbawblobstorage01;AccountKey=4eEEA1jiy/kEpf9PvN8ikjQeXGFODXXH33G+VPhUiyhqzF7K7RrwFg/0CDEBJpkaYzWArR1bW2XD+AStaWP6zg==;EndpointSuffix=core.windows.net";
	CloudStorageAccount storageAccount;
	CloudBlobClient blobClient = null;
	CloudBlobContainer container=null;
	String encode = null;
	String encodedFileName = null;
	try {    
		storageAccount = CloudStorageAccount.parse(storageConnectionString);
		blobClient = storageAccount.createCloudBlobClient();
		container = blobClient.getContainerReference("pdffiles");
		CloudBlobContainer buffercontainer=blobClient.getContainerReference("buffercontainer");
		CloudBlobContainer xmlcontainer=blobClient.getContainerReference("xmlcontainer");
		CloudBlobContainer encodecontainer=blobClient.getContainerReference("encodedfiledata");
		File finalFile=File.createTempFile("final", ".pdf");
        File outputFile=File.createTempFile("output", ".pdf");
        File encodeFile=File.createTempFile("data",".txt");
        CloudBlockBlob blob2 = buffercontainer.getBlockBlobReference(fileName);
        FileOutputStream pdfOutput= new FileOutputStream(outputFile);
        blob2.download(pdfOutput);
	    PDDocument document = PDDocument.load(outputFile);
        PDPage page = document.getPage(4);
        PDPageContentStream contentStream = new PDPageContentStream(document, page,PDPageContentStream.AppendMode.APPEND,true,true);
        contentStream.beginText(); 
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.newLineAtOffset(73, 355);
        Date date = new Date();  
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
        formatter.setTimeZone(TimeZone.getTimeZone("EST"));
        String text = "Approved by "+approverName;
        contentStream.showText(text); 
        contentStream.newLineAtOffset(0,-13);
        String text1 =formatter.format(date);
        contentStream.showText(text1); 
        contentStream.endText();
        contentStream.close();
        document.save(finalFile);
        document.close();
        String fileNameWithOutExt = FilenameUtils.removeExtension(fileName);
        CloudBlockBlob blob = container.getBlockBlobReference(fileNameWithOutExt+".pdf");
        blob.uploadFromFile(finalFile.getAbsolutePath());
        byte[] b= Files.readAllBytes(finalFile.toPath());
        encode=Base64.getEncoder().encodeToString(b);
        try (PrintWriter out = new PrintWriter(encodeFile)) {
            out.println(encode);
        }
       
       CloudBlockBlob encodeblob = encodecontainer.getBlockBlobReference(fileNameWithOutExt+".txt");
       encodedFileName=fileNameWithOutExt+".txt";
       encodeblob.uploadFromFile(encodeFile.getAbsolutePath());
       CloudBlockBlob xmlblob = xmlcontainer.getBlockBlobReference(fileNameWithOutExt.substring(0,fileNameWithOutExt.length()-11)+".xml");
       CloudBlockBlob bufferblob = buffercontainer.getBlockBlobReference(fileName);
       pdfOutput.close();
       finalFile.deleteOnExit();
       outputFile.deleteOnExit();
       encodeFile.deleteOnExit();
       blob2.deleteIfExists();
       xmlblob.deleteIfExists();
       bufferblob.deleteIfExists();
      
   }
	catch (IOException e) {
	       e.printStackTrace();
		}
	return encodedFileName;
}
}
