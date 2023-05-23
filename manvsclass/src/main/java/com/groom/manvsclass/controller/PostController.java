package com.groom.manvsclass.controller;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.groom.manvsclass.api.upload.FileUploadResponse;
import com.groom.manvsclass.api.upload.FileUploadUtil;
import com.groom.manvsclass.api.download.FileDownloadUtil;
import com.groom.manvsclass.model.ClassUT;
import com.groom.manvsclass.repository.ClassRepository;

import com.groom.manvsclass.repository.SearchRepositoryImpl;


import com.fasterxml.jackson.databind.ObjectMapper;
//@Controller
@RestController
public class PostController {
	
	@Autowired
	ClassRepository repo;
	
	private final LocalDate today = LocalDate.now();
	private final SearchRepositoryImpl srepo;
	
	public PostController(SearchRepositoryImpl srepo)
	{
		this.srepo=srepo;
	}
	
	@GetMapping("/home")
	public	List<ClassUT>	elencaClassi()
	{
		return repo.findAll();
	}
	
	@GetMapping("/classut")
	public	List<ClassUT>	elenscaClassi()
	{
		return repo.findAll();
	}

	
	
	@PostMapping("/uploadFile")
	public ResponseEntity<FileUploadResponse> aggiuntaClasse(@RequestParam("file") MultipartFile multipartFile,@RequestParam("model") String model) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		ClassUT classe = mapper.readValue(model, ClassUT.class);
		
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		long size = multipartFile.getSize();
		
		FileUploadUtil.saveCLassFile(fileName,classe.getName() ,multipartFile);
		
		FileUploadResponse response = new FileUploadResponse();
		response.setFileName(fileName);
		response.setSize(size);
		response.setDownloadUri("/downloadFile");
		
		classe.setUri("Files-Upload/"+classe.getName()+"/"+fileName);
		classe.setDate(today.toString());
		// generaTest_Randoop("Files-Upload/"+classe.getName()+"/"+fileName);
		// generaTest_Evosuite("Files-Upload/"+classe.getName()+"/"+fileName);
		repo.save(classe);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	
	@GetMapping("/home/{text}")
	public	List<ClassUT>	ricercaClasse(@PathVariable String text)
	{
		return srepo.findByText(text);
	}
	
	
	
	@PostMapping("/insert")
	public ClassUT UploadClasse(@RequestBody ClassUT classe)
	{
		return repo.save(classe);
	}
	
	
	
	@GetMapping("/filterby/{text}/{category}")
	public	List<ClassUT>	elencaClasse(@PathVariable String text,@PathVariable String category)
	{
		return srepo.searchAndFilter(text,category);
	}

	@GetMapping("filterby/{category}")
	public List<ClassUT> elencaClasse(@PathVariable String category)
	{
		return srepo.filterByCategory(category);
	}
	
	 @GetMapping("/downloadFile/{name}")
	    public ResponseEntity<?> downloadClasse(@PathVariable("name") String name) throws Exception {
		 	   List<ClassUT> classe= srepo.findByText(name);
	           return FileDownloadUtil.downloadClassFile(classe.get(0).getcode_Uri());
	    }
	 
	 
	
	     
	
	 
	
	/*
	@PostMapping("/uploadFile")
	public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException
	{
		
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		long size = multipartFile.getSize();
		
		FileUploadUtil.saveFile(fileName, multipartFile);
		
		FileUploadResponse response = new FileUploadResponse();
		response.setFileName(fileName);
		response.setSize(size);
		response.setDownloadUri("/downloadFile");
		return new ResponseEntity<>(response,HttpStatus.OK);
	}*/
	
}
