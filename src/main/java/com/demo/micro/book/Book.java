package com.demo.micro.book;

import lombok.*;

import javax.persistence.*;

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
