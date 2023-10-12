package com.example.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Dummy")
public class DummyTest {

    Dummy dummy;

    @BeforeEach
    void setup() {
        dummy = new Dummy();
    }

    @DisplayName("should say hello")
    @Test
    void sayHelloShouldReturnHello() {
        assertThat(dummy.sayHello()).isEqualTo("hello");
    }

}
