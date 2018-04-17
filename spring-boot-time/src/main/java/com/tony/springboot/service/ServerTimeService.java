package com.tony.springboot.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.tony.domain.TimeVo;

@Service
public class ServerTimeService implements TimeService {

	public void populateTime(TimeVo timeVo) {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	    String myFormatter = localDateTime.format(formatter);
	    timeVo.setServerTime(myFormatter);
	}

}
