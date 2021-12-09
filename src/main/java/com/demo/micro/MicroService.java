package com.demo.micro;

import org.springframework.stereotype.Service;

@Service
public record MicroService(MicroRepository microRepository) {

	public void register(MicroRequest microRequest) {
		Micro micro = Micro.builder().firstName(microRequest.firstName()).build();
		microRepository.save(micro);

	}

}
