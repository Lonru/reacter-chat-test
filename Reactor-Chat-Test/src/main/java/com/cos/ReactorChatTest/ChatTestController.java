package com.cos.ReactorChatTest;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@CrossOrigin
@RestController
public class ChatTestController {
	Sinks.Many<String> sink;

	public ChatTestController() {
		this.sink = Sinks.many().multicast().onBackpressureBuffer();
	}

	@CrossOrigin
	@PostMapping("/send")
	public void send(@RequestBody ChatDto chatDto) {
		sink.tryEmitNext("["+chatDto.getUsername()+ "] "+chatDto.getMsg());
	}

	// data : 실제값 /n/n
	@GetMapping(value = "/sse")
	public Flux<ServerSentEvent<String>> sse() {
		return sink.asFlux().map(e -> ServerSentEvent.builder(e).build()).doOnCancel(()->{
			System.out.println("SSE 종료됨");
			sink.asFlux().blockLast();
		}); 
	}

}
