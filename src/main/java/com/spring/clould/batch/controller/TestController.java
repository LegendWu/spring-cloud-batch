//package com.spring.clould.batch.controller;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.alibaba.fastjson.JSON;
//import com.citiccard.risk.legal.dao.tables.JobEntityMapper;
//import com.citiccard.risk.legal.model.tables.JobEntity;
//import com.citiccard.risk.legal.utils.POIUtils;
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//
//@RestController
//public class TestController {
//	
//	@Autowired
//	JobEntityMapper mapper;
//	
//    @RequestMapping("export")
//    public void export(HttpServletRequest request, HttpServletResponse response){
//        Map<String,Object> params = new HashMap<>();
//        params.put("title","这是标题");
//        params.put("name","李四");
//        //这里是我说的一行代码
//        POIUtils.exportWord("D:/word/template/export.docx","D:/word/result","result.docx",params,request,response);
//    }
//    
//    @RequestMapping("page")
//    public String page(){
//    	PageHelper.startPage(1, 1);
//    	List<JobEntity> list = mapper.selectAll();
//        PageInfo<JobEntity> pageInfo = new PageInfo<JobEntity>(list);
//        return "PageInfo: " + JSON.toJSONString(pageInfo) + ", Page: " + JSON.toJSONString(pageInfo);
//    }
//}
