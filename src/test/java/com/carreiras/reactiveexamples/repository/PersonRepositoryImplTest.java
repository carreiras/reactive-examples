package com.carreiras.reactiveexamples.repository;

import com.carreiras.reactiveexamples.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Created by Ewerton on 29-12-22
 */
class PersonRepositoryImplTest {

    PersonRepositoryImpl personRepository;

    @BeforeEach
    void setup() {
        personRepository = new PersonRepositoryImpl();
    }

    @Test
    void getByIdBlock() {
        Mono<Person> personMono = personRepository.getById(1);
        Person person = personMono.block();
        System.out.println(person.toString());
    }

    @Test
    void getByIdSubscribe() {
        Mono<Person> personMono = personRepository.getById(1);
        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void getByIdMapFunction() {
        Mono<Person> personMono = personRepository.getById(1);
        personMono.map(person -> {
            System.out.println(person.toString());
            return person.getFirstName();
        }).subscribe(firstName -> {
            System.out.println("from map: " + firstName);
        });
    }

    @Test
    void fluxTestBlockFirst() {
        Flux<Person> personFlux = personRepository.findAll();
        Person person = personFlux.blockFirst();
        System.out.println(person.toString());
    }

    @Test
    void testFluxSubscribe() {
        Flux<Person> personFlux = personRepository.findAll();
        personFlux.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFluxToListMono() {
        Flux<Person> personFlux = personRepository.findAll();
        Mono<List<Person>> personListMono = personFlux.collectList();
        personListMono.subscribe(list -> {
            list.forEach(person -> {
                System.out.println(person.toString());
            });
        });
    }

    @Test
    void testFindPersonbyId() {
        final Integer id = 3;
        Flux<Person> personFlux = personRepository.findAll();
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();
        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFindPersonbyIdNotFound() {
        final Integer id = 8;
        Flux<Person> personFlux = personRepository.findAll();
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();
        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFindPersonbyIdNotFoundWithException() {
        final Integer id = 8;
        Flux<Person> personFlux = personRepository.findAll();
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).single();
        personMono.doOnError(throwable -> {
                System.out.println("I went boom");
            })
            .onErrorReturn(Person.builder().id(id).build())
            .subscribe(person -> {
                System.out.println(person.toString());
            });
    }
}