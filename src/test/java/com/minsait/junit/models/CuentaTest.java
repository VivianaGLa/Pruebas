package com.minsait.junit.models;

import com.minsait.junit.exception.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {
    Cuenta cuenta;
    TestInfo testInfo;
    TestReporter testReporter;

    @BeforeEach
    void setUp(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Viviana", new BigDecimal("1000"));
        testReporter.publishEntry("Iniciando el metodo");
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        testReporter.publishEntry("Ejecutando" + testInfo.getTestMethod().orElse(null).getName());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el metodo de prueba");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Iniciando todos los test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando todos los test");
    }

    @Nested
    @DisplayName("Testea el nombre y el saldo")
    class CuentaNombreSaldoTest {

        @Test
        void testNombreCuenta() {
            String esperado = "Viviana";
            String real = cuenta.getPersona();
            assertNotNull(real);
            assertEquals(esperado, real);
        }

        @Test
        void testSaldoCuenta() {
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertEquals(1000, cuenta.getSaldo().intValue());
        }

        @Test
        void testReferencia() {
            Cuenta cuenta2 = new Cuenta("Viviana", new BigDecimal("1000"));
            assertEquals(cuenta2, cuenta);
        }
    }

    @Nested
    @DisplayName("Testea operaciones")
    class CuentaOperacionesTest {

        @Test
        void testRetirarCuenta() {
            cuenta.retirar(new BigDecimal(500));
            assertNotNull(cuenta.getSaldo());
            assertEquals(500, cuenta.getSaldo().doubleValue());
        }

        @Test
        void testDepositarCuenta() {
            cuenta.depositar(new BigDecimal(500));
            assertEquals(1500, cuenta.getSaldo().doubleValue());
        }

        @Test
        void testTransferirEntreCuentas() {
            Cuenta cuenta3 = new Cuenta("Bill Gates", new BigDecimal(10000));
            Banco banco = new Banco();
            banco.setNombre("BBVA");
            banco.transferir(cuenta3, cuenta, new BigDecimal(10000));
            assertEquals(11000, cuenta.getSaldo().intValue());
            assertEquals(0, cuenta3.getSaldo().doubleValue());
        }

        @Test
        void testException() {
            Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
                cuenta.retirar(new BigDecimal(1500));
            });
            assertEquals("Dinero insuficiente", exception.getMessage());
            assertTrue(exception instanceof DineroInsuficienteException);
            //nueva línea
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }

        @Test
        @DisplayName("Test relacion entre banco y cuentas")
        void testRelacionBancoCuentas() {
            Cuenta cuenta3 = new Cuenta("Bill Gates", new BigDecimal(10000));
            Banco banco = new Banco();
            banco.addCuenta(cuenta3);
            banco.addCuenta(cuenta);
            banco.setNombre("BBVA");
            banco.transferir(cuenta3, cuenta, new BigDecimal(9000));
            assertAll(
                    () -> assertEquals(10000, cuenta.getSaldo().doubleValue(), () -> "El saldo no es correcto"),
                    () -> assertEquals(1000, cuenta3.getSaldo().intValue()),
                    () -> assertEquals(2, banco.getCuentas().size()),
                    () -> assertEquals(banco.getNombre(), cuenta3.getBanco().getNombre()),
                    () -> assertEquals(banco.getNombre(), cuenta.getBanco().getNombre()),
                    () -> assertEquals(cuenta.getPersona(), banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Viviana"))
                            .findFirst()
                            .get().getPersona())
            );
        }
    }
}