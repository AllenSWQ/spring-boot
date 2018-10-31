package com.allen.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.allen.service.UserService;

import ch.qos.logback.classic.Logger;

@Controller
@EnableAutoConfiguration
public class UserController {
	@Autowired
	UserService userServiceImpl;

	private Logger log = (Logger) LoggerFactory.getLogger(this.getClass());
	private Logger errorLog = (Logger) LoggerFactory.getLogger("error");
	
	@RequestMapping("/api")
	@ResponseBody
	public JSONObject apiApp(HttpServletRequest request) {
		JSONObject jsonObj = new JSONObject();
		
		String key = request.getParameter("key");
		log.info("check key=" + key);
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			jsonObj.put("status", 0);
			errorLog.info("return json = " + jsonObj.toString());
			return jsonObj;
		}
		return jsonObj;
	}
}
