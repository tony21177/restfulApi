package com.tony.springboot.service;

import java.time.Instant;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.tony.domain.TimeVo;

@Service
public class UnixTimeService implements TimeService {

	public void populateTime(TimeVo timeVo) {
		long unixTime=Instant.now().getEpochSecond();
		timeVo.setUnixTime(Long.toString(unixTime));
	}

}
