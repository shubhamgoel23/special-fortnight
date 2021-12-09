package com.demo.micro.book;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Book {

	@Id
	@SequenceGenerator(name = "micro_id_sequence", sequenceName = "micro_id_sequence")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "micro_id_sequence")
	private Integer id;
	private String firstName;
}
