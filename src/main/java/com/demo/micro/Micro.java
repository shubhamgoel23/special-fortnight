package com.demo.micro;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Micro {

	@Id
	@SequenceGenerator(name = "micro_id_sequence", sequenceName = "micro_id_sequence")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "micro_id_sequence")
	private Integer id;
	private String firstName;
}
